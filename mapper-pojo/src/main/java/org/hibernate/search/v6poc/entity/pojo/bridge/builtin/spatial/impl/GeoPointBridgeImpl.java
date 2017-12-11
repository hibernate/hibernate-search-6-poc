/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.builtin.spatial.impl;

import java.util.function.Function;
import java.util.stream.Collector;

import org.hibernate.search.v6poc.backend.document.model.spi.IndexModelCollector;
import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.backend.document.spi.IndexFieldReference;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.entity.pojo.bridge.builtin.spatial.GeoPointBridge;
import org.hibernate.search.v6poc.backend.spatial.ImmutableGeoPoint;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.Bridge;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.pojo.model.spi.Indexable;
import org.hibernate.search.v6poc.entity.pojo.model.spi.IndexableModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.IndexableReference;
import org.hibernate.search.v6poc.util.SearchException;
import org.hibernate.search.v6poc.util.StreamHelper;

/**
 * @author Yoann Rodiere
 */
public class GeoPointBridgeImpl implements Bridge<GeoPointBridge> {

	private GeoPointBridge parameters;

	private IndexFieldReference<GeoPoint> fieldReference;
	private Function<Indexable, GeoPoint> coordinatesExtractor;

	@Override
	public void initialize(BuildContext buildContext, GeoPointBridge parameters) {
		this.parameters = parameters;
	}

	@Override
	public void bind(IndexableModel indexableModel, IndexModelCollector indexModelCollector) {
		String fieldName = parameters.fieldName();

		if ( fieldName.isEmpty() ) {
			// TODO retrieve the default name somehow when parameters.name() is empty
			throw new UnsupportedOperationException( "Default field name not implemented yet" );
		}

		fieldReference = indexModelCollector.field( fieldName ).fromGeoPoint().asReference();

		if ( indexableModel.isAssignableTo( GeoPoint.class ) ) {
			IndexableReference<GeoPoint> sourceReference = indexableModel.asReference( GeoPoint.class );
			coordinatesExtractor = indexable -> indexable.get( sourceReference );
		}
		else {
			String markerSet = parameters.markerSet();

			IndexableReference<Double> latitudeReference = indexableModel.properties()
					.filter( model -> model.markers( GeoPointBridge.Latitude.class )
							.anyMatch( m -> markerSet.equals( m.markerSet() ) ) )
					.collect( singleMarkedProperty( "latitude", fieldName, markerSet ) )
					.asReference( Double.class );
			IndexableReference<Double> longitudeReference = indexableModel.properties()
					.filter( model -> model.markers( GeoPointBridge.Longitude.class )
							.anyMatch( m -> markerSet.equals( m.markerSet() ) ) )
					.collect( singleMarkedProperty( "longitude", fieldName, markerSet ) )
					.asReference( Double.class );

			coordinatesExtractor = indexable -> {
				Double latitude = indexable.get( latitudeReference );
				Double longitude = indexable.get( longitudeReference );

				if ( latitude == null || longitude == null ) {
					return null;
				}

				return new ImmutableGeoPoint( latitude, longitude );
			};
		}
	}

	private static Collector<IndexableModel, ?, IndexableModel> singleMarkedProperty(
			String markerName, String fieldName, String markerSet) {
		return StreamHelper.singleElement(
				() -> new SearchException( "Could not find a property with the " + markerName
						+ " marker for field '" + fieldName + "' (marker set: '" + markerSet + "')" ),
				() -> new SearchException( "Found multiple properties with the " + markerName
						+ " marker for field '" + fieldName + "' (marker set: '" + markerSet + "')" )
				);
	}

	@Override
	public void toDocument(Indexable source, DocumentState target) {
		GeoPoint coordinates = coordinatesExtractor.apply( source );
		fieldReference.add( target, coordinates );
	}

}
