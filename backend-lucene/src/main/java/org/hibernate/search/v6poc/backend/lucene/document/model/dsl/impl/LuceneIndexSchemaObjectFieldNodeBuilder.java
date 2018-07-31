/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.model.dsl.impl;

import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.ObjectFieldStorage;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaObjectFieldNodeBuilder;
import org.hibernate.search.v6poc.backend.document.spi.IndexSchemaObjectFieldDefinitionHelper;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.lucene.util.impl.LuceneFields;
import org.hibernate.search.v6poc.util.EventContext;
import org.hibernate.search.v6poc.logging.spi.EventContexts;

class LuceneIndexSchemaObjectFieldNodeBuilder extends AbstractLuceneIndexSchemaObjectNodeBuilder
		implements IndexSchemaObjectFieldNodeBuilder, LuceneIndexSchemaNodeContributor {

	private final AbstractLuceneIndexSchemaObjectNodeBuilder parent;
	private final String absoluteFieldPath;
	private final ObjectFieldStorage storage;

	private final IndexSchemaObjectFieldDefinitionHelper helper;

	LuceneIndexSchemaObjectFieldNodeBuilder(AbstractLuceneIndexSchemaObjectNodeBuilder parent,
			String relativeFieldName, ObjectFieldStorage storage) {
		this.parent = parent;
		this.absoluteFieldPath = LuceneFields.compose( parent.getAbsolutePath(), relativeFieldName );
		this.storage = storage;
		this.helper = new IndexSchemaObjectFieldDefinitionHelper( this );
	}

	@Override
	public EventContext getEventContext() {
		return getRootNodeBuilder().getIndexEventContext()
				.append( EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath ) );
	}

	@Override
	public IndexObjectFieldAccessor createAccessor() {
		return helper.createAccessor();
	}

	@Override
	public void contribute(LuceneIndexSchemaNodeCollector collector, LuceneIndexSchemaObjectNode parentNode) {
		LuceneIndexSchemaObjectNode node = new LuceneIndexSchemaObjectNode( parentNode, absoluteFieldPath, storage );
		collector.collectObjectNode( absoluteFieldPath, node );

		helper.initialize( new LuceneIndexObjectFieldAccessor( node, storage ) );

		contributeChildren( node, collector );
	}

	@Override
	LuceneIndexSchemaRootNodeBuilder getRootNodeBuilder() {
		return parent.getRootNodeBuilder();
	}

	@Override
	String getAbsolutePath() {
		return absoluteFieldPath;
	}
}
