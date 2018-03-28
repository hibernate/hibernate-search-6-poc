/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.query.impl;

import java.util.Set;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.v6poc.backend.lucene.index.spi.ReaderProvider;
import org.hibernate.search.v6poc.backend.lucene.orchestration.impl.LuceneQueryWorkOrchestrator;
import org.hibernate.search.v6poc.backend.lucene.work.impl.LuceneQueryWork;
import org.hibernate.search.v6poc.backend.lucene.work.impl.LuceneWorkFactory;
import org.hibernate.search.v6poc.search.SearchQuery;
import org.hibernate.search.v6poc.search.SearchResult;


/**
 * @author Guillaume Smet
 */
public class LuceneSearchQuery<T> implements SearchQuery<T> {

	private final LuceneQueryWorkOrchestrator queryOrchestrator;
	private final LuceneWorkFactory workFactory;
	private final Set<String> indexNames;
	private final Set<ReaderProvider> readerProviders;
	private final Query luceneQuery;
	private final Sort luceneSort;
	private final HitExtractor<?> hitExtractor;
	private final SearchResultExtractor<T> searchResultExtractor;

	private Long firstResultIndex = 0L;
	private Long maxResultsCount;

	public LuceneSearchQuery(LuceneQueryWorkOrchestrator queryOrchestrator,
			LuceneWorkFactory workFactory, Set<String> indexNames, Set<ReaderProvider> readerProviders,
			Query luceneQuery, Sort luceneSort, HitExtractor<?> hitExtractor, SearchResultExtractor<T> searchResultExtractor) {
		this.queryOrchestrator = queryOrchestrator;
		this.workFactory = workFactory;
		this.indexNames = indexNames;
		this.readerProviders = readerProviders;
		this.luceneQuery = luceneQuery;
		this.luceneSort = luceneSort;
		this.hitExtractor = hitExtractor;
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
		return luceneQuery.toString();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getQueryString() + "]";
	}

	@Override
	public SearchResult<T> execute() {
		LuceneQueryWork<SearchResult<T>> work = workFactory.search( new LuceneSearcher<T>(
				indexNames,
				readerProviders,
				luceneQuery, luceneSort,
				firstResultIndex, maxResultsCount,
				hitExtractor, searchResultExtractor ) );
		return queryOrchestrator.submit( work ).join();
	}
}
