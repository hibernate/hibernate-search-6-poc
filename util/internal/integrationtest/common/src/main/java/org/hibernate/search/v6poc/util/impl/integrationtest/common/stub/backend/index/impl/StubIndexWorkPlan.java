/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.index.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hibernate.search.v6poc.backend.index.spi.IndexWorkPlan;
import org.hibernate.search.v6poc.backend.index.spi.DocumentContributor;
import org.hibernate.search.v6poc.backend.index.spi.DocumentReferenceProvider;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.StubDocumentNode;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.index.StubIndexWork;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.impl.StubDocumentElement;

class StubIndexWorkPlan implements IndexWorkPlan<StubDocumentElement> {
	private final StubIndexManager indexManager;
	private final SessionContext sessionContext;

	private final List<StubIndexWork> works = new ArrayList<>();

	private int preparedIndex = 0;

	StubIndexWorkPlan(StubIndexManager indexManager, SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		this.indexManager = indexManager;
	}

	@Override
	public void add(DocumentReferenceProvider documentReferenceProvider,
			DocumentContributor<StubDocumentElement> documentContributor) {
		StubIndexWork.Builder builder = StubIndexWork.builder( StubIndexWork.Type.ADD );
		populate( builder, documentReferenceProvider );
		StubDocumentNode.Builder documentBuilder = StubDocumentNode.document();
		StubDocumentElement documentElement = new StubDocumentElement( documentBuilder );
		documentContributor.contribute( documentElement );
		builder.document( documentBuilder.build() );
		addWork( builder.build() );
	}

	@Override
	public void update(DocumentReferenceProvider documentReferenceProvider,
			DocumentContributor<StubDocumentElement> documentContributor) {
		StubIndexWork.Builder builder = StubIndexWork.builder( StubIndexWork.Type.UPDATE );
		populate( builder, documentReferenceProvider );
		StubDocumentNode.Builder documentBuilder = StubDocumentNode.document();
		StubDocumentElement documentElement = new StubDocumentElement( documentBuilder );
		documentContributor.contribute( documentElement );
		builder.document( documentBuilder.build() );
		addWork( builder.build() );
	}

	@Override
	public void delete(DocumentReferenceProvider documentReferenceProvider) {
		StubIndexWork.Builder builder = StubIndexWork.builder( StubIndexWork.Type.DELETE );
		populate( builder, documentReferenceProvider );
		addWork( builder.build() );
	}

	@Override
	public void prepare() {
		indexManager.prepare( works.subList( preparedIndex, works.size() ) );
		preparedIndex = works.size();
	}

	@Override
	public CompletableFuture<?> execute() {
		prepare();
		CompletableFuture<?> future = indexManager.execute( works );
		works.clear();
		preparedIndex = 0;
		return future;
	}

	private void populate(StubIndexWork.Builder builder, DocumentReferenceProvider documentReferenceProvider) {
		builder.tenantIdentifier( sessionContext.getTenantIdentifier() );
		builder.identifier( documentReferenceProvider.getIdentifier() );
		builder.routingKey( documentReferenceProvider.getRoutingKey() );
	}

	private void addWork(StubIndexWork work) {
		works.add( work );
	}
}
