/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.spi;

/**
 * A search predicate contributor, i.e. an object that will push search predicates to a collector.
 *
 * @param <C> The type of predicate collector this contributor will contribute to.
 * This type is backend-specific.
 */
public interface SearchPredicateContributor<C> {

	/**
	 * Add zero or more predicates to the given collector.
	 *
	 * @param collector The collector to push search predicates to.
	 */
	void contribute(C collector);

}
