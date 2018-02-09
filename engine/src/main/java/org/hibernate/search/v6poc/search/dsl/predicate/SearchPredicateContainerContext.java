/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.predicate;


import java.util.function.Consumer;

import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.SearchPredicateContainerContextExtension;

/**
 * A context allowing to specify the type of a predicate.
 *
 * @param <N> The type of the next context (returned by terminal calls such as {@link BooleanJunctionPredicateContext#end()}
 * or {@link MatchPredicateFieldSetContext#matching(Object)}).
 */
public interface SearchPredicateContainerContext<N> {

	AllPredicateContext<N> all();

	BooleanJunctionPredicateContext<N> bool();

	MatchPredicateContext<N> match();

	RangePredicateContext<N> range();

	NestedPredicateContext<N> nested();

	N predicate(SearchPredicate predicate);

	// TODO ids query (Type + list of IDs? Just IDs? See https://www.elastic.co/guide/en/elasticsearch/reference/5.5/query-dsl-ids-query.html)
	// TODO other queries (spatial, ...)

	<T> T withExtension(SearchPredicateContainerContextExtension<N, T> extension);

	<T> N withExtensionOptional(SearchPredicateContainerContextExtension<N, T> extension, Consumer<T> clauseContributor);

	<T> N withExtensionOptional(SearchPredicateContainerContextExtension<N, T> extension, Consumer<T> clauseContributor,
			Consumer<SearchPredicateContainerContext<N>> fallbackClauseContributor);

}
