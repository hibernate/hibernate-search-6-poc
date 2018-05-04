/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.model.dsl.impl;

import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.spi.DeferredInitializationIndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.ObjectFieldStorage;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaObjectFieldNodeBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.util.impl.LuceneFields;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;

class LuceneIndexSchemaObjectFieldNodeBuilder extends AbstractLuceneIndexSchemaObjectNodeBuilder
		implements IndexSchemaObjectFieldNodeBuilder, LuceneIndexSchemaNodeContributor {

	private final DeferredInitializationIndexObjectFieldAccessor accessor =
			new DeferredInitializationIndexObjectFieldAccessor();

	private final String absoluteFieldPath;
	private final ObjectFieldStorage storage;

	LuceneIndexSchemaObjectFieldNodeBuilder(String parentPath, String relativeFieldName,
			ObjectFieldStorage storage) {
		this.absoluteFieldPath = LuceneFields.compose( parentPath, relativeFieldName );
		this.storage = storage;
	}

	@Override
	public String getAbsolutePath() {
		return absoluteFieldPath;
	}

	@Override
	public IndexObjectFieldAccessor getAccessor() {
		return accessor;
	}

	@Override
	public void contribute(LuceneIndexSchemaNodeCollector collector, LuceneIndexSchemaObjectNode parentNode) {
		LuceneIndexSchemaObjectNode node = new LuceneIndexSchemaObjectNode( parentNode, absoluteFieldPath, storage );
		collector.collectObjectNode( absoluteFieldPath, node );

		accessor.initialize( new LuceneIndexObjectFieldAccessor( node, storage ) );

		contributeChildren( node, collector );
	}
}
