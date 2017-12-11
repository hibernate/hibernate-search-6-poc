/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.model.spi;

import java.time.LocalDate;

import org.hibernate.search.v6poc.backend.spatial.GeoPoint;


/**
 * @author Yoann Rodiere
 */
public interface FieldModelContext {

	<T> TypedFieldModelContext<T> from(Class<T> inputType);

	TypedFieldModelContext<String> fromString();

	TypedFieldModelContext<Integer> fromInteger();

	TypedFieldModelContext<LocalDate> fromLocalDate();

	TypedFieldModelContext<GeoPoint> fromGeoPoint();

	// TODO NumericBridgeProvider
	// TODO JavaTimeBridgeProvider
	// TODO BasicJDKTypesBridgeProvider

	default <T> T withExtension(FieldModelExtension<T> extension) {
		return extension.extendOrFail( this );
	}

}
