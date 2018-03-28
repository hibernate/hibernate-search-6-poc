/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.sort.impl;

import org.hibernate.search.v6poc.search.dsl.sort.SortOrder;
import org.hibernate.search.v6poc.search.sort.spi.SearchSortContributor;


/**
 * @author Guillaume Smet
 */
abstract class AbstractSearchSortBuilder implements SearchSortContributor<LuceneSearchSortCollector> {

	protected SortOrder order;

	public void order(SortOrder order) {
		this.order = order;
	}

	@Override
	public abstract void contribute(LuceneSearchSortCollector collector);

}
