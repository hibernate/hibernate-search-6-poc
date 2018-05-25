/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.javabean;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.engine.SearchMappingRepositoryBuilder;
import org.hibernate.search.v6poc.entity.javabean.impl.JavaBeanMappingInitiatorImpl;
import org.hibernate.search.v6poc.entity.javabean.model.impl.JavaBeanBootstrapIntrospector;
import org.hibernate.search.v6poc.entity.pojo.mapping.PojoMappingInitiator;

public interface JavaBeanMappingInitiator extends PojoMappingInitiator<JavaBeanMapping> {

	static JavaBeanMappingInitiator create(SearchMappingRepositoryBuilder mappingRepositoryBuilder) {
		return create( mappingRepositoryBuilder, true );
	}

	static JavaBeanMappingInitiator create(SearchMappingRepositoryBuilder mappingRepositoryBuilder,
			boolean annotatedTypeDiscoveryEnabled) {
		return create( mappingRepositoryBuilder, MethodHandles.publicLookup(), annotatedTypeDiscoveryEnabled, false );
	}

	static JavaBeanMappingInitiator create(SearchMappingRepositoryBuilder mappingRepositoryBuilder,
			boolean annotatedTypeDiscoveryEnabled, boolean multiTenancyEnabled) {
		return create(
				mappingRepositoryBuilder, MethodHandles.publicLookup(),
				annotatedTypeDiscoveryEnabled, multiTenancyEnabled
		);
	}

	static JavaBeanMappingInitiator create(SearchMappingRepositoryBuilder mappingRepositoryBuilder,
			MethodHandles.Lookup lookup, boolean annotatedTypeDiscoveryEnabled, boolean multiTenancyEnabled) {
		return new JavaBeanMappingInitiatorImpl(
				mappingRepositoryBuilder, new JavaBeanBootstrapIntrospector( lookup ),
				annotatedTypeDiscoveryEnabled, multiTenancyEnabled
		);
	}
}
