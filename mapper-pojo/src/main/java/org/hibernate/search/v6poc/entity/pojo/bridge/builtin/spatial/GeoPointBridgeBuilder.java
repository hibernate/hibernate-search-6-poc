/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.builtin.spatial;

import org.hibernate.search.v6poc.backend.document.model.Store;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.builtin.spatial.impl.GeoPointBridgeImpl;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.AnnotationBridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.Bridge;


/**
 * @author Yoann Rodiere
 */
public class GeoPointBridgeBuilder implements AnnotationBridgeBuilder<Bridge, GeoPointBridge> {

	private String fieldName;
	private Store store;
	private String markerSet;

	@Override
	public void initialize(GeoPointBridge annotation) {
		fieldName( annotation.fieldName() );
		markerSet( annotation.markerSet() );
		store( annotation.store() );
	}

	public GeoPointBridgeBuilder fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public GeoPointBridgeBuilder store(Store store) {
		this.store = store;
		return this;
	}

	public GeoPointBridgeBuilder markerSet(String markerSet) {
		this.markerSet = markerSet;
		return this;
	}

	@Override
	public Bridge build(BuildContext buildContext) {
		return new GeoPointBridgeImpl( fieldName, store, markerSet );
	}

}
