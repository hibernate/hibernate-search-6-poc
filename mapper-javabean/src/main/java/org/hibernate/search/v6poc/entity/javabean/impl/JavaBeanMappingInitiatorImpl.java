/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.javabean.impl;

import org.hibernate.search.v6poc.entity.javabean.JavaBeanMapping;
import org.hibernate.search.v6poc.entity.javabean.mapping.impl.JavaBeanMappingFactory;
import org.hibernate.search.v6poc.entity.javabean.model.impl.JavaBeanBootstrapIntrospector;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoMappingInitiatorImpl;

public class JavaBeanMappingInitiatorImpl extends PojoMappingInitiatorImpl<JavaBeanMapping> {

	private final JavaBeanTypeConfigurationContributor typeConfigurationContributor;

	public JavaBeanMappingInitiatorImpl(JavaBeanBootstrapIntrospector introspector) {
		super(
				new JavaBeanMappingFactory(),
				introspector
		);
		typeConfigurationContributor = new JavaBeanTypeConfigurationContributor( introspector );
		addConfigurationContributor( typeConfigurationContributor );
	}

	public void addEntityType(Class<?> type) {
		typeConfigurationContributor.addEntityType( type );
	}
}
