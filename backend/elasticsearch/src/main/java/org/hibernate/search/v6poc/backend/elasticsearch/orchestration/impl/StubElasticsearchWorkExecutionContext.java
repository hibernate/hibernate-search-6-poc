/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.orchestration.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.client.impl.ElasticsearchClient;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWorkExecutionContext;

/**
 * @author Yoann Rodiere
 */
public class StubElasticsearchWorkExecutionContext implements ElasticsearchWorkExecutionContext {

	private final ElasticsearchClient client;

	public StubElasticsearchWorkExecutionContext(ElasticsearchClient client) {
		this.client = client;
	}

	@Override
	public ElasticsearchClient getClient() {
		return client;
	}
}
