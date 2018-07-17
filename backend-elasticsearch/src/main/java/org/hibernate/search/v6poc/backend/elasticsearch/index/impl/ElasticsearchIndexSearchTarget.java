/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.index.impl;

import java.util.function.Function;

import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchTargetModel;
import org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl.ElasticsearchSearchTargetContext;
import org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl.SearchBackendContext;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTargetBase;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.search.ObjectLoader;
import org.hibernate.search.v6poc.search.dsl.query.SearchQueryResultDefinitionContext;
import org.hibernate.search.v6poc.search.dsl.query.spi.SearchQueryResultDefinitionContextImpl;
import org.hibernate.search.v6poc.search.dsl.spi.SearchTargetContext;

class ElasticsearchIndexSearchTarget extends IndexSearchTargetBase {

	private final ElasticsearchSearchTargetModel searchTargetModel;
	private final SearchTargetContext<?> searchTargetContext;

	ElasticsearchIndexSearchTarget(SearchBackendContext searchBackendContext,
			ElasticsearchSearchTargetModel searchTargetModel) {
		this.searchTargetModel = searchTargetModel;
		this.searchTargetContext = new ElasticsearchSearchTargetContext( searchBackendContext, searchTargetModel );
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "indexNames=" ).append( searchTargetModel.getHibernateSearchIndexNames() )
				.append( "]" )
				.toString();
	}

	@Override
	public <R, O> SearchQueryResultDefinitionContext<R, O> query(
			SessionContext context,
			Function<DocumentReference, R> documentReferenceTransformer,
			ObjectLoader<R, O> objectLoader) {
		return new SearchQueryResultDefinitionContextImpl<>( searchTargetContext, context,
				documentReferenceTransformer, objectLoader );
	}

	@Override
	protected SearchTargetContext<?> getSearchTargetContext() {
		return searchTargetContext;
	}
}
