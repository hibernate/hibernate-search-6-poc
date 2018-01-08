/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.predicate.spi;

/**
 * A factory for search predicates.
 * <p>
 * This is the main entry point for the engine
 * to ask the backend to build search predicates.
 *
 * @param <C> The type of predicate collector the builders contributor will contribute to.
 * This type is backend-specific. See {@link SearchPredicateBuilder#contribute(Object)}
 */
public interface SearchPredicateFactory<C> {

	BooleanJunctionPredicateBuilder<C> bool();

	MatchPredicateBuilder<C> match(String absoluteFieldPath);

	RangePredicateBuilder<C> range(String absoluteFieldPath);

}
