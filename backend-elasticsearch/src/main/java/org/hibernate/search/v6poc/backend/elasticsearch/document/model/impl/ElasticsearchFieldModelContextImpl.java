/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl;

import java.time.LocalDate;

import org.hibernate.search.v6poc.backend.document.model.spi.TerminalFieldModelContext;
import org.hibernate.search.v6poc.backend.document.model.spi.TypedFieldModelContext;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.ElasticsearchFieldModelContext;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.PropertyMapping;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.UnknownTypeJsonAccessor;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.util.SearchException;


/**
 * @author Yoann Rodiere
 */
public class ElasticsearchFieldModelContextImpl
		implements ElasticsearchFieldModelContext, ElasticsearchIndexSchemaNodeContributor<PropertyMapping> {

	private final UnknownTypeJsonAccessor accessor;

	private ElasticsearchIndexSchemaNodeContributor<PropertyMapping> delegate;

	public ElasticsearchFieldModelContextImpl(UnknownTypeJsonAccessor accessor) {
		this.accessor = accessor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> TypedFieldModelContext<T> as(Class<T> inputType) {
		if ( String.class.equals( inputType ) ) {
			return (TypedFieldModelContext<T>) asString();
		}
		else if ( Integer.class.equals( inputType ) ) {
			return (TypedFieldModelContext<T>) asInteger();
		}
		else if ( LocalDate.class.equals( inputType ) ) {
			return (TypedFieldModelContext<T>) asLocalDate();
		}
		else if ( GeoPoint.class.equals( inputType ) ) {
			return (TypedFieldModelContext<T>) asGeoPoint();
		}
		else {
			// TODO implement other types
			throw new SearchException( "Cannot guess field type for input type " + inputType );
		}
	}

	@Override
	public TypedFieldModelContext<String> asString() {
		return setDelegate( new StringFieldModelContext( accessor ) );
	}

	@Override
	public TypedFieldModelContext<Integer> asInteger() {
		return setDelegate( new IntegerFieldModelContext( accessor ) );
	}

	@Override
	public TypedFieldModelContext<LocalDate> asLocalDate() {
		return setDelegate( new LocalDateFieldModelContext( accessor ) );
	}

	@Override
	public TypedFieldModelContext<GeoPoint> asGeoPoint() {
		return setDelegate( new GeoPointFieldModelContext( accessor ) );
	}

	@Override
	public TerminalFieldModelContext<String> asJsonString(String mappingJsonString) {
		return setDelegate( new JsonStringFieldModelContext( accessor, mappingJsonString ) );
	}

	@Override
	public PropertyMapping contribute(ElasticsearchFieldModelCollector collector) {
		// TODO error if delegate is null
		return delegate.contribute( collector );
	}

	private <T extends ElasticsearchIndexSchemaNodeContributor<PropertyMapping>> T setDelegate(T context) {
		if ( delegate != null ) {
			throw new SearchException( "You cannot set the type of a field more than once" );
		}
		delegate = context;
		return context;
	}

}
