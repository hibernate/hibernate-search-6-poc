/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonObjectAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.backend.elasticsearch.types.converter.impl.ElasticsearchFieldConverter;
import org.hibernate.search.v6poc.logging.spi.EventContexts;
import org.hibernate.search.v6poc.search.predicate.spi.MatchPredicateBuilder;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Yoann Rodiere
 */
public class MatchPredicateBuilderImpl extends AbstractSearchPredicateBuilder
		implements MatchPredicateBuilder<ElasticsearchSearchPredicateBuilder> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final JsonAccessor<JsonElement> QUERY = JsonAccessor.root().property( "query" );

	private static final JsonObjectAccessor MATCH = JsonAccessor.root().property( "match" ).asObject();

	private final String absoluteFieldPath;
	private final ElasticsearchFieldConverter converter;

	public MatchPredicateBuilderImpl(String absoluteFieldPath, ElasticsearchFieldConverter converter) {
		this.absoluteFieldPath = absoluteFieldPath;
		this.converter = converter;
	}

	@Override
	public void value(Object value) {
		JsonElement element;
		try {
			element = converter.convertFromDsl( value );
		}
		catch (RuntimeException e) {
			throw log.cannotConvertDslParameter(
					e.getMessage(), e, EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath )
			);
		}
		QUERY.set( getInnerObject(), element );
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
