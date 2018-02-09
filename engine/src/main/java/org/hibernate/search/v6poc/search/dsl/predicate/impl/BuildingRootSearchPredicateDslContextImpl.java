/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.predicate.impl;

import org.hibernate.search.v6poc.logging.impl.Log;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.SearchPredicateDslContext;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateContributor;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateFactory;
import org.hibernate.search.v6poc.util.spi.LoggerFactory;

public final class BuildingRootSearchPredicateDslContextImpl<C>
		implements SearchPredicateDslContext<SearchPredicate, C>, SearchPredicateContributor<C> {

	private static final Log log = LoggerFactory.make( Log.class );

	private final SearchPredicateFactory<C> factory;

	private SearchPredicateContributor<C> singlePredicateContributor;

	public BuildingRootSearchPredicateDslContextImpl(SearchPredicateFactory<C> factory) {
		this.factory = factory;
	}

	@Override
	public void addContributor(SearchPredicateContributor<C> child) {
		if ( this.singlePredicateContributor != null ) {
			throw log.cannotAddMultiplePredicatesToQueryRoot();
		}
		this.singlePredicateContributor = child;
	}

	@Override
	public SearchPredicate getNextContext() {
		return factory.toSearchPredicate( singlePredicateContributor );
	}

	@Override
	public void contribute(C collector) {
		singlePredicateContributor.contribute( collector );
	}
}
