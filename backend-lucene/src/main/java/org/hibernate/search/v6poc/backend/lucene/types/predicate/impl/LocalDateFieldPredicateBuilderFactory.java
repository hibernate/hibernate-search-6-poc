/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.predicate.impl;

import org.hibernate.search.v6poc.backend.lucene.types.converter.impl.LocalDateFieldConverter;

public final class LocalDateFieldPredicateBuilderFactory
		extends AbstractStandardLuceneFieldPredicateBuilderFactory<LocalDateFieldConverter> {

	public LocalDateFieldPredicateBuilderFactory(LocalDateFieldConverter converter) {
		super( converter );
	}

	@Override
	public LocalDateMatchPredicateBuilder createMatchPredicateBuilder(String absoluteFieldPath) {
		return new LocalDateMatchPredicateBuilder( absoluteFieldPath, converter );
	}

	@Override
	public LocalDateRangePredicateBuilder createRangePredicateBuilder(String absoluteFieldPath) {
		return new LocalDateRangePredicateBuilder( absoluteFieldPath, converter );
	}
}
