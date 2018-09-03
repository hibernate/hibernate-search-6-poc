/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.sort.impl;

import org.hibernate.search.v6poc.search.sort.spi.SearchSortFactory;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

public interface LuceneSearchSortFactory extends SearchSortFactory<LuceneSearchSortCollector, LuceneSearchSortBuilder> {

	LuceneSearchSortBuilder fromLuceneSortField(SortField luceneSortField);

	LuceneSearchSortBuilder fromLuceneSort(Sort luceneSort);

}
