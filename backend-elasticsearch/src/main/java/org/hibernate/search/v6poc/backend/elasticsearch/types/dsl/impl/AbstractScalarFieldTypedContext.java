/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.types.dsl.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.v6poc.backend.document.spi.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.Sortable;
import org.hibernate.search.v6poc.backend.document.model.dsl.Store;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaNodeCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaObjectNode;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.DataType;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.PropertyMapping;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

/**
 * @author Yoann Rodiere
 */
abstract class AbstractScalarFieldTypedContext<T> extends AbstractElasticsearchIndexSchemaFieldTypedContext<T> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final String relativeFieldName;
	private final DataType dataType;
	private String analyzerName;
	private String normalizerName;
	private Store store = Store.DEFAULT;
	private Sortable sortable = Sortable.DEFAULT;

	public AbstractScalarFieldTypedContext(String relativeFieldName, DataType dataType) {
		this.relativeFieldName = relativeFieldName;
		this.dataType = dataType;
	}

	@Override
	public IndexSchemaFieldTypedContext<T> analyzer(String analyzerName) {
		this.analyzerName = analyzerName;
		return this;
	}

	@Override
	public IndexSchemaFieldTypedContext<T> normalizer(String normalizerName) {
		this.normalizerName = normalizerName;
		return this;
	}

	@Override
	public IndexSchemaFieldTypedContext<T> store(Store store) {
		this.store = store;
		return this;
	}

	@Override
	public IndexSchemaFieldTypedContext<T> sortable(Sortable sortable) {
		this.sortable = sortable;
		return this;
	}

	@Override
	protected PropertyMapping contribute(
			DeferredInitializationIndexFieldAccessor<T> reference,
			ElasticsearchIndexSchemaNodeCollector collector,
			ElasticsearchIndexSchemaObjectNode parentNode) {
		PropertyMapping mapping = new PropertyMapping();

		mapping.setType( dataType );

		if ( analyzerName != null ) {
			throw log.cannotUseAnalyzerOnFieldType( parentNode.getAbsolutePath( relativeFieldName ), dataType );
		}
		if ( normalizerName != null ) {
			throw log.cannotUseNormalizerOnFieldType( parentNode.getAbsolutePath( relativeFieldName ), dataType );
		}

		switch ( store ) {
			case DEFAULT:
				break;
			case NO:
				mapping.setStore( false );
				break;
			case YES:
			case COMPRESS:
				// TODO what about Store.COMPRESS?
				mapping.setStore( true );
				break;
		}

		switch ( sortable ) {
			case DEFAULT:
				break;
			case NO:
				mapping.setDocValues( false );
				break;
			case YES:
				mapping.setDocValues( true );
				break;
		}

		return mapping;
	}

}
