/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.search.sort;

import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.search.StubQueryElementCollector;
import org.hibernate.search.v6poc.search.SearchSort;
import org.hibernate.search.v6poc.search.sort.spi.DistanceSortBuilder;
import org.hibernate.search.v6poc.search.sort.spi.FieldSortBuilder;
import org.hibernate.search.v6poc.search.sort.spi.ScoreSortBuilder;
import org.hibernate.search.v6poc.search.sort.spi.SearchSortContributor;
import org.hibernate.search.v6poc.search.sort.spi.SearchSortFactory;
import org.hibernate.search.v6poc.spatial.GeoPoint;

public class StubSearchSortFactory implements SearchSortFactory<StubQueryElementCollector> {
	@Override
	public SearchSort toSearchSort(SearchSortContributor<? super StubQueryElementCollector> contributor) {
		contributor.contribute( StubQueryElementCollector.get() );
		return new StubSearchSort();
	}

	@Override
	public SearchSortContributor<StubQueryElementCollector> toContributor(SearchSort sort) {
		return (StubSearchSort) sort;
	}

	@Override
	public ScoreSortBuilder<StubQueryElementCollector> score() {
		return new StubSortBuilder();
	}

	@Override
	public FieldSortBuilder<StubQueryElementCollector> field(String absoluteFieldPath) {
		return new StubSortBuilder();
	}

	@Override
	public SearchSortContributor<StubQueryElementCollector> indexOrder() {
		return new StubSearchSort();
	}

	@Override
	public DistanceSortBuilder<StubQueryElementCollector> distance(String absoluteFieldPath, GeoPoint location) {
		return new StubSortBuilder();
	}
}
