/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.hibernate.search.v6poc.backend.elasticsearch.orchestration.impl.ElasticsearchWorkOrchestrator;
import org.hibernate.search.v6poc.backend.elasticsearch.search.dsl.impl.ElasticsearchSearchPredicateCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.search.dsl.impl.ElasticsearchSingleSearchPredicateCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWorkFactory;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.search.SearchQuery;
import org.hibernate.search.v6poc.search.dsl.spi.SearchQueryBuilder;
import org.hibernate.search.v6poc.search.spi.HitAggregator;

import com.google.gson.JsonObject;

class SearchQueryBuilderImpl<C, T> implements SearchQueryBuilder<T, ElasticsearchSearchPredicateCollector> {

	private final ElasticsearchWorkOrchestrator queryOrchestrator;
	private final ElasticsearchWorkFactory workFactory;
	private final Set<String> indexNames;
	private final Set<String> routingKeys;
	private final HitExtractor<? super C> hitExtractor;
	private final HitAggregator<C, List<T>> hitAggregator;
	private final ElasticsearchSingleSearchPredicateCollector predicateCollector;

	public SearchQueryBuilderImpl(
			ElasticsearchWorkOrchestrator queryOrchestrator,
			ElasticsearchWorkFactory workFactory,
			Set<String> indexNames,
			SessionContext context,
			HitExtractor<? super C> hitExtractor,
			HitAggregator<C, List<T>> hitAggregator) {
		this.hitExtractor = hitExtractor;
		this.hitAggregator = hitAggregator;
		String tenantId = context.getTenantIdentifier();
		if ( tenantId != null ) {
			// TODO handle tenant ID filtering
		}
		this.queryOrchestrator = queryOrchestrator;
		this.workFactory = workFactory;
		this.indexNames = indexNames;
		this.routingKeys = new HashSet<>();
		this.predicateCollector = new ElasticsearchSingleSearchPredicateCollector();
	}

	@Override
	public ElasticsearchSearchPredicateCollector getPredicateCollector() {
		return predicateCollector;
	}

	@Override
	public void addRoutingKey(String routingKey) {
		this.routingKeys.add( routingKey );
	}

	private SearchQuery<T> build() {
		JsonObject payload = new JsonObject();
		payload.add( "query", predicateCollector.toJson() );
		hitExtractor.contributeRequest( payload );
		SearchResultExtractor<T> searchResultExtractor =
				new SearchResultExtractorImpl<>( hitExtractor, hitAggregator );
		return new ElasticsearchSearchQuery<>( queryOrchestrator, workFactory,
				indexNames, routingKeys,
				payload, searchResultExtractor );
	}

	@Override
	public <Q> Q build(Function<SearchQuery<T>, Q> searchQueryWrapperFactory) {
		return searchQueryWrapperFactory.apply( build() );
	}
}
