/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.index.impl;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexModel;
import org.hibernate.search.v6poc.backend.elasticsearch.impl.ElasticsearchBackend;
import org.hibernate.search.v6poc.backend.elasticsearch.search.query.impl.ElasticsearchSearchTargetContextImpl;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTarget;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.search.ObjectLoader;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.dsl.predicate.SearchPredicateContainerContext;
import org.hibernate.search.v6poc.search.dsl.spi.SearchTargetContext;
import org.hibernate.search.v6poc.search.dsl.spi.SearchTargetPredicateRootContext;
import org.hibernate.search.v6poc.search.dsl.query.SearchQueryResultDefinitionContext;
import org.hibernate.search.v6poc.search.dsl.spi.SearchQueryResultDefinitionContextImpl;


/**
 * @author Yoann Rodiere
 */
class ElasticsearchIndexSearchTarget implements IndexSearchTarget {

	private final SearchTargetContext<?> searchTargetContext;

	private final Set<String> indexNames;

	ElasticsearchIndexSearchTarget(ElasticsearchBackend backend, Set<ElasticsearchIndexModel> indexModels) {
		this.searchTargetContext = new ElasticsearchSearchTargetContextImpl( backend, indexModels );
		this.indexNames = indexModels.stream()
				.map( ElasticsearchIndexModel::getIndexName )
				.collect( Collectors.toSet() );
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
	public SearchPredicateContainerContext<SearchPredicate> predicate() {
		return new SearchTargetPredicateRootContext<>( searchTargetContext );
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "indexNames=" ).append( indexNames )
				.append( "]")
				.toString();
	}
}
