/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.building.impl;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexManagerBuildingState;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.pojo.bridge.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.PojoTypeManager;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.PojoTypeManagerContainer;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoTypeModel;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.IdentifierMapping;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.PojoNodeProcessor;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.PojoTypeNodeProcessorBuilder;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.PropertyIdentifierMapping;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.RoutingKeyBridgeProvider;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.RoutingKeyProvider;
import org.hibernate.search.v6poc.util.SearchException;

public class PojoTypeManagerBuilder<E, D extends DocumentElement> {
	private final PojoRawTypeModel<E> typeModel;
	private final IndexManagerBuildingState<D> indexManagerBuildingState;

	private final PojoTypeNodeIdentityMappingCollectorImpl identityMappingCollector;
	private final PojoTypeNodeProcessorBuilder<E> processorBuilder;

	PojoTypeManagerBuilder(PojoRawTypeModel<E> typeModel,
			TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> contributorProvider,
			PojoIndexModelBinder indexModelBinder,
			IndexManagerBuildingState<D> indexManagerBuildingState,
			IdentifierMapping<?, E> defaultIdentifierMapping) {
		this.typeModel = typeModel;
		this.indexManagerBuildingState = indexManagerBuildingState;
		this.identityMappingCollector = new PojoTypeNodeIdentityMappingCollectorImpl( defaultIdentifierMapping );
		IndexModelBindingContext bindingContext = indexManagerBuildingState.getRootBindingContext();
		this.processorBuilder = new PojoTypeNodeProcessorBuilder<>(
				null, typeModel, contributorProvider, indexModelBinder, bindingContext, identityMappingCollector
		);
	}

	public PojoTypeNodeMappingCollector asCollector() {
		return processorBuilder;
	}

	public void addTo(PojoTypeManagerContainer.Builder builder) {
		IdentifierMapping<?, E> identifierMapping = identityMappingCollector.identifierMapping;
		if ( identifierMapping == null ) {
			throw new SearchException( "Missing identifier mapping for indexed type '" + typeModel + "'" );
		}
		RoutingKeyBridge routingKeyBridge = identityMappingCollector.routingKeyBridge;
		RoutingKeyProvider<E> routingKeyProvider;
		if ( routingKeyBridge == null ) {
			routingKeyProvider = RoutingKeyProvider.alwaysNull();
		}
		else {
			routingKeyProvider = new RoutingKeyBridgeProvider<>( routingKeyBridge );
		}
		PojoTypeManager<?, E, D> typeManager = new PojoTypeManager<>(
				typeModel.getJavaClass(), typeModel.getCaster(),
				identifierMapping, routingKeyProvider,
				processorBuilder.build().orElseGet( PojoNodeProcessor::noOp ),
				indexManagerBuildingState.build()
		);
		builder.add( indexManagerBuildingState.getIndexName(), typeModel, typeManager );
	}

	private class PojoTypeNodeIdentityMappingCollectorImpl implements PojoTypeNodeIdentityMappingCollector {
		private IdentifierMapping<?, E> identifierMapping;
		private RoutingKeyBridge routingKeyBridge;

		PojoTypeNodeIdentityMappingCollectorImpl(IdentifierMapping<?, E> identifierMapping) {
			this.identifierMapping = identifierMapping;
		}

		@Override
		public <T> void identifierBridge(PojoTypeModel<T> propertyTypeModel, PropertyHandle handle, IdentifierBridge<T> bridge) {
			// FIXME ensure the bridge is closed upon build failure and when closing the SearchManagerRepository
			this.identifierMapping = new PropertyIdentifierMapping<>( propertyTypeModel.getCaster(), handle, bridge );
		}

		@Override
		public void routingKeyBridge(RoutingKeyBridge bridge) {
			// FIXME ensure the bridge is closed upon build failure and when closing the SearchManagerRepository
			this.routingKeyBridge = bridge;
		}
	}
}