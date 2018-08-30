/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.dsl.impl;

import org.hibernate.search.v6poc.backend.document.model.dsl.Sortable;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaContext;
import org.hibernate.search.v6poc.backend.document.spi.IndexSchemaFieldDefinitionHelper;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaFieldNode;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.lucene.types.codec.impl.GeoPointFieldCodec;
import org.hibernate.search.v6poc.backend.lucene.types.converter.impl.GeoPointFieldConverter;
import org.hibernate.search.v6poc.backend.lucene.types.predicate.impl.GeoPointFieldPredicateBuilderFactory;
import org.hibernate.search.v6poc.backend.lucene.types.sort.impl.GeoPointFieldSortContributor;
import org.hibernate.search.v6poc.spatial.GeoPoint;

/**
 * @author Guillaume Smet
 */
public class GeoPointIndexSchemaFieldContext extends AbstractLuceneIndexSchemaFieldTypedContext<GeoPoint> {

	private Sortable sortable;

	public GeoPointIndexSchemaFieldContext(IndexSchemaContext schemaContext, String relativeFieldName) {
		super( schemaContext, relativeFieldName );
	}

	@Override
	public GeoPointIndexSchemaFieldContext sortable(Sortable sortable) {
		this.sortable = sortable;
		return this;
	}

	@Override
	protected void contribute(IndexSchemaFieldDefinitionHelper<GeoPoint> helper, LuceneIndexSchemaNodeCollector collector,
			LuceneIndexSchemaObjectNode parentNode) {
		LuceneIndexSchemaFieldNode<GeoPoint> schemaNode = new LuceneIndexSchemaFieldNode<>(
				parentNode,
				getRelativeFieldName(),
				GeoPointFieldConverter.INSTANCE,
				new GeoPointFieldCodec( parentNode.getAbsolutePath( getRelativeFieldName() ), getStore(), sortable ),
				GeoPointFieldPredicateBuilderFactory.INSTANCE,
				GeoPointFieldSortContributor.INSTANCE
		);

		helper.initialize( new LuceneIndexFieldAccessor<>( schemaNode ) );

		collector.collectFieldNode( schemaNode.getAbsoluteFieldPath(), schemaNode );
	}
}
