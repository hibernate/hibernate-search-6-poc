/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.building.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoTypeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.impl.PojoTypeAdditionalMetadata;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;
import org.hibernate.search.v6poc.logging.spi.FailureCollector;

public class PojoTypeAdditionalMetadataProvider {

	private final FailureCollector failureCollector;
	private final TypeMetadataContributorProvider<PojoTypeMetadataContributor> modelContributorProvider;
	private final Map<PojoRawTypeModel<?>, PojoTypeAdditionalMetadata> cache = new HashMap<>();

	public PojoTypeAdditionalMetadataProvider(FailureCollector failureCollector,
			TypeMetadataContributorProvider<PojoTypeMetadataContributor> modelContributorProvider) {
		this.failureCollector = failureCollector;
		this.modelContributorProvider = modelContributorProvider;
	}

	public PojoTypeAdditionalMetadata get(PojoRawTypeModel<?> typeModel) {
		return cache.computeIfAbsent( typeModel, this::createTypeAdditionalMetadata );
	}

	private PojoTypeAdditionalMetadata createTypeAdditionalMetadata(PojoRawTypeModel<?> typeModel) {
		PojoTypeAdditionalMetadataBuilder builder = new PojoTypeAdditionalMetadataBuilder(
				failureCollector, typeModel
		);
		modelContributorProvider.forEach( typeModel, c -> c.contributeModel( builder ) );
		return builder.build();
	}

}
