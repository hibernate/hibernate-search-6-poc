/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.query.impl;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.SearchQuery;
import org.hibernate.search.v6poc.search.dsl.predicate.SearchPredicateContainerContext;
import org.hibernate.search.v6poc.search.dsl.predicate.impl.BuildingRootSearchPredicateDslContextImpl;
import org.hibernate.search.v6poc.search.dsl.predicate.impl.QuerySearchPredicateDslContextImpl;
import org.hibernate.search.v6poc.search.dsl.predicate.impl.SearchPredicateContainerContextImpl;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.SearchPredicateDslContext;
import org.hibernate.search.v6poc.search.dsl.query.SearchQueryContext;
import org.hibernate.search.v6poc.search.dsl.query.SearchQueryResultContext;
import org.hibernate.search.v6poc.search.dsl.query.SearchQueryWrappingDefinitionResultContext;
import org.hibernate.search.v6poc.search.dsl.spi.SearchTargetContext;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateContributor;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateFactory;
import org.hibernate.search.v6poc.search.query.spi.SearchQueryBuilder;


public final class SearchQueryWrappingDefinitionResultContextImpl<T, C, Q>
		implements SearchQueryResultContext<Q>, SearchQueryWrappingDefinitionResultContext<Q> {

	private final SearchTargetContext<C> targetContext;

	private final SearchQueryBuilder<T, C> searchQueryBuilder;

	private final Function<SearchQuery<T>, Q> searchQueryWrapperFactory;

	public SearchQueryWrappingDefinitionResultContextImpl(SearchTargetContext<C> targetContext,
			SearchQueryBuilder<T, C> searchQueryBuilder,
			Function<SearchQuery<T>, Q> searchQueryWrapperFactory) {
		this.targetContext = targetContext;
		this.searchQueryBuilder = searchQueryBuilder;
		this.searchQueryWrapperFactory = searchQueryWrapperFactory;
	}

	public SearchQueryWrappingDefinitionResultContextImpl(
			SearchQueryWrappingDefinitionResultContextImpl<T, C, ?> original,
			Function<SearchQuery<T>, Q> searchQueryWrapperFactory) {
		this( original.targetContext, original.searchQueryBuilder, searchQueryWrapperFactory );
	}

	@Override
	public <R> SearchQueryResultContext<R> asWrappedQuery(Function<Q, R> wrapperFactory) {
		return new SearchQueryWrappingDefinitionResultContextImpl<>( this,
				searchQueryWrapperFactory.andThen( wrapperFactory ) );
	}

	@Override
	public SearchQueryContext<Q> predicate(SearchPredicate predicate) {
		SearchPredicateFactory<? super C> factory = targetContext.getSearchPredicateFactory();
		factory.toContributor( predicate )
				.contribute( searchQueryBuilder.getQueryElementCollector() );
		return getNext();
	}

	@Override
	public SearchQueryContext<Q> predicate(Consumer<? super SearchPredicateContainerContext<SearchPredicate>> dslPredicateContributor) {
		toContributor( targetContext.getSearchPredicateFactory(), dslPredicateContributor )
				.contribute( searchQueryBuilder.getQueryElementCollector() );
		return getNext();
	}

	@Override
	public SearchPredicateContainerContext<SearchQueryContext<Q>> predicate() {
		SearchPredicateDslContext<SearchQueryContext<Q>, C> dslContext =
				new QuerySearchPredicateDslContextImpl<>( searchQueryBuilder.getQueryElementCollector(), this::getNext );
		return new SearchPredicateContainerContextImpl<>( targetContext.getSearchPredicateFactory(), dslContext );
	}

	private <PC> SearchPredicateContributor<PC> toContributor(SearchPredicateFactory<PC> factory,
			Consumer<? super SearchPredicateContainerContext<SearchPredicate>> dslPredicateContributor) {
		BuildingRootSearchPredicateDslContextImpl<PC> dslContext =
				new BuildingRootSearchPredicateDslContextImpl<>( factory );
		SearchPredicateContainerContext<SearchPredicate> containerContext =
				new SearchPredicateContainerContextImpl<>( factory, dslContext );
		dslPredicateContributor.accept( containerContext );
		return dslContext;
	}

	private SearchQueryContext<Q> getNext() {
		return new SearchQueryContextImpl<>( targetContext, searchQueryBuilder, searchQueryWrapperFactory );
	}

}
