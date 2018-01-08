/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.spi;

import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateFactory;
import org.hibernate.search.v6poc.search.query.spi.SearchQueryFactory;

/**
 * The target context during a search, aware of the targeted indexes and of the underlying technology (backend).
 */
public interface SearchTargetContext<C> {

	SearchPredicate toSearchPredicate(SearchPredicateContributor<C> contributor);

	SearchPredicateContributor<C> toContributor(SearchPredicate predicate);

	SearchPredicateFactory<C> getSearchPredicateFactory();

	SearchQueryFactory<C> getSearchQueryFactory();

}
