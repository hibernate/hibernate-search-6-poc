/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import java.util.function.Function;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchDocumentReference;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.search.query.spi.LoadingHitCollector;

import com.google.gson.JsonObject;

class ObjectHitExtractor<R> implements HitExtractor<LoadingHitCollector<? super R>> {
	private static final JsonAccessor<String> HIT_INDEX_NAME_ACCESSOR = JsonAccessor.root().property( "_index" ).asString();
	private static final JsonAccessor<String> HIT_ID_ACCESSOR = JsonAccessor.root().property( "_id" ).asString();

	private final Function<DocumentReference, R> referenceTransformer;

	public ObjectHitExtractor(Function<DocumentReference, R> referenceTransformer) {
		this.referenceTransformer = referenceTransformer;
	}

	@Override
	public void contributeRequest(JsonObject requestBody) {
		// Nothing to do
	}

	@Override
	public void extract(LoadingHitCollector<? super R> collector, JsonObject responseBody, JsonObject hit) {
		String indexName = HIT_INDEX_NAME_ACCESSOR.get( hit ).get();
		String id = HIT_ID_ACCESSOR.get( hit ).get();
		DocumentReference documentReference = new ElasticsearchDocumentReference( indexName, id );
		R reference = referenceTransformer.apply( documentReference );
		collector.collectForLoading( reference );
	}

}
