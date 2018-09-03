/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.sort.impl;

import org.hibernate.search.v6poc.search.dsl.sort.SortOrder;
import org.hibernate.search.v6poc.search.sort.spi.SearchSortBuilder;


/**
 * @author Guillaume Smet
 */
abstract class AbstractSearchSortBuilder implements SearchSortBuilder<LuceneSearchSortBuilder>, LuceneSearchSortBuilder {

	protected SortOrder order;

	@Override
	public LuceneSearchSortBuilder toImplementation() {
		return this;
	}

	public void order(SortOrder order) {
		this.order = order;
	}

}
