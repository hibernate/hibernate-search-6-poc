/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.dsl.impl;

import java.lang.invoke.MethodHandles;

import org.apache.lucene.analysis.Analyzer;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaContext;
import org.hibernate.search.v6poc.backend.document.spi.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.backend.document.model.dsl.Store;
import org.hibernate.search.v6poc.backend.lucene.document.model.dsl.LuceneIndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaNodeContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

/**
 * @author Guillaume Smet
 */
public abstract class AbstractLuceneIndexSchemaFieldTypedContext<F>
		implements LuceneIndexSchemaFieldTypedContext<F>, LuceneIndexSchemaNodeContributor {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final IndexSchemaContext schemaContext;
	private final String relativeFieldName;

	private final DeferredInitializationIndexFieldAccessor<F> accessor = new DeferredInitializationIndexFieldAccessor<>();

	private Store store;

	protected AbstractLuceneIndexSchemaFieldTypedContext(IndexSchemaContext schemaContext, String relativeFieldName) {
		this.schemaContext = schemaContext;
		this.relativeFieldName = relativeFieldName;
	}

	@Override
	public IndexFieldAccessor<F> createAccessor() {
		return accessor;
	}

	@Override
	public void contribute(LuceneIndexSchemaNodeCollector collector, LuceneIndexSchemaObjectNode parentNode) {
		contribute( accessor, collector, parentNode );
	}

	protected abstract void contribute(DeferredInitializationIndexFieldAccessor<F> reference, LuceneIndexSchemaNodeCollector collector,
			LuceneIndexSchemaObjectNode parentNode);

	@Override
	public IndexSchemaFieldTypedContext<F> store(Store store) {
		this.store = store;
		return this;
	}

	@Override
	public IndexSchemaFieldTypedContext<F> analyzer(String analyzerName) {
		throw log.cannotUseAnalyzerOnFieldType( relativeFieldName, schemaContext.getEventContext() );
	}

	@Override
	public IndexSchemaFieldTypedContext<F> normalizer(String normalizerName) {
		throw log.cannotUseNormalizerOnFieldType( relativeFieldName, schemaContext.getEventContext() );
	}

	protected String getRelativeFieldName() {
		return relativeFieldName;
	}

	protected Store getStore() {
		return store;
	}

	protected Analyzer getAnalyzer() {
		return null;
	}

	protected Analyzer getNormalizer() {
		return null;
	}
}
