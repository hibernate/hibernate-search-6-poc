/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.impl;

import java.time.LocalDate;

import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldContext;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.spatial.GeoPoint;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.StubIndexSchemaNode;

class StubIndexSchemaFieldContext implements IndexSchemaFieldContext {

	private final StubIndexSchemaNode.Builder builder;
	private final boolean included;

	StubIndexSchemaFieldContext(StubIndexSchemaNode.Builder builder, boolean included) {
		this.builder = builder;
		this.included = included;
	}

	@Override
	public <F> IndexSchemaFieldTypedContext<F> as(Class<F> inputType) {
		builder.inputType( inputType );
		return new StubIndexSchemaFieldTypedContext<>( builder, included );
	}

	@Override
	public IndexSchemaFieldTypedContext<String> asString() {
		return as( String.class );
	}

	@Override
	public IndexSchemaFieldTypedContext<Integer> asInteger() {
		return as( Integer.class );
	}

	@Override
	public IndexSchemaFieldTypedContext<LocalDate> asLocalDate() {
		return as( LocalDate.class );
	}

	@Override
	public IndexSchemaFieldTypedContext<GeoPoint> asGeoPoint() {
		return as( GeoPoint.class );
	}

}
