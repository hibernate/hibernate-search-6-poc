/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonArrayAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonObjectAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.UnknownTypeJsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.types.codec.impl.ElasticsearchFieldCodec;
import org.hibernate.search.v6poc.search.query.spi.ProjectionHitCollector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * A hit extractor that will extract a value from the "_source",
 * and insert it into the projection.
 */
class SourceHitExtractor implements HitExtractor<ProjectionHitCollector> {

	private static final JsonArrayAccessor REQUEST_SOURCE_ACCESSOR = JsonAccessor.root().property( "_source" ).asArray();
	private static final JsonObjectAccessor HIT_SOURCE_ACCESSOR = JsonAccessor.root().property( "_source" ).asObject();

	private final String absoluteFieldPath;
	private final UnknownTypeJsonAccessor hitFieldValueAccessor;
	private final ElasticsearchFieldCodec codec;

	SourceHitExtractor(String absoluteFieldPath, ElasticsearchFieldCodec codec) {
		this.absoluteFieldPath = absoluteFieldPath;
		this.hitFieldValueAccessor = HIT_SOURCE_ACCESSOR.property( absoluteFieldPath );
		this.codec = codec;
	}

	@Override
	public void contributeRequest(JsonObject requestBody) {
		JsonArray source = REQUEST_SOURCE_ACCESSOR.get( requestBody )
				.orElseGet( () -> {
					JsonArray newSource = new JsonArray();
					REQUEST_SOURCE_ACCESSOR.set( requestBody, newSource );
					return newSource;
				} );
		JsonPrimitive fieldPathJson = new JsonPrimitive( absoluteFieldPath );
		if ( !source.contains( fieldPathJson ) ) {
			source.add( fieldPathJson );
		}
	}

	@Override
	public void extract(ProjectionHitCollector collector, JsonObject responseBody, JsonObject hit) {
		JsonElement fieldValue = hitFieldValueAccessor.get( hit ).orElse( null );
		collector.collectProjection( codec.decode( fieldValue ) );
	}

}
