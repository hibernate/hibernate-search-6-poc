/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.impl;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.backend.document.model.dsl.Sortable;
import org.hibernate.search.v6poc.backend.document.model.dsl.Store;
import org.hibernate.search.v6poc.engine.spi.BeanReference;
import org.hibernate.search.v6poc.engine.spi.BeanProvider;
import org.hibernate.search.v6poc.engine.spi.ImmutableBeanReference;
import org.hibernate.search.v6poc.entity.mapping.building.spi.FieldModelContributor;
import org.hibernate.search.v6poc.entity.pojo.bridge.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.PropertyBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.TypeBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.ValueBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.MarkerMapping;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.MarkerMappingBuilderReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.PropertyBridgeMapping;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.PropertyBridgeAnnotationBuilderReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.PropertyBridgeReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.RoutingKeyBridgeAnnotationBuilderReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.RoutingKeyBridgeMapping;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.RoutingKeyBridgeReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.TypeBridgeMapping;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.TypeBridgeAnnotationBuilderReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.TypeBridgeReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.impl.BeanResolverBridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.AnnotationBridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.AnnotationMarkerBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.dirtiness.ReindexOnUpdate;
import org.hibernate.search.v6poc.entity.pojo.extractor.ContainerValueExtractorPath;
import org.hibernate.search.v6poc.entity.pojo.logging.impl.Log;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoMappingCollectorPropertyNode;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoMappingCollectorTypeNode;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoTypeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.AssociationInverseSide;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.ContainerValueExtractorBeanReference;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.Field;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.IdentifierBridgeBeanReference;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.IdentifierBridgeBuilderBeanReference;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.PropertyValue;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.ValueBridgeBeanReference;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.ValueBridgeBuilderBeanReference;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorPropertyNode;
import org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorTypeNode;
import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPath;
import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPathValueNode;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoPropertyModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

class AnnotationPojoTypeMetadataContributorImpl implements PojoTypeMetadataContributor {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final BeanProvider beanProvider;
	private final PojoRawTypeModel<?> typeModel;

	AnnotationPojoTypeMetadataContributorImpl(BeanProvider beanProvider, PojoRawTypeModel<?> typeModel) {
		this.beanProvider = beanProvider;
		this.typeModel = typeModel;
	}

	@Override
	public void contributeModel(PojoAdditionalMetadataCollectorTypeNode collector) {
		typeModel.getDeclaredProperties()
				.forEach( property -> contributePropertyModel( collector, property ) );
	}

	@Override
	public void contributeMapping(PojoMappingCollectorTypeNode collector) {
		// FIXME routing key bridge in programmatic mapping should probably be in the context of .indexed()?

		typeModel.getAnnotationsByMetaAnnotationType( RoutingKeyBridgeMapping.class )
				.forEach( annotation -> addRoutingKeyBridge( collector, annotation ) );

		typeModel.getAnnotationsByMetaAnnotationType( TypeBridgeMapping.class )
				.forEach( annotation -> addTypeBridge( collector, annotation ) );

		typeModel.getDeclaredProperties()
				.forEach( property -> contributePropertyMapping( collector, property ) );
	}

	private void contributePropertyModel(PojoAdditionalMetadataCollectorTypeNode collector, PojoPropertyModel<?> propertyModel) {
		String name = propertyModel.getName();
		propertyModel.getAnnotationsByMetaAnnotationType( MarkerMapping.class )
				.forEach( annotation -> addMarker( collector.property( name ), annotation ) );
		propertyModel.getAnnotationsByType( AssociationInverseSide.class )
				.forEach( annotation -> addAssociationInverseSide( collector.property( name ), propertyModel, annotation ) );
		propertyModel.getAnnotationsByType( IndexingDependency.class )
				.forEach( annotation -> addIndexingDependency( collector.property( name ), annotation ) );
	}

