/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.spi;


import java.util.Optional;

import org.hibernate.search.v6poc.search.dsl.predicate.SearchPredicateContainerContext;

/**
 * An extension to the search query DSL, allowing to add non-standard predicates to a query.
 *
 * @param <N> The next context type
 * @param <T> The type of extended search container contexts. Should generally extend
 * {@link SearchPredicateContainerContext}.
 *
 * @see SearchPredicateContainerContext#withExtension(SearchPredicateContainerContextExtension)
 * @see DelegatingSearchPredicateContainerContextImpl
 */
public interface SearchPredicateContainerContextExtension<N, T> {

	/**
	 * Attempt to extend a given context, throwing an exception in case of failure.
	 *
	 * @param original The original, non-extended {@link SearchPredicateContainerContext}.
	 * @param targetContext A {@link SearchTargetContext}.
	 * @param dslContext A {@link SearchDslContext}.
	 * @param <C> The type of collector expected by search predicate contributors for the given search target.
	 * @return An extended search predicate container context ({@link T})
	 * @throws org.hibernate.search.v6poc.util.SearchException If the current extension does not support the given
	 * search target (incompatible technology).
	 */
	<C> T extendOrFail(SearchPredicateContainerContext<N> original,
			SearchTargetContext<C> targetContext, SearchDslContext<N, C> dslContext);

	/**
	 * Attempt to extend a given context, returning an empty {@link Optional} in case of failure.
	 *
	 * @param original The original, non-extended {@link SearchPredicateContainerContext}.
	 * @param targetContext A {@link SearchTargetContext}.
	 * @param dslContext A {@link SearchDslContext}.
	 * @param <C> The type of collector expected by search predicate contributors for the given search target.
	 * @return An optional containing the extended search predicate container context ({@link T}) in case
	 * of success, or an empty optional otherwise.
	 */
	<C> Optional<T> extendOptional(SearchPredicateContainerContext<N> original,
			SearchTargetContext<C> targetContext, SearchDslContext<N, C> dslContext);

}
