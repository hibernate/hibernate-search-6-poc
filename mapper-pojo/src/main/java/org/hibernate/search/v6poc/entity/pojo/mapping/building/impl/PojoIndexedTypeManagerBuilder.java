/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.building.impl;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.Set;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexManagerBuildingState;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.dirtiness.building.impl.PojoImplicitReindexingResolverBuildingHelper;
import org.hibernate.search.v6poc.entity.pojo.dirtiness.building.impl.PojoIndexingDependencyCollectorTypeNode;
import org.hibernate.search.v6poc.entity.pojo.dirtiness.impl.PojoImplicitReindexingResolver;
import org.hibernate.search.v6poc.entity.pojo.logging.impl.Log;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoMappingCollectorTypeNode;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.IdentifierMapping;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.PojoIndexedTypeManager;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.PojoIndexedTypeManagerContainer;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.PropertyIdentifierMapping;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.RoutingKeyBridgeRoutingKeyProvider;
import org.hibernate.search.v6poc.entity.pojo.mapping.impl.RoutingKeyProvider;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.impl.PojoTypeAdditionalMetadata;
import org.hibernate.search.v6poc.entity.pojo.model.path.impl.BoundPojoModelPath;
import org.hibernate.search.v6poc.entity.pojo.model.path.impl.BoundPojoModelPathPropertyNode;
import org.hibernate.search.v6poc.entity.pojo.model.path.spi.PojoPathFilterFactory;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoPropertyModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;
import org.hibernate.search.v6poc.entity.pojo.processing.building.impl.PojoIndexingProcessorTypeNodeBuilder;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.PojoIndexingProcessor;
import org.hibernate.search.v6poc.util.AssertionFailure;
import org.hibernate.search.v6poc.util.impl.common.Closer;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

class PojoIndexedTypeManagerBuilder<E, D extends DocumentElement> {
	private static Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final PojoRawTypeModel<E> typeModel;
	private final PojoTypeAdditionalMetadata typeAdditionalMetadata;
	private final PojoMappingHelper mappingHelper;
	private final IndexManagerBuildingState<D> indexManagerBuildingState;

	private final PojoIdentityMappingCollectorImpl identityMappingCollector;
	private final PojoIndexingProcessorTypeNodeBuilder<E> processorBuilder;

	private PojoIndexingProcessor<E> preBuiltIndexingProcessor;

	private boolean closed = false;

	PojoIndexedTypeManagerBuilder(PojoRawTypeModel<E> typeModel,
			PojoTypeAdditionalMetadata typeAdditionalMetadata,
			PojoMappingHelper mappingHelper,
			IndexManagerBuildingState<D> indexManagerBuildingState,
			IdentifierMapping<?, E> defaultIdentifierMapping) {
		this.typeModel = typeModel;
		this.typeAdditionalMetadata = typeAdditionalMetadata;
		this.mappingHelper = mappingHelper;
		this.indexManagerBuildingState = indexManagerBuildingState;
		this.identityMappingCollector = new PojoIdentityMappingCollectorImpl( defaultIdentifierMapping );
		IndexModelBindingContext bindingContext = indexManagerBuildingState.getRootBindingContext();
		this.processorBuilder = new PojoIndexingProcessorTypeNodeBuilder<>(
				BoundPojoModelPath.root( typeModel ),
				mappingHelper, bindingContext,
				Optional.of( identityMappingCollector )
		);
	}

