/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.builtin.spatial;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.search.v6poc.backend.document.model.Store;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.spi.BridgeMappingBuilderReference;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.spi.BridgeMapping;

/**
 * Defines a GeoPoint bridge, mapping a latitude and longitude, in degrees,
 * to an index field representing a point on earth..
 *
 * If your longitude and latitude information are hosted on two different properties,
 * add {@code @GeoPointBridge} on the entity (class-level). The {@link Latitude} and {@link Longitude}
 * annotations must mark the properties.
 *
 * <pre><code>
 * &commat;GeoPointBridge(name="home")
 * public class User {
 *     &commat;GeoPointBridge.Latitude
 *     public Double getHomeLatitude() { ... }
 *     &commat;;GeoPointBridge.Longitude
 *     public Double getHomeLongitude() { ... }
 * }
 * </code></pre>
 *
 * Alternatively, you can put the latitude / longitude information in a property of
 * type {@link GeoPoint}.
 *
 * <pre><code>
 * public class User {
 *     &commat;GeoPointBridge
 *     public Coordinates getHome() { ... }
 * }
 * </code></pre>
 *
 * ... or make the entity itself implement {@link GeoPoint}:
 *
 * <pre><code>
 * &commat;GeoPointBridge(name="location")
 * public class Home implements Coordinates {
 *     &commat;Override
 *     public Double getLatitude() { ... }
 *     &commat;Override
 *     public Double getLongitude() { ... }
 * }
 * </code></pre>
 *
 * @hsearch.experimental Spatial support is still considered experimental
 * @author Nicolas Helleringer
 */
@BridgeMapping(builderType = @BridgeMappingBuilderReference(type = GeoPointBridgeBuilder.class))
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Documented
@Repeatable(GeoPointBridge.List.class)
public @interface GeoPointBridge {

	int DEFAULT_TOP_SPATIAL_HASH_LEVEL = 0;
	int DEFAULT_BOTTOM_SPATIAL_HASH_LEVEL = 16;

	/**
	 * The name of the index field holding spatial information.
	 *
	 * If {@code @Spatial} is hosted on a property, defaults to the property name.
	 * If {@code @Spatial} is hosted on a class, the name must be provided.
	 *
	 * @return the field name
	 */
	String fieldName() default "";

	/**
	 * @return Returns an instance of the {@link Store} enum, indicating whether the value should be stored in the document.
	 *         Defaults to {@code Store.NO}
	 */
	Store store() default Store.NO;

	/**
	 * @return The name of the marker set this spatial should look into
	 * when looking for the {@link Latitude} and {@link Longitude} markers.
	 */
	String markerSet() default "";

	/**
	 * Mark the property hosting the latitude of a specific spatial coordinate.
	 * The property must be of type {@code Double} or {@code double}.
	 *
	 * @author Nicolas Helleringer
	 */
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.METHOD, ElementType.FIELD } )
	@Documented
	public @interface Latitude {

		/**
		 * @return The name of the marker set this marker belongs to.
		 * Set it to the value of {@link GeoPointBridge#markerSet()}
		 * so that the bridge detects this marker.
		 */
		String markerSet() default "";

	}

	/**
	 * Mark the property hosting the longitude of a specific spatial coordinate.
	 * The property must be of type {@code Double} or {@code double}.
	 *
	 * @author Nicolas Helleringer
	 */
	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.METHOD, ElementType.FIELD } )
	@Documented
	public @interface Longitude {

		/**
		 * @return The name of the marker set this marker belongs to.
		 * Set it to the value of {@link GeoPointBridge#markerSet()}
		 * so that the bridge detects this marker.
		 */
		String markerSet() default "";

	}

	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.TYPE } )
	@Documented
	public @interface List {

		GeoPointBridge[] value();

	}
}