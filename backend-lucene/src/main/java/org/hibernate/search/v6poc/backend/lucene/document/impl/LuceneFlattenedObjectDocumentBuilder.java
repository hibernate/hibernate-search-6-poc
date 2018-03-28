/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;

/**
 * @author Guillaume Smet
 */
class LuceneFlattenedObjectDocumentBuilder extends AbstractLuceneDocumentBuilder {

	private final Set<IndexableField> fields = new HashSet<>();

	LuceneFlattenedObjectDocumentBuilder(LuceneIndexSchemaObjectNode schemaNode) {
		super( schemaNode );
	}

	@Override
	public void addField(LuceneIndexSchemaObjectNode expectedParentNode, IndexableField field) {
		checkTreeConsistency( expectedParentNode );

		fields.add( field );
	}

	@Override
	void contribute(String rootIndexName, String tenantId, String rootId, Document currentDocument, List<Document> nestedDocuments) {
		for ( IndexableField field : fields ) {
			currentDocument.add( field );
		}

		super.contribute( rootIndexName, tenantId, rootId, currentDocument, nestedDocuments );
	}
}
