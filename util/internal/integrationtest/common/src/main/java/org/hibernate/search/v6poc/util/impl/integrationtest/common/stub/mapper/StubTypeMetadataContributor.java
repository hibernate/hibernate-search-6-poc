/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.mapper;

import java.util.function.Consumer;

import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexManagerBuildingState;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.MappingConfigurationCollector;

class StubTypeMetadataContributor {

	private final StubTypeModel typeIdentifier;
	private final String indexName;
	private final Consumer<IndexModelBindingContext> delegate;

	StubTypeMetadataContributor(StubTypeModel typeIdentifier, String indexName, Consumer<IndexModelBindingContext> delegate) {
		this.typeIdentifier = typeIdentifier;
		this.indexName = indexName;
		this.delegate = delegate;
	}

	final void contribute(MappingConfigurationCollector<StubTypeMetadataContributor> collector) {
		if ( indexName != null ) {
			collector.mapToIndex( typeIdentifier, indexName );
		}
		collector.collectContributor( typeIdentifier, this );
	}

	public void contribute(IndexManagerBuildingState<?> indexManagerBuildingState) {
		delegate.accept( indexManagerBuildingState.getRootBindingContext() );
	}

}
