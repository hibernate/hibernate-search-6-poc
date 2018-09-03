/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.impl;

import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.MarkerBuilder;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoMappingCollectorPropertyNode;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoPropertyMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorPropertyNode;


/**
 * @author Yoann Rodiere
 */
public class MarkerMappingContributor implements PojoPropertyMetadataContributor {

	private final MarkerBuilder markerBuilder;

	public MarkerMappingContributor(MarkerBuilder markerBuilder) {
		this.markerBuilder = markerBuilder;
	}

	@Override
	public void contributeModel(PojoAdditionalMetadataCollectorPropertyNode collector) {
		collector.marker( markerBuilder );
	}

	@Override
	public void contributeMapping(PojoMappingCollectorPropertyNode collector) {
		// Nothing to do
	}

}
