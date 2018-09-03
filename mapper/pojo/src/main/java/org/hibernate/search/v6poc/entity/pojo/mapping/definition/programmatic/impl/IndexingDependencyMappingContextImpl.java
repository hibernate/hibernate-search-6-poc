/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.search.v6poc.entity.pojo.dirtiness.ReindexOnUpdate;
import org.hibernate.search.v6poc.entity.pojo.extractor.ContainerValueExtractorPath;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoMappingCollectorPropertyNode;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoPropertyMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.IndexingDependencyMappingContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyMappingContext;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorPropertyNode;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorValueNode;
import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPathValueNode;


/**
 * @author Yoann Rodiere
 */
public class IndexingDependencyMappingContextImpl
		extends DelegatingPropertyMappingContext
		implements IndexingDependencyMappingContext, PojoPropertyMetadataContributor {

	private ContainerValueExtractorPath extractorPath = ContainerValueExtractorPath.defaultExtractors();
	private ReindexOnUpdate reindexOnUpdate = ReindexOnUpdate.DEFAULT;
	// Use a LinkedHashSet for deterministic iteration
	private Set<PojoModelPathValueNode> derivedFrom = null;

	IndexingDependencyMappingContextImpl(PropertyMappingContext delegate) {
		super( delegate );
	}

	@Override
	public void contributeModel(PojoAdditionalMetadataCollectorPropertyNode collector) {
		PojoAdditionalMetadataCollectorValueNode collectorValueNode = collector.value( extractorPath );
		if ( reindexOnUpdate != null ) {
			collectorValueNode.reindexOnUpdate( reindexOnUpdate );
		}
		if ( derivedFrom != null ) {
			collectorValueNode.derivedFrom( derivedFrom );
		}
	}

	@Override
	public void contributeMapping(PojoMappingCollectorPropertyNode collector) {
		// Nothing to do
	}

	@Override
	public IndexingDependencyMappingContext reindexOnUpdate(ReindexOnUpdate reindexOnUpdate) {
		this.reindexOnUpdate = reindexOnUpdate;
		return this;
	}

	@Override
	public IndexingDependencyMappingContext derivedFrom(PojoModelPathValueNode pojoModelPath) {
		if ( derivedFrom == null ) {
			// Use a LinkedHashSet for deterministic iteration
			derivedFrom = new LinkedHashSet<>();
		}
		derivedFrom.add( pojoModelPath );
		return this;
	}

	@Override
	public IndexingDependencyMappingContext withExtractors(ContainerValueExtractorPath extractorPath) {
		this.extractorPath = extractorPath;
		return this;
	}
}
