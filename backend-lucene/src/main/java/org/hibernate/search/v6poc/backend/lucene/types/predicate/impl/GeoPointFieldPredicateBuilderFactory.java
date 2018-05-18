/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.predicate.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.backend.lucene.search.predicate.impl.LuceneSearchPredicateCollector;
import org.hibernate.search.v6poc.search.predicate.spi.MatchPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.RangePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinBoundingBoxPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinCirclePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinPolygonPredicateBuilder;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

public final class GeoPointFieldPredicateBuilderFactory implements LuceneFieldPredicateBuilderFactory {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	public static final GeoPointFieldPredicateBuilderFactory INSTANCE = new GeoPointFieldPredicateBuilderFactory();

	private GeoPointFieldPredicateBuilderFactory() {
	}

	@Override
	public MatchPredicateBuilder<LuceneSearchPredicateCollector> createMatchPredicateBuilder(String absoluteFieldPath) {
		throw log.matchPredicatesNotSupportedByGeoPoint( absoluteFieldPath );
	}

	@Override
	public RangePredicateBuilder<LuceneSearchPredicateCollector> createRangePredicateBuilder(String absoluteFieldPath) {
		throw log.rangePredicatesNotSupportedByGeoPoint( absoluteFieldPath );
	}

	@Override
	public SpatialWithinCirclePredicateBuilder<LuceneSearchPredicateCollector> createSpatialWithinCirclePredicateBuilder(String absoluteFieldPath) {
		return new GeoPointSpatialWithinCirclePredicateBuilder( absoluteFieldPath );
	}

	@Override
	public SpatialWithinPolygonPredicateBuilder<LuceneSearchPredicateCollector> createSpatialWithinPolygonPredicateBuilder(String absoluteFieldPath) {
		return new GeoPointSpatialWithinPolygonPredicateBuilder( absoluteFieldPath );
	}

	@Override
	public SpatialWithinBoundingBoxPredicateBuilder<LuceneSearchPredicateCollector> createSpatialWithinBoundingBoxPredicateBuilder(String absoluteFieldPath) {
		return new GeoPointSpatialWithinBoundingBoxPredicateBuilder( absoluteFieldPath );
	}
}
