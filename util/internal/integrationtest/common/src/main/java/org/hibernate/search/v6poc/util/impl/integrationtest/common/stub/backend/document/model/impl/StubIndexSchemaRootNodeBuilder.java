/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.impl;

import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.util.FailureContext;
import org.hibernate.search.v6poc.logging.spi.FailureContexts;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.StubBackendBehavior;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.StubIndexSchemaNode;

public class StubIndexSchemaRootNodeBuilder extends StubIndexSchemaObjectNodeBuilder
		implements IndexSchemaRootNodeBuilder {

	private final StubBackendBehavior backendBehavior;
	private final String indexName;

	public StubIndexSchemaRootNodeBuilder(StubBackendBehavior backendBehavior, String indexName) {
		this( backendBehavior, indexName, StubIndexSchemaNode.schema() );
	}

	private StubIndexSchemaRootNodeBuilder(StubBackendBehavior backendBehavior, String indexName,
			StubIndexSchemaNode.Builder builder) {
		super( builder );
		this.backendBehavior = backendBehavior;
		this.indexName = indexName;
	}

	@Override
	public void explicitRouting() {
		builder.explicitRouting();
	}

	@Override
	public FailureContext getFailureContext() {
		return getIndexFailureContext().append( FailureContexts.indexSchemaRoot() );
	}

	public StubIndexSchemaNode build() {
		return builder.build();
	}

	@Override
	StubIndexSchemaRootNodeBuilder getRootNodeBuilder() {
		return this;
	}

	FailureContext getIndexFailureContext() {
		return FailureContexts.fromIndexName( indexName );
	}

	StubBackendBehavior getBackendBehavior() {
		return backendBehavior;
	}

	String getIndexName() {
		return indexName;
	}
}
