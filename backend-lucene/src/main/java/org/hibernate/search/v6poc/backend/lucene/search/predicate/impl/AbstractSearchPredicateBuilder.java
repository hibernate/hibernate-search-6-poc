/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.predicate.impl;

import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateBuilder;


/**
 * @author Guillaume Smet
 */
abstract class AbstractSearchPredicateBuilder implements SearchPredicateBuilder<LuceneSearchPredicateCollector> {

	private Float boost;

	@Override
	public void boost(float boost) {
		this.boost = boost;
	}

	protected abstract Query buildQuery();

	@Override
	public void contribute(LuceneSearchPredicateCollector collector) {
		if ( boost != null ) {
			collector.collectPredicate( new BoostQuery( buildQuery(), boost ) );
		}
		else {
			collector.collectPredicate( buildQuery() );
		}
	}
}
