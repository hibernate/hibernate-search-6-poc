/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.hibernate.search.v6poc.backend.elasticsearch.util.impl.URLEncodedString;
import org.hibernate.search.v6poc.backend.elasticsearch.multitenancy.impl.MultiTenancyStrategy;
import org.hibernate.search.v6poc.backend.elasticsearch.orchestration.impl.ElasticsearchWorkOrchestrator;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWorkFactory;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.util.EventContext;
import org.hibernate.search.v6poc.search.query.spi.HitAggregator;

public class SearchBackendContext {
	private final EventContext eventContext;

	private final ElasticsearchWorkFactory workFactory;
	private final MultiTenancyStrategy multiTenancyStrategy;

	private final ElasticsearchWorkOrchestrator orchestrator;

	private final DocumentReferenceHitExtractor documentReferenceHitExtractor;
	private final ObjectHitExtractor objectHitExtractor;
	private final DocumentReferenceProjectionHitExtractor documentReferenceProjectionHitExtractor;

	public SearchBackendContext(EventContext eventContext,
			ElasticsearchWorkFactory workFactory,
			Function<String, String> indexNameConverter,
			MultiTenancyStrategy multiTenancyStrategy,
			ElasticsearchWorkOrchestrator orchestrator) {
		this.eventContext = eventContext;
		this.workFactory = workFactory;
		this.multiTenancyStrategy = multiTenancyStrategy;
		this.orchestrator = orchestrator;

		DocumentReferenceExtractorHelper documentReferenceExtractorHelper =
				new DocumentReferenceExtractorHelper( indexNameConverter, multiTenancyStrategy );
		this.documentReferenceHitExtractor = new DocumentReferenceHitExtractor( documentReferenceExtractorHelper );
		this.objectHitExtractor = new ObjectHitExtractor( documentReferenceExtractorHelper );
		this.documentReferenceProjectionHitExtractor =
				new DocumentReferenceProjectionHitExtractor( documentReferenceExtractorHelper );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + eventContext + "]";
	}

	public EventContext getEventContext() {
		return eventContext;
	}

	DocumentReferenceHitExtractor getDocumentReferenceHitExtractor() {
		return documentReferenceHitExtractor;
	}

	ObjectHitExtractor getObjectHitExtractor() {
		return objectHitExtractor;
	}

	DocumentReferenceProjectionHitExtractor getDocumentReferenceProjectionHitExtractor() {
		return documentReferenceProjectionHitExtractor;
	}

	<C, T> SearchQueryBuilderImpl<C, T> createSearchQueryBuilder(
			Set<URLEncodedString> indexNames,
			SessionContext sessionContext,
			HitExtractor<? super C> hitExtractor,
			HitAggregator<C, List<T>> hitAggregator) {
		multiTenancyStrategy.checkTenantId( sessionContext.getTenantIdentifier(), eventContext );
		return new SearchQueryBuilderImpl<>(
				workFactory, orchestrator, multiTenancyStrategy,
				indexNames, sessionContext, hitExtractor, hitAggregator
		);
	}
}
