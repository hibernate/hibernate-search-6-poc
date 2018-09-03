/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * A hit extractor that will delegate to a different extractor
 * depending on the index a given result was extracted from.
 * <p>
 * Used in projections, where a given projection can have a different meaning
 * depending on the index.
 */
class IndexSensitiveHitExtractor<C> implements HitExtractor<C> {
	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final JsonAccessor<String> HIT_INDEX_NAME_ACCESSOR = JsonAccessor.root()
			.property( "_index" )
			.asString();

	private final Map<String, HitExtractor<? super C>> extractorByElasticsearchIndexName;

	IndexSensitiveHitExtractor(Map<String, HitExtractor<? super C>> extractorByElasticsearchIndexName) {
		this.extractorByElasticsearchIndexName = extractorByElasticsearchIndexName;
	}

	@Override
	public void contributeRequest(JsonObject requestBody) {
		for ( HitExtractor<?> extractor : extractorByElasticsearchIndexName.values() ) {
			extractor.contributeRequest( requestBody );
		}
	}

	@Override
	public void extract(C collector, JsonObject responseBody, JsonObject hit) {
		String elasticsearchIndexName = HIT_INDEX_NAME_ACCESSOR.get( hit ).orElseThrow( log::elasticsearchResponseMissingData );
		HitExtractor<? super C> delegate = extractorByElasticsearchIndexName.get( elasticsearchIndexName );
		delegate.extract( collector, responseBody, hit );
	}
}