	void closeOnFailure() {
		if ( closed ) {
			return;
		}

		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.push( PojoIndexingProcessorTypeNodeBuilder::closeOnFailure, processorBuilder );
			closer.push( PojoIdentityMappingCollectorImpl::closeOnFailure, identityMappingCollector );
			closer.push( PojoIndexingProcessor::close, preBuiltIndexingProcessor );
			closed = true;
		}
	}

	PojoMappingCollectorTypeNode asCollector() {
		return processorBuilder;
	}

	void preBuild(PojoImplicitReindexingResolverBuildingHelper reindexingResolverBuildingHelper) {
		if ( preBuiltIndexingProcessor != null ) {
			throw new AssertionFailure( "Internal error - preBuild should be called only once" );
		}

		PojoIndexingDependencyCollectorTypeNode<E> dependencyCollector =
				reindexingResolverBuildingHelper.createDependencyCollector( typeModel );
		preBuiltIndexingProcessor = processorBuilder.build( dependencyCollector )
				.orElseGet( PojoIndexingProcessor::noOp );
	}

	void buildAndAddTo(PojoIndexedTypeManagerContainer.Builder typeManagersBuilder,
			PojoImplicitReindexingResolverBuildingHelper reindexingResolverBuildingHelper,
			PojoTypeAdditionalMetadata typeAdditionalMetadata) {
		if ( preBuiltIndexingProcessor == null ) {
			throw new AssertionFailure( "Internal error - preBuild should be called before buildAndAddTo" );
		}

		IdentifierMapping<?, E> identifierMapping = identityMappingCollector.identifierMapping;
		if ( identifierMapping == null ) {
			// Fall back to the entity ID, if possible
			Optional<BoundPojoModelPathPropertyNode<E, ?>> entityIdPathOptional = getEntityIdentifierPath();
			if ( entityIdPathOptional.isPresent() ) {
				BoundPojoModelPathPropertyNode<E, ?> entityIdPath = entityIdPathOptional.get();
				identityMappingCollector.defaultIdentifierBridge(
						mappingHelper,
						entityIdPath
				);
				identifierMapping = identityMappingCollector.identifierMapping;
			}
			else {
				throw log.missingIdentifierMapping( typeModel );
			}
		}

		RoutingKeyBridge routingKeyBridge = identityMappingCollector.routingKeyBridge;
		RoutingKeyProvider<E> routingKeyProvider;
		if ( routingKeyBridge == null ) {
			routingKeyProvider = RoutingKeyProvider.alwaysNull();
		}
		else {
			routingKeyProvider = new RoutingKeyBridgeRoutingKeyProvider<>( routingKeyBridge );
		}

		/*
		 * TODO offer more flexibility to mapper implementations, allowing them to define their own dirtiness state?
		 * Note this will require to allow them to define their own work plan APIs.
		 */
		PojoPathFilterFactory<Set<String>> pathFilterFactory = typeAdditionalMetadata
				.getEntityTypeMetadata().orElseThrow( () -> log.missingEntityTypeMetadata( typeModel ) )
				.getPathFilterFactory();
		Optional<PojoImplicitReindexingResolver<E, Set<String>>> reindexingResolverOptional =
				reindexingResolverBuildingHelper.build( typeModel, pathFilterFactory );

		PojoIndexedTypeManager<?, E, D> typeManager = new PojoIndexedTypeManager<>(
				typeModel.getJavaClass(), typeModel.getCaster(),
				identifierMapping, routingKeyProvider,
				preBuiltIndexingProcessor,
				indexManagerBuildingState.build(),
				reindexingResolverOptional.orElseGet( PojoImplicitReindexingResolver::noOp )
		);
		log.createdPojoIndexedTypeManager( typeManager );

		typeManagersBuilder.add( indexManagerBuildingState.getIndexName(), typeModel, typeManager );

		closed = true;
	}

	private Optional<BoundPojoModelPathPropertyNode<E, ?>> getEntityIdentifierPath() {
		Optional<String> entityIdPropertyName = typeAdditionalMetadata.getEntityTypeMetadata()
				.orElseThrow( () -> log.missingEntityTypeMetadata( typeModel ) )
				.getEntityIdPropertyName();
		if ( entityIdPropertyName.isPresent() ) {
			PojoPropertyModel<?> propertyModel = typeModel.getProperty( entityIdPropertyName.get() );
			return Optional.of( BoundPojoModelPath.root( typeModel ).property( propertyModel.getHandle() ) );
		}
		else {
			return Optional.empty();
		}
	}

	private class PojoIdentityMappingCollectorImpl implements PojoIdentityMappingCollector {
		private IdentifierMapping<?, E> identifierMapping;
		private RoutingKeyBridge routingKeyBridge;

		PojoIdentityMappingCollectorImpl(IdentifierMapping<?, E> identifierMapping) {
			this.identifierMapping = identifierMapping;
		}

		void closeOnFailure() {
			try ( Closer<RuntimeException> closer = new Closer<>() ) {
				closer.push( IdentifierMapping::close, identifierMapping );
				closer.push( RoutingKeyBridge::close, routingKeyBridge );
			}
		}

		@Override
		public <T> void identifierBridge(BoundPojoModelPathPropertyNode<?, T> modelPath, IdentifierBridge<T> bridge) {
			PojoPropertyModel<T> propertyModel = modelPath.getPropertyModel();
			this.identifierMapping = new PropertyIdentifierMapping<>(
					propertyModel.getTypeModel().getRawType().getCaster(),
					propertyModel.getHandle(),
					bridge
			);
		}

		<T> void defaultIdentifierBridge(PojoMappingHelper mappingHelper,
				BoundPojoModelPathPropertyNode<?, T> entityIdPath) {
			identifierBridge(
					entityIdPath,
					mappingHelper.getIndexModelBinder().addIdentifierBridge( entityIdPath, null )
			);
		}

		@Override
		public void routingKeyBridge(RoutingKeyBridge bridge) {
			this.routingKeyBridge = bridge;
		}
	}
}