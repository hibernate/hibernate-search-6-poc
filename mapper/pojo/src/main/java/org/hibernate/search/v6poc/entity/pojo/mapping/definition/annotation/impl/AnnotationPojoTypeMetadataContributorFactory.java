/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.impl;

import org.hibernate.search.v6poc.entity.pojo.mapping.building.spi.PojoTypeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyMappingContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.impl.TypeMappingContextImpl;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoPropertyModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;

class AnnotationPojoTypeMetadataContributorFactory {

	private final AnnotationProcessorProvider annotationProcessorProvider;

	AnnotationPojoTypeMetadataContributorFactory(AnnotationProcessorProvider annotationProcessorProvider) {
		this.annotationProcessorProvider = annotationProcessorProvider;
	}

	public PojoTypeMetadataContributor create(PojoRawTypeModel<?> typeModel) {
		// Create a programmatic type mapping object
		TypeMappingContextImpl typeMappingContext = new TypeMappingContextImpl( typeModel );

		// Process annotations and add metadata to the type mapping
		processTypeLevelAnnotations( typeMappingContext, typeModel );
		typeModel.getDeclaredProperties()
				.forEach( propertyModel -> processPropertyLevelAnnotations( typeMappingContext, typeModel, propertyModel ) );

		// Return the resulting mapping, which includes all the metadata extracted from annotations
		return typeMappingContext;
	}

	private void processTypeLevelAnnotations(TypeMappingContextImpl typeMappingContext, PojoRawTypeModel<?> typeModel) {
		for ( TypeAnnotationProcessor<?> processor : annotationProcessorProvider.getTypeAnnotationProcessors() ) {
			processor.process(
					typeMappingContext, typeModel
			);
		}
	}

	private void processPropertyLevelAnnotations(TypeMappingContextImpl typeMappingContext,
			PojoRawTypeModel<?> typeModel, PojoPropertyModel<?> propertyModel) {
		String propertyName = propertyModel.getName();
		PropertyMappingContext mappingContext = typeMappingContext.property( propertyName );

		for ( PropertyAnnotationProcessor<?> processor : annotationProcessorProvider.getPropertyAnnotationProcessors() ) {
			processor.process(
					mappingContext, typeModel, propertyModel
			);
		}
	}

}
