/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl;

import org.hibernate.search.v6poc.backend.document.impl.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.document.impl.ElasticsearchIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.DataType;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.PropertyMapping;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonElementType;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.backend.spatial.ImmutableGeoPoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * @author Yoann Rodiere
 * @author Guillaume Smet
 */
class GeoPointFieldModelContext extends AbstractScalarFieldModelContext<GeoPoint> {

	private final String relativeName;

	public GeoPointFieldModelContext(String relativeName) {
		this.relativeName = relativeName;
	}

	@Override
	protected PropertyMapping contribute(DeferredInitializationIndexFieldAccessor<GeoPoint> reference,
			ElasticsearchIndexSchemaNodeCollector collector,
			ElasticsearchObjectNodeModel parentModel) {
		PropertyMapping mapping = super.contribute( reference, collector, parentModel );

		ElasticsearchFieldModel model = new ElasticsearchFieldModel( parentModel, GeoPointFieldFormatter.INSTANCE );

		JsonAccessor<JsonElement> jsonAccessor = JsonAccessor.root().property( relativeName );
		reference.initialize( new ElasticsearchIndexFieldAccessor<>( jsonAccessor, model ) );
		mapping.setType( DataType.GEO_POINT );

		String absolutePath = parentModel.getAbsolutePath( relativeName );
		collector.collect( absolutePath, model );

		return mapping;
	}

	private static final class GeoPointFieldFormatter implements ElasticsearchFieldFormatter {
		// Must be a singleton so that equals() works as required by the interface
		public static final GeoPointFieldFormatter INSTANCE = new GeoPointFieldFormatter();

		private static final JsonAccessor<Double> LATITUDE_ACCESSOR =
				JsonAccessor.root().property( "lat" ).asDouble();
		private static final JsonAccessor<Double> LONGITUDE_ACCESSOR =
				JsonAccessor.root().property( "lon" ).asDouble();

		private GeoPointFieldFormatter() {
		}

		@Override
		public JsonElement format(Object object) {
			if ( object == null ) {
				return JsonNull.INSTANCE;
			}
			GeoPoint value = (GeoPoint) object;
			JsonObject result = new JsonObject();
			LATITUDE_ACCESSOR.set( result, value.getLatitude() );
			LONGITUDE_ACCESSOR.set( result, value.getLongitude() );
			return result;
		}

		@Override
		public Object parse(JsonElement element) {
			if ( element == null || element.isJsonNull() ) {
				return null;
			}
			JsonObject object = JsonElementType.OBJECT.fromElement( element );
			double latitude = LATITUDE_ACCESSOR.get( object ).get();
			double longitude = LONGITUDE_ACCESSOR.get( object ).get();
			return new ImmutableGeoPoint( latitude, longitude );
		}
	}

}
