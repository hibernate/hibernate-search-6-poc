/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.index.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.client.impl.URLEncodedString;
import org.hibernate.search.v6poc.backend.elasticsearch.document.impl.ElasticsearchDocumentObjectBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.orchestration.impl.ElasticsearchWorkOrchestrator;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWork;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWorkFactory;
import org.hibernate.search.v6poc.backend.index.spi.StreamIndexWorker;
import org.hibernate.search.v6poc.engine.spi.SessionContext;


/**
 * @author Yoann Rodiere
 */
public class ElasticsearchStreamIndexWorker extends ElasticsearchIndexWorker
		implements StreamIndexWorker<ElasticsearchDocumentObjectBuilder> {

	private final ElasticsearchWorkOrchestrator orchestrator;

	public ElasticsearchStreamIndexWorker(ElasticsearchWorkFactory factory,
			ElasticsearchWorkOrchestrator orchestrator,
			URLEncodedString indexName, URLEncodedString typeName,
			SessionContext context) {
		super( factory, indexName, typeName, context );
		this.orchestrator = orchestrator;
	}

	@Override
	protected void collect(ElasticsearchWork<?> work) {
		orchestrator.submit( work );
	}

	@Override
	public void flush() {
		collect( factory.flush( indexName ) );
	}

	@Override
	public void optimize() {
		collect( factory.optimize( indexName ) );
	}

}
