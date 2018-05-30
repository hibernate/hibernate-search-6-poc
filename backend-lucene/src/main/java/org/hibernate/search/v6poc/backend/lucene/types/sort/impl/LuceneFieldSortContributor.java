/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.sort.impl;

import org.hibernate.search.v6poc.backend.lucene.search.sort.impl.LuceneSearchSortCollector;
import org.hibernate.search.v6poc.search.dsl.sort.SortOrder;
import org.hibernate.search.v6poc.spatial.GeoPoint;

public interface LuceneFieldSortContributor {

	void contribute(LuceneSearchSortCollector collector, String absoluteFieldPath, SortOrder order, Object missingValue);

	void contributeDistanceSort(LuceneSearchSortCollector collector, String absoluteFieldPath, GeoPoint location, SortOrder order);

	// equals()/hashCode() needs to be implemented if the sort contributor is not a singleton

	boolean equals(Object obj);

	int hashCode();
}
