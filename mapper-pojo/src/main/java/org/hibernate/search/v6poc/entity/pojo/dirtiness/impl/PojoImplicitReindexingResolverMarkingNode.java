/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.dirtiness.impl;

import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRuntimeIntrospector;
import org.hibernate.search.v6poc.util.impl.common.ToStringTreeBuilder;

/**
 * A {@link PojoImplicitReindexingResolverNode} marking as "to reindex" any dirty object passed as an input.
 *
 * @param <T> The type of "dirty" objects received as input.
 * @param <S> The expected type of the object describing the "dirtiness state".
 */
public class PojoImplicitReindexingResolverMarkingNode<T, S> extends PojoImplicitReindexingResolverNode<T, S> {

	public PojoImplicitReindexingResolverMarkingNode() {
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "class", getClass().getSimpleName() );
	}

	@Override
	public void resolveEntitiesToReindex(PojoReindexingCollector collector,
			PojoRuntimeIntrospector runtimeIntrospector, T dirty, S dirtinessState) {
		collector.markForReindexing( dirty );
	}
}
