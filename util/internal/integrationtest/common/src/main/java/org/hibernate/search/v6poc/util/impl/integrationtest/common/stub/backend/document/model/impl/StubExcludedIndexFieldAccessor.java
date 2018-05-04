/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;
import org.hibernate.search.v6poc.util.impl.common.logging.Log;

class StubExcludedIndexFieldAccessor<T> implements IndexFieldAccessor<T> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final String absolutePath;
	private final String relativeFieldName;

	StubExcludedIndexFieldAccessor(String absolutePath, String relativeFieldName) {
		this.absolutePath = absolutePath;
		this.relativeFieldName = relativeFieldName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + absolutePath + "]";
	}

	@Override
	public void write(DocumentElement target, T value) {
		log.tracev( "Ignoring write on document element {}, field '{}' with value '{}'" +
				" because the field was excluded during bootstrap.", target, relativeFieldName, value );
	}
}
