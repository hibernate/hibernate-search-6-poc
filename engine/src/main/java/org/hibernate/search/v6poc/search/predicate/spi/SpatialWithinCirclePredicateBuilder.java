/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.predicate.spi;

import org.hibernate.search.v6poc.spatial.DistanceUnit;
import org.hibernate.search.v6poc.spatial.GeoPoint;

public interface SpatialWithinCirclePredicateBuilder<B> extends SearchPredicateBuilder<B> {

	void circle(GeoPoint center, double radius, DistanceUnit unit);

}
