/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.types.predicate.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonObjectAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl.AbstractSearchPredicateBuilder;
import org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl.ElasticsearchSearchPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinPolygonPredicateBuilder;
import org.hibernate.search.v6poc.spatial.GeoPoint;
import org.hibernate.search.v6poc.spatial.GeoPolygon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class GeoPointSpatialWithinPolygonPredicateBuilder extends AbstractSearchPredicateBuilder
		implements SpatialWithinPolygonPredicateBuilder<ElasticsearchSearchPredicateBuilder> {

	private static final JsonObjectAccessor GEO_POLYGON = JsonAccessor.root().property( "geo_polygon" ).asObject();

	private static final String POINTS = "points";

	private final String absoluteFieldPath;

	GeoPointSpatialWithinPolygonPredicateBuilder(String absoluteFieldPath) {
		this.absoluteFieldPath = absoluteFieldPath;
	}

	@Override
	public void polygon(GeoPolygon polygon) {
		getInnerObject().add( absoluteFieldPath, toPointsObject( polygon ) );
	}

	@Override
	protected JsonObject doBuild() {
		JsonObject outerObject = getOuterObject();
		GEO_POLYGON.set( outerObject, getInnerObject() );
		return outerObject;
	}

	private JsonObject toPointsObject(GeoPolygon polygon) {
		JsonArray pointsArray = new JsonArray();

		for ( GeoPoint point : polygon.getPoints() ) {
			JsonArray pointArray = new JsonArray();
			pointArray.add( point.getLongitude() );
			pointArray.add( point.getLatitude() );

			pointsArray.add( pointArray );
		}

		JsonObject pointsObject = new JsonObject();
		pointsObject.add( POINTS, pointsArray );

		return pointsObject;
	}
}
