/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.bootstrap.impl;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.cfg.spi.ConfigurationProperty;
import org.hibernate.search.v6poc.cfg.spi.UnusedPropertyTrackingConfigurationPropertySource;
import org.hibernate.search.v6poc.engine.SearchMappingRepository;
import org.hibernate.search.v6poc.engine.SearchMappingRepositoryBuilder;
import org.hibernate.search.v6poc.engine.spi.BeanResolver;
import org.hibernate.search.v6poc.engine.spi.ReflectionBeanResolver;
import org.hibernate.search.v6poc.entity.orm.cfg.SearchOrmSettings;
import org.hibernate.search.v6poc.entity.orm.event.impl.FullTextIndexEventListener;
import org.hibernate.search.v6poc.entity.orm.impl.HibernateSearchContextService;
import org.hibernate.search.v6poc.entity.orm.logging.impl.Log;
import org.hibernate.search.v6poc.entity.orm.mapping.HibernateOrmMapping;
import org.hibernate.search.v6poc.entity.orm.mapping.impl.HibernateOrmMappingInitiator;
import org.hibernate.search.v6poc.entity.orm.spi.EnvironmentSynchronizer;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.AnnotationMappingDefinition;
import org.hibernate.search.v6poc.util.impl.common.Closer;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;
import org.hibernate.search.v6poc.util.impl.common.SuppressingCloser;

/**
 * A {@code SessionFactoryObserver} registered with Hibernate ORM during the integration phase.
 * This observer will initialize Hibernate Search once the {@code SessionFactory} is built.
 *
 * @author Hardy Ferentschik
 * @see HibernateSearchIntegrator
 */
public class HibernateSearchSessionFactoryObserver implements SessionFactoryObserver {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final ConfigurationProperty<Boolean> ENABLE_ANNOTATION_MAPPING =
			ConfigurationProperty.forKey( SearchOrmSettings.Radicals.ENABLE_ANNOTATION_MAPPING )
					.asBoolean()
					.withDefault( SearchOrmSettings.Defaults.ENABLE_ANNOTATION_MAPPING )
					.build();

	private final ConfigurationPropertySource propertySource;
	private final UnusedPropertyTrackingConfigurationPropertySource unusedPropertyTrackingPropertySource;
	private final JndiService namingService;
	private final ClassLoaderService classLoaderService;
	private final EnvironmentSynchronizer environmentSynchronizer;
	private final ManagedBeanRegistry managedBeanRegistry;
	private final FullTextIndexEventListener listener;
	private final Metadata metadata;

	private final CompletableFuture<HibernateSearchContextService> contextFuture = new CompletableFuture<>();
	private final CompletableFuture<?> closingTrigger = new CompletableFuture<>();


	//Guarded by synchronization on this
	// TODO JMX
//	private JMXHook jmx;

	HibernateSearchSessionFactoryObserver(
			Metadata metadata,
			ConfigurationPropertySource propertySource,
			UnusedPropertyTrackingConfigurationPropertySource unusedPropertyTrackingPropertySource,
			FullTextIndexEventListener listener,
			ClassLoaderService classLoaderService,
			EnvironmentSynchronizer environmentSynchronizer,
			ManagedBeanRegistry managedBeanRegistry,
			JndiService namingService) {
		this.metadata = metadata;
		this.propertySource = propertySource;
		this.unusedPropertyTrackingPropertySource = unusedPropertyTrackingPropertySource;
		this.listener = listener;
		this.classLoaderService = classLoaderService;
		this.environmentSynchronizer = environmentSynchronizer;
		this.managedBeanRegistry = managedBeanRegistry;
		this.namingService = namingService;

		/*
		 * Make sure that if a Search integrator is created, it will eventually get closed,
		 * either when the environment is destroyed (see the use of EnvironmentSynchronizer in #sessionFactoryCreated)
		 * or when the session factory is closed (see #sessionFactoryClosed),
		 * whichever happens first.
		 */
		contextFuture.thenAcceptBoth( closingTrigger,
				(context, ignored) -> this.cleanup( context ) );
	}

