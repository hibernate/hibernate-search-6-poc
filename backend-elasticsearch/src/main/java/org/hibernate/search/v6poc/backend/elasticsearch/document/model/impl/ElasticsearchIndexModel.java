/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.TypeMapping;

/**
 * @author Yoann Rodiere
 */
public class ElasticsearchIndexModel {

	private final TypeMapping mapping;

	public ElasticsearchIndexModel(ElasticsearchIndexModelCollectorImpl collector) {
		this.mapping = new TypeMapping();
		// TODO also collect projections
		collector.contribute( mapping );
	}

	@Override
	public String toString() {
		return mapping.toString();
	}

}
