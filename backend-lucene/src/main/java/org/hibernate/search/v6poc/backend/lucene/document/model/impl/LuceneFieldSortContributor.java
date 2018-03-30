/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.model.impl;

import org.hibernate.search.v6poc.backend.lucene.search.sort.impl.LuceneSearchSortCollector;
import org.hibernate.search.v6poc.search.dsl.sort.SortOrder;

public interface LuceneFieldSortContributor {

	void contribute(LuceneSearchSortCollector collector, String absoluteFieldPath, SortOrder order, Object missingValue);
}