	@Override
	public void sessionFactoryCreated(SessionFactory factory) {
		boolean failedBootScheduling = true;
		try {
			SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) factory;
			listener.initialize( contextFuture );

			if ( environmentSynchronizer != null ) {
				environmentSynchronizer.whenEnvironmentDestroying( () -> {
					// Trigger integrator closing if the integrator actually exists and wasn't already closed
					closingTrigger.complete( null );
				} );
				environmentSynchronizer.whenEnvironmentReady( () -> boot( sessionFactoryImplementor ) );
			}
			else {
				boot( sessionFactoryImplementor );
			}

			failedBootScheduling = false;
		}
		finally {
			if ( failedBootScheduling ) {
				cancelBoot();
			}
		}
	}

	/**
	 * Boot Hibernate Search if it hasn't booted already,
	 * and complete {@link #contextFuture}.
	 * <p>
	 * This method is synchronized in order to avoid booting Hibernate Search
	 * after (or while) the boot has been canceled.
	 *
	 * @param sessionFactoryImplementor The factory on which to graft Hibernate Search.
	 *
	 * @see #cancelBoot()
	 */
	private synchronized void boot(SessionFactoryImplementor sessionFactoryImplementor) {
		if ( contextFuture.isDone() ) {
			return;
		}
		BeanResolver beanResolver = null;
		try {
			SearchMappingRepositoryBuilder builder = SearchMappingRepository.builder( propertySource );

			boolean enableAnnotationMapping = ENABLE_ANNOTATION_MAPPING.get( propertySource );

			HibernateOrmMappingInitiator mappingInitiator = HibernateOrmMappingInitiator.create(
					builder, metadata, sessionFactoryImplementor, enableAnnotationMapping
			);

			if ( managedBeanRegistry != null ) {
				BeanContainer beanContainer = managedBeanRegistry.getBeanContainer();
				if ( beanContainer != null ) {
					// Only use the primary registry, so that we can implement our own fallback when beans are not found
					beanResolver = new HibernateOrmBeanContainerBeanResolver( beanContainer );
				}
				// else: The given ManagedBeanRegistry only implements fallback: let's ignore it
			}
			if ( beanResolver == null ) {
				beanResolver = new ReflectionBeanResolver();
			}
			builder.setBeanResolver( beanResolver );

			if ( enableAnnotationMapping ) {
				AnnotationMappingDefinition annotationMapping = mappingInitiator.annotationMapping();
				metadata.getEntityBindings().stream()
						.map( PersistentClass::getMappedClass )
						// getMappedClass() can return null, which should be ignored
						.filter( Objects::nonNull )
						.forEach( annotationMapping::add );
			}

			// TODO namingService (JMX)
			// TODO ClassLoaderService

			SearchMappingRepository mappingRepository = builder.build();
			HibernateOrmMapping mapping = mappingInitiator.getResult();

			// TODO JMX
//			this.jmx = new JMXHook( propertySource );
//			this.jmx.registerIfEnabled( extendedIntegrator, factory );

			//Register the SearchFactory in the ORM ServiceRegistry (for convenience of lookup)
			HibernateSearchContextService contextService =
					sessionFactoryImplementor.getServiceRegistry().getService( HibernateSearchContextService.class );
			contextService.initialize( mappingRepository, mapping );
			contextFuture.complete( contextService );

			if ( unusedPropertyTrackingPropertySource != null ) {
				Set<String> unusedPropertyKeys = unusedPropertyTrackingPropertySource.getUnusedPropertyKeys();
				if ( !unusedPropertyKeys.isEmpty() ) {
					log.configurationPropertyTrackingUnusedProperties( unusedPropertyKeys );
				}
			}
		}
		catch (RuntimeException e) {
			new SuppressingCloser( e ).push( BeanResolver::close, beanResolver );

			contextFuture.completeExceptionally( e );
			// This will make the SessionFactory abort and close itself
			throw e;
		}
	}

	@Override
	public synchronized void sessionFactoryClosing(SessionFactory factory) {
		cancelBoot();
	}

	/**
	 * Cancel the planned boot if it hasn't happened already.
	 * <p>
	 * This method is synchronized in order to avoid canceling the boot while it is ongoing,
	 * which could lead to resource leaks.
	 *
	 * @see #boot(SessionFactoryImplementor)
	 */
	private synchronized void cancelBoot() {
		contextFuture.cancel( false );
	}

	@Override
	public void sessionFactoryClosed(SessionFactory factory) {
		/*
		 * Trigger integrator closing if the integrator actually exists and wasn't already closed
		 * The closing might have been triggered already if an EnvironmentSynchronizer is being used
		 * (see #sessionFactoryCreated).
		 */
		closingTrigger.complete( null );
	}

	private synchronized void cleanup(HibernateSearchContextService context) {
		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.push( c -> c.getMappingRepository().close(), context );
			// TODO JMX
			// closer.push( JMXHook::unRegisterIfRegistered, jmx );
		}
	}

}

