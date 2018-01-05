/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.spi;

import org.hibernate.search.v6poc.backend.document.model.spi.FieldModelContext;
import org.hibernate.search.v6poc.backend.document.model.spi.TypedFieldModelContext;
import org.hibernate.search.v6poc.engine.spi.BuildContext;

/**
 * @author Yoann Rodiere
 */
public interface FunctionBridge<T, R> extends AutoCloseable {

	/* Solves HSEARCH-1306 */
	default void initialize(BuildContext buildContext) {
	}

	default TypedFieldModelContext<R> bind(FieldModelContext context) {
		return null; // Auto-detect the return type and use default encoding
	}

	R toIndexedValue(T propertyValue);

	/*
	 * TODO use this method when projecting.
	 */
	default Object fromIndexedValue(R fieldValue) {
		return fieldValue;
	}

	@Override
	default void close() {
	}

}
