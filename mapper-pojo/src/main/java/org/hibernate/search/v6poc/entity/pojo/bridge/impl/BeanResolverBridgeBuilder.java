/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.impl;

import org.hibernate.search.v6poc.engine.spi.BeanReference;
import org.hibernate.search.v6poc.engine.spi.BeanResolver;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeBuilder;

public class BeanResolverBridgeBuilder<T> implements BridgeBuilder<T> {

	private final Class<T> expectedType;

	private final BeanReference beanReference;

	public BeanResolverBridgeBuilder(Class<T> expectedType, BeanReference beanReference) {
		this.expectedType = expectedType;
		this.beanReference = beanReference;
	}

	@Override
	public T build(BuildContext buildContext) {
		BeanResolver beanResolver = buildContext.getServiceManager().getBeanResolver();
		return beanResolver.resolve( beanReference, expectedType );
	}
}
