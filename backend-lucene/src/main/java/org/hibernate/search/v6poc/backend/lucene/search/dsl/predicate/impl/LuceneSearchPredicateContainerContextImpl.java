/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.dsl.predicate.impl;

import org.apache.lucene.search.Query;
import org.hibernate.search.v6poc.backend.lucene.search.dsl.predicate.LuceneSearchPredicateContainerContext;
import org.hibernate.search.v6poc.backend.lucene.search.predicate.impl.LuceneSearchPredicateCollector;
import org.hibernate.search.v6poc.backend.lucene.search.predicate.impl.LuceneSearchPredicateFactory;
import org.hibernate.search.v6poc.search.dsl.predicate.SearchPredicateContainerContext;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.DelegatingSearchPredicateContainerContextImpl;
import org.hibernate.search.v6poc.search.dsl.predicate.spi.SearchPredicateDslContext;


public class LuceneSearchPredicateContainerContextImpl<N>
		extends DelegatingSearchPredicateContainerContextImpl<N>
		implements LuceneSearchPredicateContainerContext<N> {

	private final LuceneSearchPredicateFactory factory;

	private final SearchPredicateDslContext<N, LuceneSearchPredicateCollector> dslContext;

	public LuceneSearchPredicateContainerContextImpl(SearchPredicateContainerContext<N> delegate,
			LuceneSearchPredicateFactory factory,
			SearchPredicateDslContext<N, LuceneSearchPredicateCollector> dslContext) {
		super( delegate );
		this.factory = factory;
		this.dslContext = dslContext;
	}

	@Override
	public N fromLuceneQuery(Query luceneQuery) {
		dslContext.addContributor( factory.fromLuceneQuery( luceneQuery ) );
		return dslContext.getNextContext();
	}
}
