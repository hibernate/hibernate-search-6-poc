/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.sort.impl;

import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneFieldFormatter;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneFieldSortContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.SortMissingValue;
import org.hibernate.search.v6poc.search.sort.spi.FieldSortBuilder;

class FieldSortBuilderImpl extends AbstractSearchSortBuilder
		implements FieldSortBuilder<LuceneSearchSortCollector> {

	private final String absoluteFieldPath;

	private final LuceneFieldFormatter<?> fieldFormatter;

	private final LuceneFieldSortContributor fieldSortContributor;

	private Object missingValue;

	FieldSortBuilderImpl(String absoluteFieldPath, LuceneFieldFormatter<?> fieldFormatter, LuceneFieldSortContributor fieldSortContributor) {
		this.absoluteFieldPath = absoluteFieldPath;
		this.fieldFormatter = fieldFormatter;
		this.fieldSortContributor = fieldSortContributor;
	}

	@Override
	public void missingFirst() {
		missingValue = SortMissingValue.MISSING_FIRST;
	}

	@Override
	public void missingLast() {
		missingValue = SortMissingValue.MISSING_LAST;
	}

	@Override
	public void missingAs(Object value) {
		missingValue = fieldFormatter.format( value );
	}

	@Override
	public void contribute(LuceneSearchSortCollector collector) {
		fieldSortContributor.contribute( collector, absoluteFieldPath, order, missingValue );
	}
}
