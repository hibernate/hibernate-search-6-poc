/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.impl;

import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.model.StubIndexSchemaNode;

public class StubIndexSchemaRootNodeBuilder extends StubIndexSchemaObjectNodeBuilder
		implements IndexSchemaRootNodeBuilder {

	private final StubIndexSchemaNode.Builder builder;

	public StubIndexSchemaRootNodeBuilder() {
		this( StubIndexSchemaNode.schema() );
	}

	private StubIndexSchemaRootNodeBuilder(StubIndexSchemaNode.Builder builder) {
		super( builder );
		this.builder = builder;
	}

	@Override
	public void explicitRouting() {
		builder.explicitRouting();
	}

	public StubIndexSchemaNode build() {
		return builder.build();
	}
}
