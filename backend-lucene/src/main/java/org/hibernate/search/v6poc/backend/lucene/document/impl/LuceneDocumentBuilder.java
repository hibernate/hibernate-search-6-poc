/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.impl;

import org.apache.lucene.index.IndexableField;
import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;

/**
 * @author Guillaume Smet
 */
public interface LuceneDocumentBuilder extends DocumentElement {

	void addField(LuceneIndexSchemaObjectNode expectedParentNode, IndexableField field);

	void addNestedObjectDocumentBuilder(LuceneIndexSchemaObjectNode parentNode, LuceneNestedObjectDocumentBuilder nestedObjectDocumentBuilder);

	void addFlattenedObjectDocumentBuilder(LuceneIndexSchemaObjectNode parentNode, LuceneFlattenedObjectDocumentBuilder flattenedObjectDocumentBuilder);
}
