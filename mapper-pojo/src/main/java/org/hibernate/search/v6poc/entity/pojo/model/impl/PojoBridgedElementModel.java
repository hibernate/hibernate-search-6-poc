/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.pojo.model.spi.BridgedElementReader;
import org.hibernate.search.v6poc.entity.pojo.model.spi.BridgedElementModel;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeModelCollector;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.TypeModel;


/**
 * @author Yoann Rodiere
 */
public abstract class PojoBridgedElementModel implements BridgedElementModel, PojoTypeNodeModelCollector {

	private final TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> modelContributorProvider;

	private final Map<String, PojoPropertyBridgedElementModel> propertyModelsByName = new HashMap<>();

	private boolean markersForTypeInitialized = false;

	public PojoBridgedElementModel(TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> modelContributorProvider) {
		this.modelContributorProvider = modelContributorProvider;
	}

	@Override
	public abstract BridgedElementReader<?> createReader();

	@Override
	public boolean isAssignableTo(Class<?> clazz) {
		return clazz.isAssignableFrom( getJavaType() );
	}

	@Override
	public PojoPropertyBridgedElementModel property(String relativeName) {
		initMarkersForType();
		return propertyModelsByName.computeIfAbsent( relativeName, name -> {
			PropertyModel<?> model = getTypeModel().getProperty( name );
			return new PojoPropertyBridgedElementModel( this, model, modelContributorProvider );
		} );
	}

	@SuppressWarnings({ "unchecked", "rawtypes" }) // Stream is covariant in T
	@Override
	public Stream<BridgedElementModel> properties() {
		initMarkersForType();
		return (Stream<BridgedElementModel>) (Stream) propertyModelsByName.values().stream();
	}

	/*
	 * Lazily initialize markers.
	 * Lazy initialization is necessary to avoid inifinite recursion.
	 */
	private void initMarkersForType() {
		if ( !markersForTypeInitialized ) {
			this.markersForTypeInitialized = true;
			getModelContributorProvider().get( new PojoIndexedTypeIdentifier( getJavaType() ) )
					.forEach( c -> c.contributeModel( this ) );
		}
	}

	protected abstract TypeModel<?> getTypeModel();

	protected abstract Class<?> getJavaType();

	protected TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> getModelContributorProvider() {
		return modelContributorProvider;
	}

}
