/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.impl;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaFieldNode;


/**
 * @author Guillaume Smet
 */
public class LuceneIndexFieldAccessor<F> implements IndexFieldAccessor<F> {

	private final LuceneIndexSchemaFieldNode<F> schemaNode;

	public LuceneIndexFieldAccessor(LuceneIndexSchemaFieldNode<F> schemaNode) {
		this.schemaNode = schemaNode;
	}

	@Override
	public void write(DocumentElement target, F value) {
		LuceneDocumentBuilder documentBuilder = (LuceneDocumentBuilder) target;
		documentBuilder.checkTreeConsistency( schemaNode.getParent() );
		schemaNode.getCodec().encode( documentBuilder, schemaNode.getAbsoluteFieldPath(), value );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[schemaNode=" + schemaNode + "]";
	}
}
