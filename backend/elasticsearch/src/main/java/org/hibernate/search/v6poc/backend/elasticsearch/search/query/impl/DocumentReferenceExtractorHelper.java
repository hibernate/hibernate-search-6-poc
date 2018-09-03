/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.backend.elasticsearch.multitenancy.impl.MultiTenancyStrategy;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchDocumentReference;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

import com.google.gson.JsonObject;

class DocumentReferenceExtractorHelper {
	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final JsonAccessor<String> HIT_INDEX_NAME_ACCESSOR = JsonAccessor.root().property( "_index" ).asString();

	private final Function<String, String> indexNameConverter;
	private final MultiTenancyStrategy multiTenancyStrategy;

	DocumentReferenceExtractorHelper(Function<String, String> indexNameConverter, MultiTenancyStrategy multiTenancyStrategy) {
		this.indexNameConverter = indexNameConverter;
		this.multiTenancyStrategy = multiTenancyStrategy;
	}

	void contributeRequest(JsonObject requestBody) {
		multiTenancyStrategy.contributeToSearchRequest( requestBody );
	}

	DocumentReference extractDocumentReference(JsonObject hit) {
		String indexName = HIT_INDEX_NAME_ACCESSOR.get( hit )
				.map( indexNameConverter )
				.orElseThrow( log::elasticsearchResponseMissingData );
		String id = multiTenancyStrategy.extractTenantScopedDocumentId( hit );
		return new ElasticsearchDocumentReference( indexName, id );
	}
}
