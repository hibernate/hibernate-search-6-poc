/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonObjectAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.types.codec.impl.ElasticsearchFieldCodec;
import org.hibernate.search.v6poc.search.predicate.spi.MatchPredicateBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Yoann Rodiere
 */
public class MatchPredicateBuilderImpl<F> extends AbstractSearchPredicateBuilder
		implements MatchPredicateBuilder<ElasticsearchSearchPredicateBuilder> {

	private static final JsonAccessor<JsonElement> QUERY = JsonAccessor.root().property( "query" );

	private static final JsonObjectAccessor MATCH = JsonAccessor.root().property( "match" ).asObject();

	private final String absoluteFieldPath;

	private final ElasticsearchFieldCodec<F> codec;

	public MatchPredicateBuilderImpl(String absoluteFieldPath, ElasticsearchFieldCodec<F> codec) {
		this.absoluteFieldPath = absoluteFieldPath;
		this.codec = codec;
	}

	@Override
	public void value(Object value) {
		QUERY.set( getInnerObject(), codec.encode( value ) );
	}

	@Override
	protected JsonObject doBuild() {
		JsonObject outerObject = getOuterObject();
		JsonObject middleObject = new JsonObject();
		middleObject.add( absoluteFieldPath, getInnerObject() );
		MATCH.set( outerObject, middleObject );
		return outerObject;
	}

}
