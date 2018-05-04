/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.spi;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;

public class DeferredInitializationIndexFieldAccessor<T> implements IndexFieldAccessor<T> {

	private IndexFieldAccessor<T> delegate;

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + delegate + "]";
	}

	public void initialize(IndexFieldAccessor<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void write(DocumentElement state, T value) {
		if ( delegate != null ) {
			delegate.write( state, value );
		}
		// else: The field was filtered out - ignore it
	}
}
