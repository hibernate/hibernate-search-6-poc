/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.mapping.building.impl;

import java.util.Optional;
import java.util.Set;

import org.hibernate.search.v6poc.backend.document.model.spi.IndexSchemaElement;
import org.hibernate.search.v6poc.backend.document.model.spi.IndexSchemaCollector;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.model.spi.SearchModel;
import org.hibernate.search.v6poc.entity.model.spi.IndexableTypeOrdering;
import org.hibernate.search.v6poc.entity.model.spi.IndexedTypeIdentifier;

public class IndexModelBindingContextImpl implements IndexModelBindingContext {

	private final IndexSchemaCollector schemaCollector;
	private final IndexSchemaNestingContextImpl nestingContext;
	private final SearchModel searchModel = new SearchModel() {
		// TODO provide an actual implementation when the interface defines methods
	};

	private IndexSchemaElement schemaElementWithNestingContext;

	public IndexModelBindingContextImpl(IndexSchemaCollector schemaCollector,
			IndexableTypeOrdering typeOrdering) {
		this( schemaCollector, new IndexSchemaNestingContextImpl( typeOrdering ) );
	}

	private IndexModelBindingContextImpl(IndexSchemaCollector schemaCollector,
			IndexSchemaNestingContextImpl nestingContext) {
		this.schemaCollector = schemaCollector;
		this.nestingContext = nestingContext;
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "schemaCollector=" ).append( schemaCollector )
				.append( ",nestingContext=" ).append( nestingContext )
				.append( "]" )
				.toString();
	}

	@Override
	public IndexSchemaElement getSchemaElement() {
		if ( schemaElementWithNestingContext == null ) {
			schemaElementWithNestingContext = schemaCollector.withContext( nestingContext );
		}
		return schemaElementWithNestingContext;
	}

	@Override
	public SearchModel getSearchModel() {
		return searchModel;
	}

	@Override
	public void explicitRouting() {
		schemaCollector.explicitRouting();
	}

	@Override
	public Optional<IndexModelBindingContext> addIndexedEmbeddedIfIncluded(IndexedTypeIdentifier parentTypeId,
			String relativePrefix, Integer nestedMaxDepth, Set<String> nestedPathFilters) {
		return nestingContext.addIndexedEmbeddedIfIncluded(
				relativePrefix,
				f -> f.composeWithNested( parentTypeId, relativePrefix, nestedMaxDepth, nestedPathFilters ),
				schemaCollector, IndexSchemaCollector::childObject,
				IndexModelBindingContextImpl::new
		);
	}

}
