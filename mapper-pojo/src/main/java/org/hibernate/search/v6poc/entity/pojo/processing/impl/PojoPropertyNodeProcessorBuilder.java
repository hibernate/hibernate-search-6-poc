/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.hibernate.search.v6poc.bridge.spi.FunctionBridge;
import org.hibernate.search.v6poc.bridge.spi.IdentifierBridge;
import org.hibernate.search.v6poc.engine.spi.BeanReference;
import org.hibernate.search.v6poc.entity.mapping.building.spi.FieldModelContributor;
import org.hibernate.search.v6poc.entity.mapping.building.spi.MappingIndexModelCollector;
import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeIdentityMappingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoPropertyNodeMappingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.model.impl.PojoIndexedTypeIdentifier;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyModel;
import org.hibernate.search.v6poc.entity.processing.spi.ValueProcessor;

/**
 * @author Yoann Rodiere
 */
public class PojoPropertyNodeProcessorBuilder extends AbstractPojoProcessorBuilder
		implements PojoPropertyNodeMappingCollector {

	private final PropertyModel<?> propertyModel;

	private final Collection<PojoTypeNodeProcessorBuilder> indexedEmbeddedProcessorBuilders = new ArrayList<>();

	public PojoPropertyNodeProcessorBuilder(
			PropertyModel<?> propertyModel,
			TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> contributorProvider,
			MappingIndexModelCollector indexModelCollector,
			PojoTypeNodeIdentityMappingCollector identityMappingCollector) {
		super( propertyModel.getTypeModel(), contributorProvider, indexModelCollector,
				identityMappingCollector
		);
		this.propertyModel = propertyModel;
	}

	@Override
	public void functionBridge(BeanReference<? extends FunctionBridge<?, ?>> reference,
			String fieldName, FieldModelContributor fieldModelContributor) {
		String defaultedFieldName = fieldName;
		if ( defaultedFieldName == null ) {
			defaultedFieldName = propertyModel.getName();
		}

		ValueProcessor processor = indexModelCollector.addFunctionBridge(
				indexableModel, propertyModel.getJavaType(), reference, defaultedFieldName, fieldModelContributor );
		processors.add( processor );
	}

	@Override
	public void identifierBridge(BeanReference<IdentifierBridge<?>> converterReference) {
		IdentifierBridge<?> bridge = indexModelCollector.createIdentifierBridge( propertyModel.getJavaType(), converterReference );
		identityMappingCollector.identifierBridge( propertyModel.getHandle(), bridge );
	}

	@Override
	public void containedIn() {
		// FIXME implement ContainedIn
		// FIXME also bind containedIns to indexedEmbeddeds using the parent's metadata here, if possible?
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public void indexedEmbedded(String relativePrefix, Integer maxDepth, Set<String> pathFilters) {
		// TODO handle collections

		String defaultedRelativePrefix = relativePrefix;
		if ( defaultedRelativePrefix == null ) {
			defaultedRelativePrefix = propertyModel.getName() + ".";
		}

		PojoIndexedTypeIdentifier typeId = new PojoIndexedTypeIdentifier( propertyModel.getJavaType() );

		Optional<MappingIndexModelCollector> nestedCollectorOptional = indexModelCollector.addIndexedEmbeddedIfIncluded(
				typeId, defaultedRelativePrefix, maxDepth, pathFilters );
		nestedCollectorOptional.ifPresent( nestedCollector -> {
			PojoTypeNodeProcessorBuilder nestedProcessorBuilder = new PojoTypeNodeProcessorBuilder(
					propertyModel.getTypeModel(), contributorProvider, nestedCollector,
					PojoTypeNodeIdentityMappingCollector.noOp() // Do NOT propagate the identity mapping collector to IndexedEmbeddeds
					);
			indexedEmbeddedProcessorBuilders.add( nestedProcessorBuilder );
			contributorProvider.get( typeId ).forEach( c -> c.contributeMapping( nestedProcessorBuilder ) );
		} );
	}

	public PojoPropertyNodeProcessor build() {
		return new PojoPropertyNodeProcessor( propertyModel.getHandle(), processors, indexedEmbeddedProcessorBuilders );
	}

}
