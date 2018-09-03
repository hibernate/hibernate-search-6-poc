/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.index.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.elasticsearch.document.impl.ElasticsearchDocumentObjectBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexModel;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.backend.elasticsearch.orchestration.impl.ElasticsearchWorkOrchestrator;
import org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl.SearchBackendContext;
import org.hibernate.search.v6poc.backend.elasticsearch.util.impl.URLEncodedString;
import org.hibernate.search.v6poc.backend.index.spi.IndexWorkPlan;
import org.hibernate.search.v6poc.backend.index.spi.IndexManagerImplementor;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTargetBuilder;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;


/**
 * @author Yoann Rodiere
 */
public class ElasticsearchIndexManager implements IndexManagerImplementor<ElasticsearchDocumentObjectBuilder> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final IndexingBackendContext indexingBackendContext;
	private final SearchBackendContext searchBackendContext;

	private final String hibernateSearchIndexName;
	private final URLEncodedString elasticsearchIndexName;
	private final URLEncodedString typeName;
	private final ElasticsearchIndexModel model;

	private final ElasticsearchWorkOrchestrator workPlanOrchestrator;

	ElasticsearchIndexManager(IndexingBackendContext indexingBackendContext, SearchBackendContext searchBackendContext,
			String hibernateSearchIndexName, URLEncodedString elasticsearchIndexName, URLEncodedString typeName,
			ElasticsearchIndexModel model) {
		this.indexingBackendContext = indexingBackendContext;
		this.searchBackendContext = searchBackendContext;
		this.hibernateSearchIndexName = hibernateSearchIndexName;
		this.elasticsearchIndexName = elasticsearchIndexName;
		this.typeName = typeName;
		this.model = model;
		this.workPlanOrchestrator = indexingBackendContext.createWorkPlanOrchestrator();
	}

	@Override
	public void close() {
		// Index managers own the work plan context, but not the stream context (which is shared)
		workPlanOrchestrator.close();
	}

	public ElasticsearchIndexModel getModel() {
		return model;
	}

	@Override
	public IndexWorkPlan<ElasticsearchDocumentObjectBuilder> createWorkPlan(SessionContext sessionContext) {
		return indexingBackendContext.createWorkPlan( workPlanOrchestrator, elasticsearchIndexName, typeName, sessionContext );
	}

	@Override
	public IndexSearchTargetBuilder createSearchTarget() {
		return new ElasticsearchIndexSearchTargetBuilder( searchBackendContext, this );
	}

	@Override
	public void addToSearchTarget(IndexSearchTargetBuilder searchTargetBuilder) {
		if ( ! (searchTargetBuilder instanceof ElasticsearchIndexSearchTargetBuilder ) ) {
			throw log.cannotMixElasticsearchSearchTargetWithOtherType(
					searchTargetBuilder, this, searchBackendContext.getEventContext()
			);
		}

		ElasticsearchIndexSearchTargetBuilder esSearchTargetBuilder = (ElasticsearchIndexSearchTargetBuilder) searchTargetBuilder;
		esSearchTargetBuilder.add( searchBackendContext, this );
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "name=" ).append( hibernateSearchIndexName )
				.append( "elasticsearchName=" ).append( elasticsearchIndexName.original )
				.append( "]" )
				.toString();
	}

}
