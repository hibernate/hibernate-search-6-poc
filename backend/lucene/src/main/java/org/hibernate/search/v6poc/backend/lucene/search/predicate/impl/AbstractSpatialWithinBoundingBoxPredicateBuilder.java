/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.predicate.impl;

import org.hibernate.search.v6poc.search.predicate.spi.SpatialWithinBoundingBoxPredicateBuilder;
import org.hibernate.search.v6poc.spatial.GeoBoundingBox;


public abstract class AbstractSpatialWithinBoundingBoxPredicateBuilder<F> extends AbstractSearchPredicateBuilder
		implements SpatialWithinBoundingBoxPredicateBuilder<LuceneSearchPredicateBuilder> {

	protected final String absoluteFieldPath;

	protected GeoBoundingBox boundingBox;

	protected AbstractSpatialWithinBoundingBoxPredicateBuilder(String absoluteFieldPath) {
		this.absoluteFieldPath = absoluteFieldPath;
	}

	@Override
	public void boundingBox(GeoBoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
}
