/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.sort.impl;

import java.lang.invoke.MethodHandles;

import org.apache.lucene.search.SortField;
import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.backend.lucene.search.sort.impl.LuceneSearchSortCollector;
import org.hibernate.search.v6poc.logging.spi.EventContexts;
import org.hibernate.search.v6poc.search.dsl.sort.SortOrder;
import org.hibernate.search.v6poc.spatial.GeoPoint;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

abstract class AbstractStandardLuceneFieldSortContributor implements LuceneFieldSortContributor {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private Object sortMissingValueFirstPlaceholder;

	private Object sortMissingValueLastPlaceholder;

	protected AbstractStandardLuceneFieldSortContributor(Object sortMissingValueFirstPlaceholder, Object sortMissingValueLastPlaceholder) {
		this.sortMissingValueFirstPlaceholder = sortMissingValueFirstPlaceholder;
		this.sortMissingValueLastPlaceholder = sortMissingValueLastPlaceholder;
	}

	protected void setEffectiveMissingValue(SortField sortField, Object missingValue, SortOrder order) {
		if ( missingValue == null ) {
			return;
		}

		// TODO so this is to mimic the Elasticsearch behavior, I'm not totally convinced it's the good choice though
		Object effectiveMissingValue;
		if ( missingValue == SortMissingValue.MISSING_FIRST ) {
			effectiveMissingValue = order == SortOrder.DESC ? sortMissingValueLastPlaceholder : sortMissingValueFirstPlaceholder;
		}
		else if ( missingValue == SortMissingValue.MISSING_LAST ) {
			effectiveMissingValue = order == SortOrder.DESC ? sortMissingValueFirstPlaceholder : sortMissingValueLastPlaceholder;
		}
		else {
			effectiveMissingValue = missingValue;
		}

		sortField.setMissingValue( effectiveMissingValue );
	}

	@Override
	public void contributeDistanceSort(LuceneSearchSortCollector collector, String absoluteFieldPath, GeoPoint location, SortOrder order) {
		throw log.distanceOperationsNotSupportedByFieldType(
				EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath )
		);
	}
}
