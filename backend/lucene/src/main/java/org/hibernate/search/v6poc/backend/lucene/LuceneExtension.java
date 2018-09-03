/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldContext;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.FieldModelExtension;
import org.hibernate.search.v6poc.backend.lucene.document.model.dsl.LuceneIndexSchemaFieldContext;
import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.backend.lucene.search.dsl.predicate.LuceneSearchPredicateContainerContext;
import org.hibernate.search.v6poc.backend.lucene.search.dsl.predicate.impl.LuceneSearchPredicateContainerContextImpl;
import org.hibernate.search.v6poc.backend.lucene.search.dsl.sort.LuceneSearchSortContainerContext;
import org.hibernate.search.v6poc.backend.lucene.search.dsl.sort.impl.LuceneSearchSortContainerContextImpl;
import org.hibernate.search.v6poc.backend.lucene.search.predicate.impl.LuceneSearchPredicateBuilder;
import org.hibernate.search.v6poc.backend.lucene.search.predicate.impl.LuceneSearchPredicateFactory;
import org.hibernate.search.v6poc.backend.lucene.search.sort.impl.LuceneSearchSortBuilder;
import org.hibernate.search.v6poc.backend.lucene.search.sort.impl.LuceneSearchSortFactory;
import org.hibernate.search.v6poc.search.dsl.predicate.SearchPredicateContainerContext;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.SearchPredicateContainerContextExtension;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.SearchPredicateDslContext;
import org.hibernate.search.v6poc.search.dsl.sort.SearchSortContainerContext;
import org.hibernate.search.v6poc.search.dsl.sort.spi.SearchSortContainerContextExtension;
import org.hibernate.search.v6poc.search.dsl.sort.spi.SearchSortDslContext;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateFactory;
import org.hibernate.search.v6poc.search.sort.spi.SearchSortFactory;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

public final class LuceneExtension<N>
		implements SearchPredicateContainerContextExtension<N, LuceneSearchPredicateContainerContext<N>>,
		SearchSortContainerContextExtension<N, LuceneSearchSortContainerContext<N>>,
		FieldModelExtension<LuceneIndexSchemaFieldContext> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	@SuppressWarnings("rawtypes")
	private static final LuceneExtension INSTANCE = new LuceneExtension();

	@SuppressWarnings("unchecked")
	public static <N> LuceneExtension<N> get() {
		return INSTANCE;
	}

	private LuceneExtension() {
		// Private constructor, use get() instead.
	}

	@Override
	public <C, B> LuceneSearchPredicateContainerContext<N> extendOrFail(SearchPredicateContainerContext<N> original,
			SearchPredicateFactory<C, B> factory, SearchPredicateDslContext<N, ? super B> dslContext) {
		if ( factory instanceof LuceneSearchPredicateFactory ) {
			return extendUnsafe( original, (LuceneSearchPredicateFactory) factory, dslContext );
		}
		else {
			throw log.luceneExtensionOnUnknownType( factory );
		}
	}

	@Override
	public <C, B> Optional<LuceneSearchPredicateContainerContext<N>> extendOptional(
			SearchPredicateContainerContext<N> original, SearchPredicateFactory<C, B> factory,
			SearchPredicateDslContext<N, ? super B> dslContext) {
		if ( factory instanceof LuceneSearchPredicateFactory ) {
			return Optional.of( extendUnsafe( original, (LuceneSearchPredicateFactory) factory, dslContext ) );
		}
		else {
			return Optional.empty();
		}
	}

	@Override
	public <C, B> LuceneSearchSortContainerContext<N> extendOrFail(SearchSortContainerContext<N> original,
			SearchSortFactory<C, B> factory, SearchSortDslContext<N, ? super B> dslContext) {
		if ( factory instanceof LuceneSearchSortFactory ) {
			return extendUnsafe( original, (LuceneSearchSortFactory) factory, dslContext );
		}
		else {
			throw log.luceneExtensionOnUnknownType( factory );
		}
	}

	@Override
	public <C, B> Optional<LuceneSearchSortContainerContext<N>> extendOptional(
			SearchSortContainerContext<N> original, SearchSortFactory<C, B> factory,
			SearchSortDslContext<N, ? super B> dslContext) {
		if ( factory instanceof LuceneSearchSortFactory ) {
			return Optional.of( extendUnsafe( original, (LuceneSearchSortFactory) factory, dslContext ) );
		}
		else {
			return Optional.empty();
		}
	}

	@Override
	public LuceneIndexSchemaFieldContext extendOrFail(IndexSchemaFieldContext original) {
		if ( original instanceof LuceneIndexSchemaFieldContext ) {
			return (LuceneIndexSchemaFieldContext) original;
		}
		else {
			throw log.luceneExtensionOnUnknownType( original );
		}
	}

	@SuppressWarnings("unchecked") // If the target is Lucene, then we know B = LuceSearchPredicateBuilder
	private <B> LuceneSearchPredicateContainerContext<N> extendUnsafe(
			SearchPredicateContainerContext<N> original, LuceneSearchPredicateFactory factory,
			SearchPredicateDslContext<N, ? super B> dslContext) {
		return new LuceneSearchPredicateContainerContextImpl<>(
				original, factory,
				(SearchPredicateDslContext<N, ? super LuceneSearchPredicateBuilder>) dslContext
		);
	}

	@SuppressWarnings("unchecked") // If the target is Lucene, then we know B = LuceSearchSortBuilder
	private <B> LuceneSearchSortContainerContext<N> extendUnsafe(
			SearchSortContainerContext<N> original, LuceneSearchSortFactory factory,
			SearchSortDslContext<N, ? super B> dslContext) {
		return new LuceneSearchSortContainerContextImpl<>(
				original, factory,
				(SearchSortDslContext<N, ? super LuceneSearchSortBuilder>) dslContext
		);
	}
}
