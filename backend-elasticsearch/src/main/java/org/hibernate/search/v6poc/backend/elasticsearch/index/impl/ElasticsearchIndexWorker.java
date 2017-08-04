/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.index.impl;

import org.hibernate.search.v6poc.backend.document.spi.DocumentContributor;
import org.hibernate.search.v6poc.backend.elasticsearch.document.impl.ElasticsearchDocumentBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWork;
import org.hibernate.search.v6poc.backend.elasticsearch.work.impl.ElasticsearchWorkFactory;
import org.hibernate.search.v6poc.backend.index.spi.IndexWorker;
import org.hibernate.search.v6poc.engine.spi.SessionContext;


/**
 * @author Yoann Rodiere
 */
public abstract class ElasticsearchIndexWorker implements IndexWorker<ElasticsearchDocumentBuilder> {

	protected final ElasticsearchWorkFactory factory;
	protected final String indexName;
	protected final String tenantId;

	public ElasticsearchIndexWorker(ElasticsearchWorkFactory factory, String indexName, SessionContext context) {
		this.factory = factory;
		this.indexName = indexName;
		this.tenantId = context.getTenantIdentifier();
	}

	@Override
	public void add(String id, DocumentContributor<ElasticsearchDocumentBuilder> documentContributor) {
		ElasticsearchDocumentBuilder builder = new ElasticsearchDocumentBuilder();
		documentContributor.contribute( builder );
		collect( factory.add( indexName, toActualId( id ), builder.build() ) );
	}

	@Override
	public void update(String id, DocumentContributor<ElasticsearchDocumentBuilder> documentContributor) {
		ElasticsearchDocumentBuilder builder = new ElasticsearchDocumentBuilder();
		documentContributor.contribute( builder );
		collect( factory.update( indexName, toActualId( id ), builder.build() ) );
	}

	@Override
	public void delete(String id) {
		collect( factory.delete( indexName, toActualId( id ) ) );
	}

	protected final String toActualId(String id) {
		return tenantId == null ? id : tenantId + "_" + id;
	}

	protected abstract void collect(ElasticsearchWork<?> work);

}
