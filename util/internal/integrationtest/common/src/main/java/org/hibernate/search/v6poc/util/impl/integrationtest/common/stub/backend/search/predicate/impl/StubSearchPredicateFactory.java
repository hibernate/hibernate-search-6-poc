/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.search.predicate.impl;

import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.search.StubQueryElementCollector;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.predicate.spi.MatchAllPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.BooleanJunctionPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.MatchPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.NestedPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.RangePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateContributor;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateFactory;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinBoundingBoxPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinCirclePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinPolygonPredicateBuilder;

public class StubSearchPredicateFactory implements SearchPredicateFactory<StubQueryElementCollector> {

	@Override
	public SearchPredicate toSearchPredicate(SearchPredicateContributor<? super StubQueryElementCollector> contributor) {
		contributor.contribute( StubQueryElementCollector.get() );
		return new StubSearchPredicate();
	}

	@Override
	public SearchPredicateContributor<StubQueryElementCollector> toContributor(SearchPredicate predicate) {
		return (StubSearchPredicate) predicate;
	}

	@Override
	public MatchAllPredicateBuilder<StubQueryElementCollector> matchAll() {
		return new StubPredicateBuilder();
	}

	@Override
	public BooleanJunctionPredicateBuilder<StubQueryElementCollector> bool() {
		return new StubPredicateBuilder();
	}

	@Override
	public MatchPredicateBuilder<StubQueryElementCollector> match(String absoluteFieldPath) {
		return new StubPredicateBuilder();
	}

	@Override
	public RangePredicateBuilder<StubQueryElementCollector> range(String absoluteFieldPath) {
		return new StubPredicateBuilder();
	}

	@Override
	public SpatialWithinCirclePredicateBuilder<StubQueryElementCollector> spatialWithinCircle(String absoluteFieldPath) {
		return new StubPredicateBuilder();
	}

	@Override
	public SpatialWithinPolygonPredicateBuilder<StubQueryElementCollector> spatialWithinPolygon(String absoluteFieldPath) {
		return new StubPredicateBuilder();
	}

	@Override
	public SpatialWithinBoundingBoxPredicateBuilder<StubQueryElementCollector> spatialWithinBoundingBox(String absoluteFieldPath) {
		return new StubPredicateBuilder();
	}

	@Override
	public NestedPredicateBuilder<StubQueryElementCollector> nested(String absoluteFieldPath) {
		return new StubPredicateBuilder();
	}
}
