/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import java.util.Set;

import org.hibernate.search.v6poc.backend.elasticsearch.util.impl.URLEncodedString;
import org.hibernate.search.v6poc.backend.elasticsearch.orchestration.impl.ElasticsearchWorkOrchestrator;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWork;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWorkFactory;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.SearchResultExtractor;
import org.hibernate.search.v6poc.search.SearchQuery;
import org.hibernate.search.v6poc.search.SearchResult;

import com.google.gson.JsonObject;


/**
 * @author Yoann Rodiere
 */
public class ElasticsearchSearchQuery<T> implements SearchQuery<T> {

	private final ElasticsearchWorkFactory workFactory;
	private final ElasticsearchWorkOrchestrator queryOrchestrator;
	private final Set<URLEncodedString> indexNames;
	private final Set<String> routingKeys;
	private final JsonObject payload;
	private final SearchResultExtractor<T> searchResultExtractor;

	private Long firstResultIndex;
	private Long maxResultsCount;

	public ElasticsearchSearchQuery(ElasticsearchWorkFactory workFactory,
			ElasticsearchWorkOrchestrator queryOrchestrator,
			Set<URLEncodedString> indexNames, Set<String> routingKeys,
			JsonObject payload, SearchResultExtractor<T> searchResultExtractor) {
		this.workFactory = workFactory;
		this.queryOrchestrator = queryOrchestrator;
		this.indexNames = indexNames;
		this.routingKeys = routingKeys;
		this.payload = payload;
		this.searchResultExtractor = searchResultExtractor;
	}

	@Override
	public void setFirstResult(Long firstResultIndex) {
		this.firstResultIndex = firstResultIndex;
	}

	@Override
	public void setMaxResults(Long maxResultsCount) {
		this.maxResultsCount = maxResultsCount;
	}

	@Override
	public String getQueryString() {
		return payload.toString();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getQueryString() + "]";
	}

	@Override
	public SearchResult<T> execute() {
		ElasticsearchWork<SearchResult<T>> work = workFactory.search(
				indexNames, routingKeys,
				payload, searchResultExtractor,
				firstResultIndex, maxResultsCount );
		return queryOrchestrator.submit( work ).join();
	}

}
