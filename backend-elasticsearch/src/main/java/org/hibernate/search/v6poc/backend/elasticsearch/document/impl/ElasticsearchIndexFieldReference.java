/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.impl;

import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.backend.document.spi.IndexFieldReference;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;


/**
 * @author Yoann Rodiere
 */
public abstract class ElasticsearchIndexFieldReference<T, T2> implements IndexFieldReference<T> {

	private final JsonAccessor<T2> accessor;

	protected ElasticsearchIndexFieldReference(JsonAccessor<T2> accessor) {
		this.accessor = accessor;
	}

	@Override
	public void add(DocumentState state, T value) {
		((ElasticsearchDocumentBuilder) state).add( accessor, convert( value ) );
	}

	protected abstract T2 convert(T value);

}
