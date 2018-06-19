/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.sort.spi;

import org.hibernate.search.v6poc.search.SearchSort;
import org.hibernate.search.v6poc.spatial.GeoPoint;

/**
 * A factory for search sorts.
 * <p>
 * This is the main entry point for the engine
 * to ask the backend to build search sorts.
 *
 * @param <C> The type of sort collector the builders contributor will contribute to.
 * This type is backend-specific. See {@link SearchSortContributor#contribute(Object)}
 */
public interface SearchSortFactory<C> {

	SearchSort toSearchSort(SearchSortContributor<? super C> contributor);

	SearchSortContributor<C> toContributor(SearchSort predicate);

	ScoreSortBuilder<C> score();

	FieldSortBuilder<C> field(String absoluteFieldPath);

	DistanceSortBuilder<C> distance(String absoluteFieldPath, GeoPoint location);

	SearchSortContributor<C> indexOrder();

}
