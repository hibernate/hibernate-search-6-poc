/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.dsl.impl;

import org.hibernate.search.v6poc.backend.document.spi.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaFieldNode;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.lucene.types.codec.impl.GeoPointFieldCodec;
import org.hibernate.search.v6poc.backend.lucene.types.formatter.impl.GeoPointFieldFormatter;
import org.hibernate.search.v6poc.backend.lucene.types.predicate.impl.GeoPointFieldPredicateBuilderFactory;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;

/**
 * @author Guillaume Smet
 */
public class GeoPointIndexSchemaFieldContext extends AbstractLuceneIndexSchemaFieldTypedContext<GeoPoint> {

	public GeoPointIndexSchemaFieldContext(String relativeFieldName) {
		super( relativeFieldName );
	}

	@Override
	protected void contribute(DeferredInitializationIndexFieldAccessor<GeoPoint> accessor, LuceneIndexSchemaNodeCollector collector,
			LuceneIndexSchemaObjectNode parentNode) {
		LuceneIndexSchemaFieldNode<GeoPoint> schemaNode = new LuceneIndexSchemaFieldNode<>(
				parentNode,
				getRelativeFieldName(),
				GeoPointFieldFormatter.INSTANCE,
				new GeoPointFieldCodec( parentNode.getAbsolutePath( getRelativeFieldName() ), getStore() ),
				GeoPointFieldPredicateBuilderFactory.INSTANCE,
				null // for now we don't have a sort contributor for GeoPoint
		);

		accessor.initialize( new LuceneIndexFieldAccessor<>( schemaNode ) );

		collector.collectFieldNode( schemaNode.getAbsoluteFieldPath(), schemaNode );
	}
}
