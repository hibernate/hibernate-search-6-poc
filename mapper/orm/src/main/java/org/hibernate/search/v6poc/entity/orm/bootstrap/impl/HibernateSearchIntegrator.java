/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.search.v6poc.entity.orm.bootstrap.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.cfg.spi.ConfigurationProperty;
import org.hibernate.search.v6poc.cfg.spi.UnusedPropertyTrackingConfigurationPropertySource;
import org.hibernate.search.v6poc.entity.orm.cfg.IndexingStrategyConfiguration;
import org.hibernate.search.v6poc.entity.orm.cfg.SearchOrmSettings;
import org.hibernate.search.v6poc.entity.orm.cfg.impl.HibernateOrmConfigurationPropertySource;
import org.hibernate.search.v6poc.entity.orm.event.impl.FullTextIndexEventListener;
import org.hibernate.search.v6poc.entity.orm.logging.impl.Log;
import org.hibernate.search.v6poc.entity.orm.spi.EnvironmentSynchronizer;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * Integrates Hibernate Search into Hibernate Core by registering its needed listeners
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Steve Ebersole
 */
public class HibernateSearchIntegrator implements Integrator {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final ConfigurationProperty<Boolean> AUTOREGISTER_LISTENERS =
			ConfigurationProperty.forKey( SearchOrmSettings.Radicals.AUTOREGISTER_LISTENERS )
					.asBoolean()
					.withDefault( SearchOrmSettings.Defaults.AUTOREGISTER_LISTENERS )
					.build();

	private static final ConfigurationProperty<IndexingStrategyConfiguration> INDEXING_MODE =
			ConfigurationProperty.forKey( SearchOrmSettings.Radicals.INDEXING_STRATEGY )
					.as( IndexingStrategyConfiguration.class, IndexingStrategyConfiguration::fromExternalRepresentation )
					.withDefault( SearchOrmSettings.Defaults.INDEXING_STRATEGY )
					.build();

	private static final ConfigurationProperty<Boolean> DIRTY_PROCESSING_ENABLED =
			ConfigurationProperty.forKey( SearchOrmSettings.Radicals.ENABLE_DIRTY_CHECK )
					.asBoolean()
					.withDefault( SearchOrmSettings.Defaults.ENABLE_DIRTY_CHECK )
					.build();

	private static final ConfigurationProperty<Boolean> ENABLE_CONFIGURATION_PROPERTY_TRACKING =
			// We don't use the radical here, but the full property key: the property is retrieved before we apply the mask
			ConfigurationProperty.forKey( SearchOrmSettings.ENABLE_CONFIGURATION_PROPERTY_TRACKING )
					.asBoolean()
					.withDefault( SearchOrmSettings.Defaults.ENABLE_CONFIGURATION_PROPERTY_TRACKING )
					.build();

	@Override
	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		ConfigurationService configurationService = serviceRegistry.getService( ConfigurationService.class );
		HibernateOrmConfigurationPropertySource hibernateOrmPropertySource =
				new HibernateOrmConfigurationPropertySource( configurationService );
		UnusedPropertyTrackingConfigurationPropertySource unusedPropertyTrackingPropertySource;
		ConfigurationPropertySource unmaskedPropertySource;
		if ( ENABLE_CONFIGURATION_PROPERTY_TRACKING.get( hibernateOrmPropertySource ) ) {
			unusedPropertyTrackingPropertySource = hibernateOrmPropertySource.withUnusedPropertyTracking();
			// Make sure to mark the "enable configuration property tracking" property as used
			ENABLE_CONFIGURATION_PROPERTY_TRACKING.get( unusedPropertyTrackingPropertySource );
			unmaskedPropertySource = unusedPropertyTrackingPropertySource;
		}
		else {
			log.configurationPropertyTrackingDisabled();
			unusedPropertyTrackingPropertySource = null;
			unmaskedPropertySource = hibernateOrmPropertySource;
		}

		/*
		 * Only apply the mask after we added support for unused property tracking,
		 * because that tracking must work on the user's keys without a mask,
		 * so as to report unused keys exactly as they were provided by the user.
		 */
		ConfigurationPropertySource propertySource = unmaskedPropertySource.withMask( "hibernate.search" );
		JndiService namingService = serviceRegistry.getService( JndiService.class );

		if ( ! AUTOREGISTER_LISTENERS.get( propertySource ) ) {
			log.debug( "Skipping Hibernate Search event listener auto registration" );
			return;
		}

		FullTextIndexEventListener fullTextIndexEventListener = new FullTextIndexEventListener(
				IndexingStrategyConfiguration.EVENT.equals( INDEXING_MODE.get( propertySource ) ),
				DIRTY_PROCESSING_ENABLED.get( propertySource )
		);
		registerHibernateSearchEventListener( fullTextIndexEventListener, serviceRegistry );

		ClassLoaderService hibernateOrmClassLoaderService = serviceRegistry.getService( ClassLoaderService.class );
		ServiceBinding<EnvironmentSynchronizer> environmentSynchronizerBinding =
				serviceRegistry.locateServiceBinding( EnvironmentSynchronizer.class );
		ServiceBinding<ManagedBeanRegistry> managedBeanRegistryServiceBinding =
				serviceRegistry.locateServiceBinding( ManagedBeanRegistry.class );
		HibernateSearchSessionFactoryObserver observer = new HibernateSearchSessionFactoryObserver(
				metadata,
				propertySource, unusedPropertyTrackingPropertySource,
				fullTextIndexEventListener,
				hibernateOrmClassLoaderService,
				environmentSynchronizerBinding == null ? null : serviceRegistry.getService( EnvironmentSynchronizer.class ),
				managedBeanRegistryServiceBinding == null ? null : serviceRegistry.getService( ManagedBeanRegistry.class ),
				namingService
		);

		sessionFactory.addObserver( observer );
	}

	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		// Nothing to do, Hibernate Search shuts down automatically when the SessionFactory is closed
	}

	private void registerHibernateSearchEventListener(FullTextIndexEventListener eventListener, SessionFactoryServiceRegistry serviceRegistry) {
		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
		listenerRegistry.addDuplicationStrategy( new DuplicationStrategyImpl( FullTextIndexEventListener.class ) );

		listenerRegistry.appendListeners( EventType.POST_INSERT, eventListener );
		listenerRegistry.appendListeners( EventType.POST_UPDATE, eventListener );
		listenerRegistry.appendListeners( EventType.POST_DELETE, eventListener );
		listenerRegistry.appendListeners( EventType.POST_COLLECTION_RECREATE, eventListener );
		listenerRegistry.appendListeners( EventType.POST_COLLECTION_REMOVE, eventListener );
		listenerRegistry.appendListeners( EventType.POST_COLLECTION_UPDATE, eventListener );
		listenerRegistry.appendListeners( EventType.FLUSH, eventListener );
	}

	public static class DuplicationStrategyImpl implements DuplicationStrategy {
		private final Class checkClass;

		public DuplicationStrategyImpl(Class checkClass) {
			this.checkClass = checkClass;
		}

		@Override
		public boolean areMatch(Object listener, Object original) {
			// not isAssignableFrom since the user could subclass
			return checkClass == original.getClass() && checkClass == listener.getClass();
		}

		@Override
		public Action getAction() {
			return Action.KEEP_ORIGINAL;
		}
	}

}
