/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.index.impl;

import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.dsl.impl.ElasticsearchIndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.util.impl.URLEncodedString;
import org.hibernate.search.v6poc.backend.elasticsearch.document.impl.ElasticsearchDocumentObjectBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexModel;
import org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl.SearchBackendContext;
import org.hibernate.search.v6poc.backend.index.spi.IndexManagerBuilder;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.engine.spi.BuildContext;

/**
 * @author Yoann Rodiere
 */
public class ElasticsearchIndexManagerBuilder implements IndexManagerBuilder<ElasticsearchDocumentObjectBuilder> {

	private final IndexingBackendContext indexingBackendContext;
	private final SearchBackendContext searchBackendContext;

	private final String indexName;
	private final ElasticsearchIndexSchemaRootNodeBuilder schemaRootNodeBuilder;

	private final BuildContext buildContext;
	private final ConfigurationPropertySource propertySource;

	public ElasticsearchIndexManagerBuilder(IndexingBackendContext indexingBackendContext,
			SearchBackendContext searchBackendContext,
			String indexName, ElasticsearchIndexSchemaRootNodeBuilder schemaRootNodeBuilder,
			BuildContext buildContext, ConfigurationPropertySource propertySource) {
		this.indexingBackendContext = indexingBackendContext;
		this.searchBackendContext = searchBackendContext;

		this.indexName = indexName;
		this.schemaRootNodeBuilder = schemaRootNodeBuilder;

		this.buildContext = buildContext;
		this.propertySource = propertySource;
	}

	@Override
	public IndexSchemaRootNodeBuilder getSchemaRootNodeBuilder() {
		return schemaRootNodeBuilder;
	}

	@Override
	public ElasticsearchIndexManager build() {
		URLEncodedString encodedIndexName = URLEncodedString.fromString( indexName );
		// TODO find out what to do with type names: what's the point if there is only one type per index anyway?
		URLEncodedString encodedTypeName = URLEncodedString.fromString( "typeName" );

		ElasticsearchIndexModel model = new ElasticsearchIndexModel( encodedIndexName, schemaRootNodeBuilder );

		// TODO make sure index initialization is performed in parallel for all indexes?
		indexingBackendContext.initializeIndex( encodedIndexName, encodedTypeName, model )
				.join();

		return new ElasticsearchIndexManager(
				indexingBackendContext, searchBackendContext, encodedIndexName, encodedTypeName, model
		);
	}

}
