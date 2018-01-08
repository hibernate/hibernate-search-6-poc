/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;

import com.google.gson.JsonObject;


/**
 * @author Yoann Rodiere
 */
abstract class AbstractSearchPredicateBuilder implements SearchPredicateBuilder {

	private static final JsonAccessor<Float> BOOST = JsonAccessor.root().property( "boost" ).asFloat();

	private final JsonObject outerObject = new JsonObject();

	private final JsonObject innerObject = new JsonObject();

	@Override
	public void boost(float boost) {
		BOOST.set( getInnerObject(), boost );
	}

	protected JsonObject getInnerObject() {
		return innerObject;
	}

	protected JsonObject getOuterObject() {
		return outerObject;
	}

	@Override
	public abstract JsonObject build();

}
