/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchDocumentReference;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.search.query.spi.DocumentReferenceHitCollector;

import com.google.gson.JsonObject;

class DocumentReferenceHitExtractor implements HitExtractor<DocumentReferenceHitCollector> {
	private static final JsonAccessor<String> HIT_INDEX_NAME_ACCESSOR = JsonAccessor.root().property( "_index" ).asString();
	private static final JsonAccessor<String> HIT_ID_ACCESSOR = JsonAccessor.root().property( "_id" ).asString();

	private static final DocumentReferenceHitExtractor INSTANCE = new DocumentReferenceHitExtractor();

	public static DocumentReferenceHitExtractor get() {
		return INSTANCE;
	}

	@Override
	public void contributeRequest(JsonObject requestBody) {
		// Nothing to do
	}

	@Override
	public void extract(DocumentReferenceHitCollector collector, JsonObject responseBody, JsonObject hit) {
		String indexName = HIT_INDEX_NAME_ACCESSOR.get( hit ).get();
		String id = HIT_ID_ACCESSOR.get( hit ).get();
		DocumentReference documentReference = new ElasticsearchDocumentReference( indexName, id );
		collector.collectReference( documentReference );
	}

}