	private void contributePropertyMapping(PojoMappingCollectorTypeNode collector, PojoPropertyModel<?> propertyModel) {
		PropertyHandle handle = propertyModel.getHandle();
		propertyModel.getAnnotationsByType( DocumentId.class )
				.forEach( annotation -> addDocumentId( collector.property( handle ), propertyModel, annotation ) );
		propertyModel.getAnnotationsByMetaAnnotationType( PropertyBridgeMapping.class )
				.forEach( annotation -> addPropertyBridge( collector.property( handle ), annotation ) );
		propertyModel.getAnnotationsByType( Field.class )
				.forEach( annotation -> addField( collector.property( handle ), propertyModel, annotation ) );
		propertyModel.getAnnotationsByType( IndexedEmbedded.class )
				.forEach( annotation -> addIndexedEmbedded( collector.property( handle ), propertyModel, annotation ) );
	}

	private <A extends Annotation> void addMarker(PojoAdditionalMetadataCollectorPropertyNode collector, A annotation) {
		AnnotationMarkerBuilder<A> builder = createMarkerBuilder( annotation );
		builder.initialize( annotation );
		collector.marker( builder );
	}

	private void addAssociationInverseSide(PojoAdditionalMetadataCollectorPropertyNode collector,
			PojoPropertyModel<?> propertyModel, AssociationInverseSide annotation) {
		ContainerValueExtractorPath extractorPath = getExtractorPath(
				annotation.extractors(), AssociationInverseSide.DefaultExtractors.class
		);

		PropertyValue[] inversePathElements = annotation.inversePath();
		if ( inversePathElements.length == 0 ) {
			throw log.missingInversePathInAssociationInverseSideMapping( typeModel, propertyModel.getName() );
		}
		PojoModelPathValueNode inversePath = null;
		for ( PropertyValue element : inversePathElements ) {
			String inversePropertyName = element.propertyName();
			ContainerValueExtractorPath inverseExtractorPath = getExtractorPath(
					element.extractors(), PropertyValue.DefaultExtractors.class
			);
			if ( inversePath == null ) {
				inversePath = PojoModelPath.fromRoot( inversePropertyName ).value( inverseExtractorPath );
			}
			else {
				inversePath = inversePath.property( inversePropertyName ).value( inverseExtractorPath );
			}
		}

		collector.value( extractorPath ).associationInverseSide( inversePath );
	}

	private void addIndexingDependency(PojoAdditionalMetadataCollectorPropertyNode collector,
			IndexingDependency annotation) {
		ContainerValueExtractorPath extractorPath = getExtractorPath(
				annotation.extractors(), IndexingDependency.DefaultExtractors.class
		);

		ReindexOnUpdate reindexOnUpdate = annotation.reindexOnUpdate();

		collector.value( extractorPath ).indexingDependency( reindexOnUpdate );
	}

	private void addDocumentId(PojoMappingCollectorPropertyNode collector, PojoPropertyModel<?> propertyModel, DocumentId annotation) {
		BridgeBuilder<? extends IdentifierBridge<?>> builder = createIdentifierBridgeBuilder( annotation, propertyModel );

		collector.identifierBridge( builder );
	}

	private <A extends Annotation> void addRoutingKeyBridge(PojoMappingCollectorTypeNode collector, A annotation) {
		BridgeBuilder<? extends RoutingKeyBridge> builder = createRoutingKeyBridgeBuilder( annotation );
		collector.routingKeyBridge( builder );
	}

	private <A extends Annotation> void addTypeBridge(PojoMappingCollectorTypeNode collector, A annotation) {
		BridgeBuilder<? extends TypeBridge> builder = createTypeBridgeBuilder( annotation );
		collector.bridge( builder );
	}

	private <A extends Annotation> void addPropertyBridge(PojoMappingCollectorPropertyNode collector, A annotation) {
		BridgeBuilder<? extends PropertyBridge> builder = createPropertyBridgeBuilder( annotation );
		collector.bridge( builder );
	}

	private void addField(PojoMappingCollectorPropertyNode collector, PojoPropertyModel<?> propertyModel, Field annotation) {
		String cleanedUpRelativeFieldName = annotation.name();
		if ( cleanedUpRelativeFieldName.isEmpty() ) {
			cleanedUpRelativeFieldName = null;
		}

		BridgeBuilder<? extends ValueBridge<?, ?>> builder = createValueBridgeBuilder( annotation, propertyModel );

		ContainerValueExtractorPath extractorPath =
				getExtractorPath( annotation.extractors(), Field.DefaultExtractors.class );

		collector.value( extractorPath )
				.valueBridge( builder, cleanedUpRelativeFieldName, new AnnotationFieldModelContributor( annotation ) );
	}

