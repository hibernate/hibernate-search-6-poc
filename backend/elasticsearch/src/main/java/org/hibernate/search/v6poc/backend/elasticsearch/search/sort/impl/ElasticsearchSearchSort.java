/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.sort.impl;

import org.hibernate.search.v6poc.search.SearchSort;

import com.google.gson.JsonArray;

class ElasticsearchSearchSort implements SearchSort, ElasticsearchSearchSortBuilder {

	private final JsonArray jsonArray;

	ElasticsearchSearchSort(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	@Override
	public void buildAndAddTo(ElasticsearchSearchSortCollector collector) {
		collector.collectSort( jsonArray );
	}

}
