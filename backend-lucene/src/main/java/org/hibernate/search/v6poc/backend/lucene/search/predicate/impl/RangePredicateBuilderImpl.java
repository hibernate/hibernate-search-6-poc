/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.predicate.impl;

import org.apache.lucene.search.Query;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneFieldQueryFactory;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.RangeQueryOptions;
import org.hibernate.search.v6poc.search.predicate.spi.RangePredicateBuilder;

/**
 * @author Guillaume Smet
 */
class RangePredicateBuilderImpl extends AbstractSearchPredicateBuilder
		implements RangePredicateBuilder<LuceneSearchPredicateCollector> {

	private final String absoluteFieldPath;

	private final LuceneFieldQueryFactory queryBuilder;

	private Object lowerLimit;
	private Object upperLimit;

	private final RangeQueryOptions queryOptions = new RangeQueryOptions();

	public RangePredicateBuilderImpl(String absoluteFieldPath, LuceneFieldQueryFactory queryBuilder) {
		this.absoluteFieldPath = absoluteFieldPath;
		this.queryBuilder = queryBuilder;
	}

	@Override
	public void lowerLimit(Object value) {
		lowerLimit = value;
	}

	@Override
	public void excludeLowerLimit() {
		queryOptions.excludeLowerLimit();
	}

	@Override
	public void upperLimit(Object value) {
		upperLimit = value;
	}

	@Override
	public void excludeUpperLimit() {
		queryOptions.excludeUpperLimit();
	}

	@Override
	protected Query buildQuery() {
		return queryBuilder.createRangeQuery( absoluteFieldPath, lowerLimit, upperLimit, queryOptions );
	}
}
