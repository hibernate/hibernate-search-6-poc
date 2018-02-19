/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.util.common.stub.backend.document.model.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.integrationtest.util.common.stub.backend.document.StubDocumentNode;
import org.hibernate.search.v6poc.integrationtest.util.common.stub.backend.document.impl.StubDocumentElement;
import org.hibernate.search.v6poc.logging.impl.Log;
import org.hibernate.search.v6poc.util.spi.LoggerFactory;

class StubExcludedIndexObjectFieldAccessor implements IndexObjectFieldAccessor {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final String relativeName;

	StubExcludedIndexObjectFieldAccessor(String relativeName) {
		this.relativeName = relativeName;
	}

	@Override
	public DocumentElement add(DocumentElement target) {
		log.tracev( "Ignoring add on document element {}, object field '{}'" +
				" because the field was excluded during bootstrap.", target, relativeName );
		return new StubDocumentElement( StubDocumentNode.object() );
	}

	@Override
	public void addMissing(DocumentElement target) {
		log.tracev( "Ignoring add missing on document element {}, object field '{}'" +
				" because the field was excluded during bootstrap.", target, relativeName );
	}
}