	private void addIndexedEmbedded(PojoMappingCollectorPropertyNode collector, PojoPropertyModel<?> propertyModel,
			IndexedEmbedded annotation) {
		String cleanedUpPrefix = annotation.prefix();
		if ( cleanedUpPrefix.isEmpty() ) {
			cleanedUpPrefix = null;
		}

		Integer cleanedUpMaxDepth = annotation.maxDepth();
		if ( cleanedUpMaxDepth.equals( -1 ) ) {
			cleanedUpMaxDepth = null;
		}

		String[] includePathsArray = annotation.includePaths();
		Set<String> cleanedUpIncludePaths;
		if ( includePathsArray.length > 0 ) {
			cleanedUpIncludePaths = new HashSet<>();
			Collections.addAll( cleanedUpIncludePaths, includePathsArray );
		}
		else {
			cleanedUpIncludePaths = Collections.emptySet();
		}

		ContainerValueExtractorPath extractorPath =
				getExtractorPath( annotation.extractors(), IndexedEmbedded.DefaultExtractors.class );

		collector.value( extractorPath )
				.indexedEmbedded(
						cleanedUpPrefix, annotation.storage(), cleanedUpMaxDepth, cleanedUpIncludePaths
				);
	}

	private ContainerValueExtractorPath getExtractorPath(
			ContainerValueExtractorBeanReference[] extractors, Class<?> defaultExtractorsClass) {
		if ( extractors.length == 0 ) {
			return ContainerValueExtractorPath.noExtractors();
		}
		else if ( extractors.length == 1 && defaultExtractorsClass.equals( extractors[0].type() ) ) {
			return ContainerValueExtractorPath.defaultExtractors();
		}
		else {
			return ContainerValueExtractorPath.explicitExtractors(
					Arrays.stream( extractors )
							.map( ContainerValueExtractorBeanReference::type )
							.collect( Collectors.toList() )
			);
		}
	}

	private <A extends Annotation> AnnotationMarkerBuilder<A> createMarkerBuilder(A annotation) {
		MarkerMapping markerMapping = annotation.annotationType().getAnnotation( MarkerMapping.class );
		MarkerMappingBuilderReference markerBuilderReferenceAnnotation = markerMapping.builder();
		BeanReference markerBuilderReference =
				toBeanReference(
						markerBuilderReferenceAnnotation.name(),
						markerBuilderReferenceAnnotation.type(),
						MarkerMappingBuilderReference.UndefinedImplementationType.class
				)
						.orElseThrow( () -> log.missingBuilderReferenceInMarkerMapping( annotation.annotationType() ) );

		// TODO check generic parameters of builder.getClass() somehow, maybe in a similar way to what we do in PojoIndexModelBinderImpl#addValueBridge
		return beanProvider.getBean( markerBuilderReference, AnnotationMarkerBuilder.class );
	}

