/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.impl;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.ObjectFieldStorage;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;


/**
 * @author Guillaume Smet
 */
public class LuceneIndexObjectFieldAccessor implements IndexObjectFieldAccessor {

	private final LuceneIndexSchemaObjectNode schemaNode;

	private final ObjectFieldStorage storage;

	public LuceneIndexObjectFieldAccessor(LuceneIndexSchemaObjectNode schemaNode, ObjectFieldStorage storage) {
		this.schemaNode = schemaNode;
		this.storage = storage;
	}

	@Override
	public DocumentElement add(DocumentElement target) {
		LuceneDocumentBuilder currentDocumentBuilder = (LuceneDocumentBuilder) target;

		currentDocumentBuilder.checkTreeConsistency( schemaNode.getParent() );

		switch ( storage ) {
			case NESTED:
				LuceneNestedObjectDocumentBuilder nestedDocumentBuilder = new LuceneNestedObjectDocumentBuilder( schemaNode );
				currentDocumentBuilder.addNestedObjectDocumentBuilder( nestedDocumentBuilder );

				return nestedDocumentBuilder;
			default:
				LuceneFlattenedObjectDocumentBuilder flattenedDocumentBuilder = new LuceneFlattenedObjectDocumentBuilder( schemaNode );
				currentDocumentBuilder.addFlattenedObjectDocumentBuilder( flattenedDocumentBuilder );

				return flattenedDocumentBuilder;
		}
	}

	@Override
	public void addMissing(DocumentElement target) {
		// we ignore the missing element
	}
}
