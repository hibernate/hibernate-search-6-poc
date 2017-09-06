/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.impl;

import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.backend.document.spi.IndexFieldReference;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchFieldFormatter;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;

import com.google.gson.JsonElement;


/**
 * @author Yoann Rodiere
 */
public class ElasticsearchIndexFieldReference<T> implements IndexFieldReference<T> {

	private final JsonAccessor<JsonElement> accessor;

	private final ElasticsearchFieldFormatter formatter;

	public ElasticsearchIndexFieldReference(JsonAccessor<JsonElement> accessor,
			ElasticsearchFieldFormatter formatter) {
		this.accessor = accessor;
		this.formatter = formatter;
	}

	@Override
	public void add(DocumentState state, T value) {
		((ElasticsearchDocumentBuilder) state).add( accessor, formatter.format( value ) );
	}

}
