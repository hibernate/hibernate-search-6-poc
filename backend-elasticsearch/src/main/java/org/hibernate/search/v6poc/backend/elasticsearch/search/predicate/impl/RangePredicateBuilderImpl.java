/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonObjectAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.types.converter.impl.ElasticsearchFieldConverter;
import org.hibernate.search.v6poc.search.predicate.spi.RangePredicateBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Yoann Rodiere
 */
public class RangePredicateBuilderImpl extends AbstractSearchPredicateBuilder
		implements RangePredicateBuilder<ElasticsearchSearchPredicateBuilder> {

	private static final JsonObjectAccessor RANGE = JsonAccessor.root().property( "range" ).asObject();

	private static final JsonAccessor<JsonElement> GT = JsonAccessor.root().property( "gt" );
	private static final JsonAccessor<JsonElement> GTE = JsonAccessor.root().property( "gte" );
	private static final JsonAccessor<JsonElement> LT = JsonAccessor.root().property( "lt" );
	private static final JsonAccessor<JsonElement> LTE = JsonAccessor.root().property( "lte" );

	private final String absoluteFieldPath;
	private final ElasticsearchFieldConverter converter;

	private JsonElement lowerLimit;
	private boolean excludeLowerLimit = false;
	private JsonElement upperLimit;
	private boolean excludeUpperLimit = false;

	public RangePredicateBuilderImpl(String absoluteFieldPath, ElasticsearchFieldConverter converter) {
		this.absoluteFieldPath = absoluteFieldPath;
		this.converter = converter;
	}

	@Override
	public void lowerLimit(Object value) {
		this.lowerLimit = converter.convertFromDsl( value );
	}

	@Override
	public void excludeLowerLimit() {
		this.excludeLowerLimit = true;
	}

	@Override
	public void upperLimit(Object value) {
		this.upperLimit = converter.convertFromDsl( value );
	}

	@Override
	public void excludeUpperLimit() {
		this.excludeUpperLimit = true;
	}

	@Override
	protected JsonObject doBuild() {
		JsonObject innerObject = getInnerObject();
		JsonAccessor<JsonElement> accessor;
		if ( lowerLimit != null ) {
			accessor = excludeLowerLimit ? GT : GTE;
			accessor.set( innerObject, lowerLimit );
		}
		if ( upperLimit != null ) {
			accessor = excludeUpperLimit ? LT : LTE;
			accessor.set( innerObject, upperLimit );
		}

		JsonObject outerObject = getOuterObject();
		JsonObject middleObject = new JsonObject();
		middleObject.add( absoluteFieldPath, innerObject );
		RANGE.set( outerObject, middleObject );

		return outerObject;
	}

}
