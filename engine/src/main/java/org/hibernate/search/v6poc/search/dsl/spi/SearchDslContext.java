/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.spi;

/**
 * Represents the current context in the search DSL,
 * i.e. the current position in the predicate tree.
 */
public interface SearchDslContext<N, C> {

	/**
	 * Add a predicate contributor at the current position in the predicate tree.
	 *
	 * @param child The contributor to add.
	 */
	void addContributor(SearchPredicateContributor<C> child);

	/**
	 * @return The context that should be exposed to the user after the current predicate has been fully defined.
	 */
	N getNextContext();

}
