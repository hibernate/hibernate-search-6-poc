/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.types.dsl.impl;

import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaContext;
import org.hibernate.search.v6poc.backend.document.spi.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.dsl.ElasticsearchIndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNodeContributor;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.PropertyMapping;

/**
 * @author Yoann Rodiere
 */
public abstract class AbstractElasticsearchIndexSchemaFieldTypedContext<F>
		implements ElasticsearchIndexSchemaFieldTypedContext<F>,
		ElasticsearchIndexSchemaNodeContributor<PropertyMapping> {

	protected final IndexSchemaContext schemaContext;

	private final DeferredInitializationIndexFieldAccessor<F> accessor = new DeferredInitializationIndexFieldAccessor<>();

	AbstractElasticsearchIndexSchemaFieldTypedContext(IndexSchemaContext schemaContext) {
		this.schemaContext = schemaContext;
	}

	@Override
	public IndexFieldAccessor<F> createAccessor() {
		return accessor;
	}

	@Override
	public PropertyMapping contribute(ElasticsearchIndexSchemaNodeCollector collector,
			ElasticsearchIndexSchemaObjectNode parentNode) {
		return contribute( accessor, collector, parentNode );
	}

	protected abstract PropertyMapping contribute(DeferredInitializationIndexFieldAccessor<F> reference,
			ElasticsearchIndexSchemaNodeCollector collector,
			ElasticsearchIndexSchemaObjectNode parentNode);

}
