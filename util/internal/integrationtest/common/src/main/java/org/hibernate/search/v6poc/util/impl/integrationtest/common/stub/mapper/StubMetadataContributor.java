/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.engine.SearchMappingRepositoryBuilder;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.MetadataCollector;
import org.hibernate.search.v6poc.entity.mapping.building.spi.MetadataContributor;

public class StubMetadataContributor implements MetadataContributor {

	private final SearchMappingRepositoryBuilder searchBuilder;
	private final StubMapperFactory mapperFactory;
	private final List<StubTypeMetadataContributor> mappingContributors = new ArrayList<>();

	public StubMetadataContributor(SearchMappingRepositoryBuilder searchBuilder, boolean multiTenancyEnabled) {
		this.searchBuilder = searchBuilder;
		this.mapperFactory = new StubMapperFactory( multiTenancyEnabled );
		searchBuilder.addMetadataContributor( this );
	}

	public void add(String typeIdentifier, String indexName, Consumer<IndexModelBindingContext> mappingContributor) {
		mappingContributors.add( new StubTypeMetadataContributor( new StubTypeModel( typeIdentifier ), indexName, mappingContributor ) );
	}

	@Override
	public void contribute(BuildContext buildContext, ConfigurationPropertySource propertySource,
			MetadataCollector collector) {
		for ( StubTypeMetadataContributor mappingContributor : mappingContributors ) {
			mappingContributor.contribute( mapperFactory, collector );
		}
	}

	public StubMapping getResult() {
		return searchBuilder.getBuiltResult().getMapping( mapperFactory );
	}
}
