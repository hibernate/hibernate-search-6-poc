/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.MarkerDefinition;
import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoModelElementAccessor;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoModelElement;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoPropertyNodeModelCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.TypeModel;
import org.hibernate.search.v6poc.util.SearchException;


/**
 * @author Yoann Rodiere
 */
public class PojoModelProperty extends AbstractPojoModelElement
		implements PojoModelElement, PojoPropertyNodeModelCollector {

	private final AbstractPojoModelElement parent;

	private final PropertyModel<?> propertyModel;

	private final Map<Class<? extends Annotation>, List<? extends Annotation>> markers = new HashMap<>();

	public PojoModelProperty(AbstractPojoModelElement parent, PropertyModel<?> propertyModel,
			TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> modelContributorProvider) {
		super( modelContributorProvider );
		this.parent = parent;
		this.propertyModel = propertyModel;
	}

	@Override
	public <T> PojoModelElementAccessor<T> createAccessor(Class<T> requestedType) {
		if ( !isAssignableTo( requestedType ) ) {
			throw new SearchException( "Requested incompatible type for '" + this.createAccessor() + "': '" + requestedType + "'" );
		}
		return new PojoPropertyAccessor<>( parent.createAccessor(), getHandle() );
	}

	@Override
	public PojoModelElementAccessor<?> createAccessor() {
		return new PojoPropertyAccessor<>( parent.createAccessor(), getHandle() );
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Annotation> Stream<M> markers(Class<M> markerType) {
		return ( (List<M>) this.markers.getOrDefault( markerType, Collections.emptyList() ) )
				.stream();
	}

	@Override
	public final void marker(MarkerDefinition<?> definition) {
		doAddMarker( definition );
	}

	public PropertyHandle getHandle() {
		return propertyModel.getHandle();
	}

	@Override
	protected Class<?> getJavaType() {
		return propertyModel.getJavaType();
	}

	@Override
	protected TypeModel<?> getTypeModel() {
		return propertyModel.getTypeModel();
	}

	public PropertyModel<?> getPropertyModel() {
		return propertyModel;
	}

	@SuppressWarnings("unchecked")
	private <M extends Annotation> void doAddMarker(MarkerDefinition<M> definition) {
		M marker = definition.get();
		Class<M> markerType = (Class<M>) marker.annotationType();
		List<M> list = (List<M>) markers.computeIfAbsent( markerType, ignored -> new ArrayList<M>() );
		list.add( marker );
	}
}
