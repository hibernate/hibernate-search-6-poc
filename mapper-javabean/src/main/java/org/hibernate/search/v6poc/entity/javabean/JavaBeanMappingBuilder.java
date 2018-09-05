/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.javabean;

import java.lang.invoke.MethodHandles;
import java.util.Set;

import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.engine.spi.SearchMappingRepository;
import org.hibernate.search.v6poc.engine.spi.SearchMappingRepositoryBuilder;
import org.hibernate.search.v6poc.entity.javabean.impl.JavaBeanMappingInitiatorImpl;
import org.hibernate.search.v6poc.entity.javabean.mapping.impl.JavaBeanMappingImpl;
import org.hibernate.search.v6poc.entity.javabean.mapping.impl.JavaBeanMappingKey;
import org.hibernate.search.v6poc.entity.javabean.model.impl.JavaBeanBootstrapIntrospector;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.AnnotationMappingDefinition;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.ProgrammaticMappingDefinition;
import org.hibernate.search.v6poc.util.impl.common.SuppressingCloser;

public final class JavaBeanMappingBuilder {

	private final SearchMappingRepositoryBuilder mappingRepositoryBuilder;
	private final JavaBeanMappingKey mappingKey;
	private final JavaBeanMappingInitiatorImpl mappingInitiator;

	JavaBeanMappingBuilder(ConfigurationPropertySource propertySource, MethodHandles.Lookup lookup) {
		mappingRepositoryBuilder = SearchMappingRepository.builder( propertySource );
		JavaBeanBootstrapIntrospector introspector = new JavaBeanBootstrapIntrospector( lookup );
		mappingKey = new JavaBeanMappingKey();
		mappingInitiator = new JavaBeanMappingInitiatorImpl( introspector );
		mappingRepositoryBuilder.addMappingInitiator( mappingKey, mappingInitiator );
		// Enable annotated type discovery by default
		mappingInitiator.setAnnotatedTypeDiscoveryEnabled( true );
	}

	public ProgrammaticMappingDefinition programmaticMapping() {
		return mappingInitiator.programmaticMapping();
	}

	public AnnotationMappingDefinition annotationMapping() {
		return mappingInitiator.annotationMapping();
	}

	/**
	 * @param type The type to be considered as an entity type, i.e. a type that may be indexed
	 * and whose instances be added/updated/deleted through the {@link org.hibernate.search.v6poc.entity.pojo.mapping.PojoWorkPlan}.
	 */
	public JavaBeanMappingBuilder addEntityType(Class<?> type) {
		mappingInitiator.addEntityType( type );
		return this;
	}

	/**
	 * @param types The types to be considered as entity types, i.e. types that may be indexed
	 * and whose instances be added/updated/deleted through the {@link org.hibernate.search.v6poc.entity.pojo.mapping.PojoWorkPlan}.
	 */
	public JavaBeanMappingBuilder addEntityTypes(Set<Class<?>> types) {
		for ( Class<?> type : types ) {
			addEntityType( type );
		}
		return this;
	}

	public JavaBeanMappingBuilder setMultiTenancyEnabled(boolean multiTenancyEnabled) {
		mappingInitiator.setMultiTenancyEnabled( multiTenancyEnabled );
		return this;
	}

	public JavaBeanMappingBuilder setAnnotatedTypeDiscoveryEnabled(boolean annotatedTypeDiscoveryEnabled) {
		mappingInitiator.setAnnotatedTypeDiscoveryEnabled( annotatedTypeDiscoveryEnabled );
		return this;
	}

	public JavaBeanMappingBuilder setProperty(String name, String value) {
		mappingRepositoryBuilder.setProperty( name, value );
		return this;
	}

	public CloseableJavaBeanMapping build() {
		SearchMappingRepository mappingRepository = mappingRepositoryBuilder.build();
		try {
			JavaBeanMapping mapping = mappingRepository.getMapping( mappingKey );

			/*
			 * Since the user doesn't have access to the mapping repository, but only to the (closeable) mapping,
			 * make sure to close the mapping repository whenever the mapping is closed by the user.
			 */
			JavaBeanMappingImpl mappingImpl = (JavaBeanMappingImpl) mapping;
			mappingImpl.onClose( mappingRepository::close );
			return mappingImpl;
		}
		catch (RuntimeException e) {
			new SuppressingCloser( e ).push( mappingRepository );
			throw e;
		}
	}
}
