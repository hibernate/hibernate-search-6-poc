/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.spi;

import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.dsl.predicate.impl.SearchPredicateContainerContextImpl;

public final class SearchTargetPredicateRootContext<C> extends SearchPredicateContainerContextImpl<SearchPredicate, C> {

	public SearchTargetPredicateRootContext(SearchTargetContext<C> targetContext) {
		super( targetContext, new QuerySearchPredicateBuildingRootContextImpl<>( targetContext ) );
	}

}
