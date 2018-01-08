/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.dsl.impl;

import java.util.Arrays;
import java.util.function.Supplier;

import org.hibernate.search.v6poc.search.dsl.predicate.MatchPredicateContext;
import org.hibernate.search.v6poc.search.dsl.predicate.MatchPredicateFieldSetContext;
import org.hibernate.search.v6poc.search.dsl.spi.SearchPredicateContributor;
import org.hibernate.search.v6poc.search.dsl.spi.SearchTargetContext;


/**
 * @author Yoann Rodiere
 */
class MatchPredicateContextImpl<N, C> implements MatchPredicateContext<N>, SearchPredicateContributor<C> {

	private final MatchPredicateFieldSetContextImpl.CommonState<N, C> commonState;

	public MatchPredicateContextImpl(SearchTargetContext<C> targetContext, Supplier<N> nextContextProvider) {
		this.commonState = new MatchPredicateFieldSetContextImpl.CommonState<>( targetContext, nextContextProvider );
	}

	@Override
	public MatchPredicateFieldSetContext<N> onFields(String ... fieldName) {
		return new MatchPredicateFieldSetContextImpl<>( commonState, Arrays.asList( fieldName ) );
	}

	@Override
	public void contribute(C collector) {
		commonState.contribute( collector );
	}

}
