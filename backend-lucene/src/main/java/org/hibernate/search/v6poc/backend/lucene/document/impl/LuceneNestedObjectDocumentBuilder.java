/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.impl;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneFields;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;

/**
 * @author Guillaume Smet
 */
class LuceneNestedObjectDocumentBuilder extends AbstractLuceneDocumentBuilder {

	private final Document nestedDocument = new Document();

	LuceneNestedObjectDocumentBuilder(LuceneIndexSchemaObjectNode schemaNode) {
		super( schemaNode );
	}

	@Override
	public void addField(LuceneIndexSchemaObjectNode expectedParentNode, IndexableField field) {
		checkTreeConsistency( expectedParentNode );

		nestedDocument.add( field );
	}

	@Override
	void contribute(String rootIndexName, String tenantId, String rootId, Document currentDocument, List<Document> nestedDocuments) {
		nestedDocument.add( new StringField( LuceneFields.typeFieldName(), LuceneFields.TYPE_CHILD_DOCUMENT, Store.YES ) );
		nestedDocument.add( new StringField( LuceneFields.rootIndexFieldName(), rootIndexName, Store.YES ) );
		if ( tenantId != null ) {
			nestedDocument.add( new StringField( LuceneFields.tenantIdFieldName(), tenantId, Store.YES ) );
		}
		nestedDocument.add( new StringField( LuceneFields.rootIdFieldName(), rootId, Store.YES ) );
		nestedDocument.add( new StringField( LuceneFields.nestedDocumentPathFieldName(), schemaNode.getAbsolutePath(), Store.YES ) );

		nestedDocuments.add( nestedDocument );

		super.contribute( rootIndexName, tenantId, rootId, nestedDocument, nestedDocuments );
	}
}
