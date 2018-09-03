/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.index.impl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.backend.index.spi.IndexManagerBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneRootDocumentBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.model.dsl.impl.LuceneIndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexModel;
import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.backend.lucene.search.query.impl.SearchBackendContext;
import org.hibernate.search.v6poc.backend.spi.BackendBuildContext;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.util.EventContext;
import org.hibernate.search.v6poc.logging.spi.EventContexts;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;
import org.hibernate.search.v6poc.util.impl.common.SuppressingCloser;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

/**
 * @author Guillaume Smet
 */
public class LuceneDirectoryIndexManagerBuilder implements IndexManagerBuilder<LuceneRootDocumentBuilder> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final IndexingBackendContext indexingBackendContext;
	private final SearchBackendContext searchBackendContext;

	private final String indexName;
	private final LuceneIndexSchemaRootNodeBuilder schemaRootNodeBuilder;

	private final BackendBuildContext buildContext;
	private final ConfigurationPropertySource propertySource;

	public LuceneDirectoryIndexManagerBuilder(IndexingBackendContext indexingBackendContext,
			SearchBackendContext searchBackendContext,
			String indexName,
			BackendBuildContext buildContext, ConfigurationPropertySource propertySource) {
		this.indexingBackendContext = indexingBackendContext;
		this.searchBackendContext = searchBackendContext;
		this.indexName = indexName;
		this.schemaRootNodeBuilder = new LuceneIndexSchemaRootNodeBuilder( indexName );
		this.buildContext = buildContext;
		this.propertySource = propertySource;
	}

	@Override
	public void closeOnFailure() {
		// Nothing to do
	}

	@Override
	public IndexSchemaRootNodeBuilder getSchemaRootNodeBuilder() {
		return schemaRootNodeBuilder;
	}

	@Override
	public LuceneDirectoryIndexManager build() {
		LuceneIndexModel model = null;
		IndexWriter indexWriter = null;
		try {
			model = new LuceneIndexModel( indexName, schemaRootNodeBuilder );
			indexWriter = createIndexWriter( model );
			return new LuceneDirectoryIndexManager(
					indexingBackendContext, searchBackendContext, indexName, model, indexWriter
			);
		}
		catch (RuntimeException e) {
			new SuppressingCloser( e )
					.push( model )
					.push( indexWriter );
			throw e;
		}
	}

	private IndexWriter createIndexWriter(LuceneIndexModel model) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig( model.getScopedAnalyzer() );
		try {
			Directory directory = indexingBackendContext.createDirectory( indexName );
			try {
				return new IndexWriter( directory, indexWriterConfig );
			}
			catch (RuntimeException e) {
				new SuppressingCloser( e ).push( directory );
				throw e;
			}
		}
		catch (IOException | RuntimeException e) {
			throw log.unableToCreateIndexWriter( getEventContext(), e );
		}
	}

	private EventContext getEventContext() {
		return indexingBackendContext.getEventContext().append(
				EventContexts.fromIndexName( indexName )
		);
	}
}