	private BridgeBuilder<? extends IdentifierBridge<?>> createIdentifierBridgeBuilder(
			DocumentId annotation, PojoPropertyModel<?> propertyModel) {
		IdentifierBridgeBeanReference bridgeReferenceAnnotation = annotation.identifierBridge();
		Optional<BeanReference> bridgeReference = toBeanReference(
				bridgeReferenceAnnotation.name(),
				bridgeReferenceAnnotation.type(),
				IdentifierBridgeBeanReference.UndefinedImplementationType.class
		);
		IdentifierBridgeBuilderBeanReference bridgeBuilderReferenceAnnotation = annotation.identifierBridgeBuilder();
		Optional<BeanReference> bridgeBuilderReference = toBeanReference(
				bridgeBuilderReferenceAnnotation.name(),
				bridgeBuilderReferenceAnnotation.type(),
				IdentifierBridgeBuilderBeanReference.UndefinedImplementationType.class
		);

		if ( bridgeReference.isPresent() && bridgeBuilderReference.isPresent() ) {
			throw log.invalidDocumentIdDefiningBothBridgeReferenceAndBridgeBuilderReference( propertyModel.getName() );
		}
		else if ( bridgeReference.isPresent() ) {
			// The builder will return an object of some class T where T extends ValueBridge<?, ?>, so this is safe
			@SuppressWarnings( "unchecked" )
			BridgeBuilder<? extends IdentifierBridge<?>> castedBuilder =
					new BeanResolverBridgeBuilder( IdentifierBridge.class, bridgeReference.get() );
			return castedBuilder;
		}
		else if ( bridgeBuilderReference.isPresent() ) {
			// TODO check generic parameters of builder.getClass() somehow, maybe in a similar way to what we do in PojoIndexModelBinderImpl#addValueBridge
			return beanProvider.getBean( bridgeBuilderReference.get(), BridgeBuilder.class );
		}
		else {
			// The bridge will be auto-detected from the property type
			return null;
		}
	}
	private <A extends Annotation> BridgeBuilder<? extends RoutingKeyBridge> createRoutingKeyBridgeBuilder(A annotation) {
		RoutingKeyBridgeMapping bridgeMapping = annotation.annotationType().getAnnotation( RoutingKeyBridgeMapping.class );
		RoutingKeyBridgeReference bridgeReferenceAnnotation = bridgeMapping.bridge();
		RoutingKeyBridgeAnnotationBuilderReference bridgeBuilderReferenceAnnotation = bridgeMapping.builder();

		return createAnnotationMappedBridgeBuilder(
				RoutingKeyBridgeMapping.class, RoutingKeyBridge.class, annotation,
				toBeanReference(
						bridgeReferenceAnnotation.name(),
						bridgeReferenceAnnotation.type(),
						RoutingKeyBridgeReference.UndefinedImplementationType.class
				),
				toBeanReference(
						bridgeBuilderReferenceAnnotation.name(),
						bridgeBuilderReferenceAnnotation.type(),
						RoutingKeyBridgeAnnotationBuilderReference.UndefinedImplementationType.class
				)
		);
	}

	private <A extends Annotation> BridgeBuilder<? extends TypeBridge> createTypeBridgeBuilder(A annotation) {
		TypeBridgeMapping bridgeMapping = annotation.annotationType().getAnnotation( TypeBridgeMapping.class );
		TypeBridgeReference bridgeReferenceAnnotation = bridgeMapping.bridge();
		TypeBridgeAnnotationBuilderReference bridgeBuilderReferenceAnnotation = bridgeMapping.builder();

		return createAnnotationMappedBridgeBuilder(
				TypeBridgeMapping.class, TypeBridge.class, annotation,
				toBeanReference(
						bridgeReferenceAnnotation.name(),
						bridgeReferenceAnnotation.type(),
						TypeBridgeReference.UndefinedImplementationType.class
				),
				toBeanReference(
						bridgeBuilderReferenceAnnotation.name(),
						bridgeBuilderReferenceAnnotation.type(),
						TypeBridgeAnnotationBuilderReference.UndefinedImplementationType.class
				)
		);
	}

	private <A extends Annotation> BridgeBuilder<? extends PropertyBridge> createPropertyBridgeBuilder(A annotation) {
		PropertyBridgeMapping bridgeMapping = annotation.annotationType().getAnnotation( PropertyBridgeMapping.class );
		PropertyBridgeReference bridgeReferenceAnnotation = bridgeMapping.bridge();
		PropertyBridgeAnnotationBuilderReference bridgeBuilderReferenceAnnotation = bridgeMapping.builder();

		return createAnnotationMappedBridgeBuilder(
				PropertyBridgeMapping.class, PropertyBridge.class, annotation,
				toBeanReference(
						bridgeReferenceAnnotation.name(),
						bridgeReferenceAnnotation.type(),
						PropertyBridgeReference.UndefinedImplementationType.class
				),
				toBeanReference(
						bridgeBuilderReferenceAnnotation.name(),
						bridgeBuilderReferenceAnnotation.type(),
						PropertyBridgeAnnotationBuilderReference.UndefinedImplementationType.class
				)
		);
	}

