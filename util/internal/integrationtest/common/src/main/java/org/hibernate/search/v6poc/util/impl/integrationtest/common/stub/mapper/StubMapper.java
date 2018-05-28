/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.search.v6poc.backend.index.spi.IndexManager;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexManagerBuildingState;
import org.hibernate.search.v6poc.entity.mapping.building.spi.Mapper;
import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.model.spi.MappableTypeModel;

class StubMapper implements Mapper<StubMapping> {

	private final TypeMetadataContributorProvider<StubTypeMetadataContributor> contributorProvider;

	private final Map<StubTypeModel, IndexManagerBuildingState<?>> indexManagerBuildingStates = new HashMap<>();

	StubMapper(TypeMetadataContributorProvider<StubTypeMetadataContributor> contributorProvider) {
		this.contributorProvider = contributorProvider;
	}

	@Override
	public void closeOnFailure() {
		// Nothing to do
	}

	@Override
	public void addIndexed(MappableTypeModel typeModel, IndexManagerBuildingState<?> indexManagerBuildingState) {
		indexManagerBuildingStates.put( (StubTypeModel) typeModel, indexManagerBuildingState );
		contributorProvider.forEach( typeModel, c -> c.contribute( indexManagerBuildingState ) );
	}

	@Override
	public StubMapping build() {
		Map<String, String> normalizedIndexNamesByTypeIdentifier = indexManagerBuildingStates.entrySet().stream()
				.collect( Collectors.toMap( e -> e.getKey().asString(), e -> e.getValue().getIndexName() ) );
		Map<String, IndexManager<?>> indexManagersByTypeIdentifier = indexManagerBuildingStates.entrySet().stream()
				.collect( Collectors.toMap( e -> e.getKey().asString(), e -> e.getValue().build() ) );
		return new StubMapping( normalizedIndexNamesByTypeIdentifier, indexManagersByTypeIdentifier );
	}
}
