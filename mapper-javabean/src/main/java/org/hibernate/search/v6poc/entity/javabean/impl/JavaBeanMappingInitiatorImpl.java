/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.javabean.impl;

import java.util.Set;

import org.hibernate.search.v6poc.engine.SearchMappingRepositoryBuilder;
import org.hibernate.search.v6poc.entity.javabean.JavaBeanMapping;
import org.hibernate.search.v6poc.entity.javabean.JavaBeanMappingInitiator;
import org.hibernate.search.v6poc.entity.javabean.mapping.impl.JavaBeanMappingFactory;
import org.hibernate.search.v6poc.entity.javabean.mapping.impl.JavaBeanMappingKey;
import org.hibernate.search.v6poc.entity.javabean.model.impl.JavaBeanBootstrapIntrospector;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoMappingInitiatorImpl;

public class JavaBeanMappingInitiatorImpl extends PojoMappingInitiatorImpl<JavaBeanMapping>
		implements JavaBeanMappingInitiator {

	private final JavaBeanTypeConfigurationContributor typeConfigurationContributor;

	public JavaBeanMappingInitiatorImpl(SearchMappingRepositoryBuilder mappingRepositoryBuilder,
			JavaBeanBootstrapIntrospector introspector,
			boolean annotatedTypeDiscoveryEnabled, boolean multiTenancyEnabled) {
		super(
				mappingRepositoryBuilder, new JavaBeanMappingKey(),
				new JavaBeanMappingFactory(),
				introspector, false,
				multiTenancyEnabled
		);
		if ( annotatedTypeDiscoveryEnabled ) {
			enableAnnotatedTypeDiscovery();
		}
		typeConfigurationContributor = new JavaBeanTypeConfigurationContributor( introspector );
		addConfigurationContributor( typeConfigurationContributor );
	}

	@Override
	public void addEntityType(Class<?> type) {
		typeConfigurationContributor.addEntityType( type );
	}

	@Override
	public void addEntityTypes(Set<Class<?>> types) {
		for ( Class<?> type : types ) {
			addEntityType( type );
		}
	}
}