	private BridgeBuilder<? extends ValueBridge<?, ?>> createValueBridgeBuilder(
			Field annotation, PojoPropertyModel<?> propertyModel) {
		ValueBridgeBeanReference bridgeReferenceAnnotation = annotation.valueBridge();
		Optional<BeanReference> bridgeReference = toBeanReference(
				bridgeReferenceAnnotation.name(),
				bridgeReferenceAnnotation.type(),
				ValueBridgeBeanReference.UndefinedImplementationType.class
		);
		ValueBridgeBuilderBeanReference bridgeBuilderReferenceAnnotation = annotation.valueBridgeBuilder();
		Optional<BeanReference> bridgeBuilderReference = toBeanReference(
				bridgeBuilderReferenceAnnotation.name(),
				bridgeBuilderReferenceAnnotation.type(),
				ValueBridgeBuilderBeanReference.UndefinedImplementationType.class
		);

		if ( bridgeReference.isPresent() && bridgeBuilderReference.isPresent() ) {
			throw log.invalidFieldDefiningBothBridgeReferenceAndBridgeBuilderReference( propertyModel.getName() );
		}
		else if ( bridgeReference.isPresent() ) {
			// The builder will return an object of some class T where T extends ValueBridge<?, ?>, so this is safe
			@SuppressWarnings( "unchecked" )
			BridgeBuilder<? extends ValueBridge<?, ?>> castedBuilder =
					new BeanResolverBridgeBuilder( ValueBridge.class, bridgeReference.get() );
			return castedBuilder;
		}
		else if ( bridgeBuilderReference.isPresent() ) {
			// TODO check generic parameters of builder.getClass() somehow, maybe in a similar way to what we do in PojoIndexModelBinderImpl#addValueBridge
			return beanProvider.getBean( bridgeBuilderReference.get(), BridgeBuilder.class );
		}
		else {
			// The bridge will be auto-detected from the property type
			return null;
		}
	}

	private <A extends Annotation, B> BridgeBuilder<B> createAnnotationMappedBridgeBuilder(
			Class<? extends Annotation> bridgeMappingAnnotation, Class<B> bridgeClass, A annotation,
			Optional<BeanReference> bridgeReferenceOptional, Optional<BeanReference> builderReferenceOptional) {
		if ( bridgeReferenceOptional.isPresent() && builderReferenceOptional.isPresent() ) {
			throw log.conflictingBridgeReferenceInBridgeMapping( bridgeMappingAnnotation, annotation.annotationType() );
		}
		else if ( bridgeReferenceOptional.isPresent() ) {
			return new BeanResolverBridgeBuilder<>( bridgeClass, bridgeReferenceOptional.get() );
		}
		else if ( builderReferenceOptional.isPresent() ) {
			AnnotationBridgeBuilder builder = beanProvider.getBean( builderReferenceOptional.get(), AnnotationBridgeBuilder.class );
			// TODO check generic parameters of builder.getClass() somehow, maybe in a similar way to what we do in PojoIndexModelBinderImpl#addValueBridge
			builder.initialize( annotation );
			return builder;
		}
		else {
			throw log.missingBridgeReferenceInBridgeMapping( bridgeMappingAnnotation, annotation.annotationType() );
		}
	}

	private Optional<BeanReference> toBeanReference(String name, Class<?> type, Class<?> undefinedTypeMarker) {
		String cleanedUpName = name.isEmpty() ? null : name;
		Class<?> cleanedUpType = undefinedTypeMarker.equals( type ) ? null : type;
		if ( cleanedUpName == null && cleanedUpType == null ) {
			return Optional.empty();
		}
		else {
			return Optional.of( new ImmutableBeanReference( cleanedUpName, cleanedUpType ) );
		}
	}

	private static class AnnotationFieldModelContributor implements FieldModelContributor {

		private final Field annotation;

		private AnnotationFieldModelContributor(Field annotation) {
			this.annotation = annotation;
		}

		@Override
		public void contribute(IndexSchemaFieldTypedContext<?> context) {
			if ( !Store.DEFAULT.equals( annotation.store() ) ) {
				context.store( annotation.store() );
			}
			if ( !Sortable.DEFAULT.equals( annotation.sortable() ) ) {
				context.sortable( annotation.sortable() );
			}
			if ( !annotation.analyzer().isEmpty() ) {
				context.analyzer( annotation.analyzer() );
			}
			if ( !annotation.normalizer().isEmpty() ) {
				context.normalizer( annotation.normalizer() );
			}
		}
	}
}
