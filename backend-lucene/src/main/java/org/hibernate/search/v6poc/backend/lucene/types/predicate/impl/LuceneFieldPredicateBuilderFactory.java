/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.predicate.impl;

import org.hibernate.search.v6poc.backend.lucene.search.predicate.impl.LuceneSearchPredicateBuilder;
import org.hibernate.search.v6poc.backend.lucene.types.converter.impl.LuceneFieldConverter;
import org.hibernate.search.v6poc.search.predicate.spi.MatchPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.RangePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinBoundingBoxPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinCirclePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinPolygonPredicateBuilder;

/**
 * @author Guillaume Smet
 */
public interface LuceneFieldPredicateBuilderFactory {

	/**
	 * Determine whether another predicate builder factory is DSL-compatible with this one,
	 * i.e. whether it creates builders that behave the same way.
	 *
	 * @see LuceneFieldConverter#isDslCompatibleWith(LuceneFieldConverter)
	 *
	 * @param other Another {@link LuceneFieldPredicateBuilderFactory}, never {@code null}.
	 * @return {@code true} if the given predicate builder factory is DSL-compatible.
	 * {@code false} otherwise, or when in doubt.
	 */
	boolean isDslCompatibleWith(LuceneFieldPredicateBuilderFactory other);

	MatchPredicateBuilder<LuceneSearchPredicateBuilder> createMatchPredicateBuilder(String absoluteFieldPath);

	RangePredicateBuilder<LuceneSearchPredicateBuilder> createRangePredicateBuilder(String absoluteFieldPath);

	SpatialWithinCirclePredicateBuilder<LuceneSearchPredicateBuilder> createSpatialWithinCirclePredicateBuilder(
			String absoluteFieldPath);

	SpatialWithinPolygonPredicateBuilder<LuceneSearchPredicateBuilder> createSpatialWithinPolygonPredicateBuilder(
			String absoluteFieldPath);

	SpatialWithinBoundingBoxPredicateBuilder<LuceneSearchPredicateBuilder> createSpatialWithinBoundingBoxPredicateBuilder(
			String absoluteFieldPath);

	// equals()/hashCode() needs to be implemented if the predicate factory is not a singleton

	boolean equals(Object obj);

	int hashCode();
}
