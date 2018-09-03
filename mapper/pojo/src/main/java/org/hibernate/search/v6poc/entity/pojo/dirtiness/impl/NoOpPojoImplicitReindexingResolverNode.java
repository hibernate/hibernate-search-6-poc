/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.dirtiness.impl;

import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRuntimeIntrospector;
import org.hibernate.search.v6poc.util.impl.common.ToStringTreeBuilder;

class NoOpPojoImplicitReindexingResolverNode extends PojoImplicitReindexingResolverNode<Object, Object> {

	private static NoOpPojoImplicitReindexingResolverNode INSTANCE = new NoOpPojoImplicitReindexingResolverNode();

	@SuppressWarnings( "unchecked" ) // This instance works for any T or D
	public static <T, D> PojoImplicitReindexingResolverNode<T, D> get() {
		return (PojoImplicitReindexingResolverNode<T, D>) INSTANCE;
	}

	@Override
	public void resolveEntitiesToReindex(PojoReindexingCollector collector,
			PojoRuntimeIntrospector runtimeIntrospector, Object dirty, Object dirtinessState) {
		// No-op
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "class", getClass().getSimpleName() );
	}
}
