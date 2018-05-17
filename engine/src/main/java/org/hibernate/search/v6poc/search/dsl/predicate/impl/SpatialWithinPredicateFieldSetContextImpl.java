/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.predicate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.search.dsl.predicate.SpatialWithinCirclePredicateContext;
import org.hibernate.search.v6poc.search.dsl.predicate.SpatialWithinPredicateFieldSetContext;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateFactory;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinCirclePredicateBuilder;
import org.hibernate.search.v6poc.util.impl.common.CollectionHelper;
import org.hibernate.search.v6poc.util.impl.common.Contracts;


class SpatialWithinPredicateFieldSetContextImpl<N, C>
		implements SpatialWithinPredicateFieldSetContext<N>, MultiFieldPredicateCommonState.FieldSetContext<C> {

	private final CommonState<N, C> commonState;

	private final List<String> absoluteFieldPaths;

	private final List<SearchPredicateBuilder<C>> queryBuilders;

	private Float boost;

	SpatialWithinPredicateFieldSetContextImpl(CommonState<N, C> commonState, List<String> absoluteFieldPaths) {
		this.commonState = commonState;
		this.commonState.add( this );
		this.absoluteFieldPaths = CollectionHelper.toImmutableList( absoluteFieldPaths );
		this.queryBuilders = new ArrayList<>( absoluteFieldPaths.size() );
	}

	@Override
	public SpatialWithinPredicateFieldSetContext<N> orFields(String... absoluteFieldPaths) {
		return new SpatialWithinPredicateFieldSetContextImpl<>( commonState, Arrays.asList( absoluteFieldPaths ) );
	}

	@Override
	public SpatialWithinPredicateFieldSetContext<N> boostedTo(float boost) {
		this.boost = boost;
		return this;
	}

	@Override
	public SpatialWithinCirclePredicateContext<N> circle(GeoPoint center, double radiusInMeters) {
		Contracts.assertNotNull( center, "center" );
		Contracts.assertNotNull( radiusInMeters, "radius" );

		return commonState.circle( center, radiusInMeters );
	}

	@Override
	public void contributePredicateBuilders(Consumer<SearchPredicateBuilder<? super C>> collector) {
		queryBuilders.forEach( collector );
	}

	private void generateWithinCircleQueryBuilders(GeoPoint center, double radiusInMeters) {
		for ( String absoluteFieldPath : absoluteFieldPaths ) {
			SpatialWithinCirclePredicateBuilder<C> predicateBuilder = commonState.getFactory().spatialWithinCircle( absoluteFieldPath );
			predicateBuilder.circle( center, radiusInMeters );
			if ( boost != null ) {
				predicateBuilder.boost( boost );
			}
			queryBuilders.add( predicateBuilder );
		}
	}

	static class CommonState<N, C> extends MultiFieldPredicateCommonState<N, C, SpatialWithinPredicateFieldSetContextImpl<N, C>> {

		CommonState(SearchPredicateFactory<C> factory, Supplier<N> nextContextProvider) {
			super( factory, nextContextProvider );
		}

		public SpatialWithinCirclePredicateContext<N> circle(GeoPoint center, double radiusInMeters) {
			for ( SpatialWithinPredicateFieldSetContextImpl<N, C> fieldSetContext : getFieldSetContexts() ) {
				fieldSetContext.generateWithinCircleQueryBuilders( center, radiusInMeters );
			}

			return new SpatialWithinCirclePredicateContextImpl<N>( getNextContextProvider() );
		}
	}
}
