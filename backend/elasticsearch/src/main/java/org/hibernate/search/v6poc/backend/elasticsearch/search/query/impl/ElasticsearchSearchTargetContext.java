/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchQueryElementCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchTargetModel;
import org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl.SearchPredicateFactoryImpl;
import org.hibernate.search.v6poc.backend.elasticsearch.search.sort.impl.SearchSortFactoryImpl;
import org.hibernate.search.v6poc.search.dsl.spi.SearchTargetContext;

public class ElasticsearchSearchTargetContext
		implements SearchTargetContext<ElasticsearchSearchQueryElementCollector> {

	private final SearchPredicateFactoryImpl searchPredicateFactory;
	private final SearchSortFactoryImpl searchSortFactory;
	private final SearchQueryFactoryImpl searchQueryFactory;

	public ElasticsearchSearchTargetContext(SearchBackendContext searchBackendContext,
			ElasticsearchSearchTargetModel searchTargetModel) {
		this.searchPredicateFactory = new SearchPredicateFactoryImpl( searchTargetModel );
		this.searchSortFactory = new SearchSortFactoryImpl( searchTargetModel );
		this.searchQueryFactory = new SearchQueryFactoryImpl( searchBackendContext, searchTargetModel );
	}

	@Override
	public SearchPredicateFactoryImpl getSearchPredicateFactory() {
		return searchPredicateFactory;
	}

	@Override
	public SearchSortFactoryImpl getSearchSortFactory() {
		return searchSortFactory;
	}

	@Override
	public SearchQueryFactoryImpl getSearchQueryFactory() {
		return searchQueryFactory;
	}
}
