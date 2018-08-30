/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.dsl.impl;

import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldTerminalContext;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaContext;
import org.hibernate.search.v6poc.backend.document.spi.IndexSchemaFieldDefinitionHelper;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.document.model.LuceneFieldContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.LuceneFieldValueExtractor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaFieldNode;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.lucene.types.codec.impl.LuceneFieldFieldCodec;
import org.hibernate.search.v6poc.backend.lucene.types.formatter.impl.SimpleCastingFieldFormatter;

/**
 * @author Guillaume Smet
 */
public class LuceneFieldIndexSchemaFieldContext<F>
		implements IndexSchemaFieldTerminalContext<F>, LuceneIndexSchemaNodeContributor {

	private final IndexSchemaFieldDefinitionHelper<F> helper;
	private final String relativeFieldName;
	private final LuceneFieldContributor<F> fieldContributor;
	private final LuceneFieldValueExtractor<F> fieldValueExtractor;

	public LuceneFieldIndexSchemaFieldContext(IndexSchemaContext schemaContext, String relativeFieldName,
			LuceneFieldContributor<F> fieldContributor, LuceneFieldValueExtractor<F> fieldValueExtractor) {
		this.helper = new IndexSchemaFieldDefinitionHelper<>( schemaContext );
		this.relativeFieldName = relativeFieldName;
		this.fieldContributor = fieldContributor;
		this.fieldValueExtractor = fieldValueExtractor;
	}

	@Override
	public IndexFieldAccessor<F> createAccessor() {
		return helper.createAccessor();
	}

	@Override
	public void contribute(LuceneIndexSchemaNodeCollector collector, LuceneIndexSchemaObjectNode parentNode) {
		LuceneIndexSchemaFieldNode<F> schemaNode = new LuceneIndexSchemaFieldNode<F>(
				parentNode,
				relativeFieldName,
				new SimpleCastingFieldFormatter<>(),
				new LuceneFieldFieldCodec<>( fieldContributor, fieldValueExtractor ),
				null,
				null
		);

		helper.initialize( new LuceneIndexFieldAccessor<>( schemaNode ) );

		collector.collectFieldNode( schemaNode.getAbsoluteFieldPath(), schemaNode );
	}
}
