/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge;


import org.hibernate.search.v6poc.entity.pojo.model.PojoModelValue;

/**
 * A bridge between a POJO property of type {@code T} and a document identifier.
 *
 * @author Yoann Rodiere
 */
public interface IdentifierBridge<T> extends AutoCloseable {

	/**
	 * Bind this bridge instance to the given POJO model element.
	 * <p>
	 * This method is called exactly once for each bridge instance, before any other method.
	 * It allows the bridge to inspect the type of values extracted from the POJO model that will be passed to this bridge.
	 *
	 * @param pojoModelValue An entry point to inspecting the type of values that will be passed to this bridge.
	 */
	default void bind(PojoModelValue<T> pojoModelValue) {
		// No-op by default
	}

	/**
	 * Transform the given POJO property value to the value of the document identifier.
	 * <p>
	 * Must return a unique value for each value of {@code propertyValue}
	 *
	 * @param propertyValue The POJO property value to be transformed.
	 * @return The value of the document identifier.
	 */
	String toDocumentIdentifier(T propertyValue);

	/**
	 * Transform the given document identifier value back to the value of the POJO property.
	 * <p>
	 * Must be the exact inverse function of {@link #toDocumentIdentifier(Object)},
	 * i.e. {@code object.equals(fromDocumentIdentifier(toDocumentIdentifier(object)))} must always be true.
	 *
	 * @param documentIdentifier The document identifier value to be transformed.
	 * @return The value of the document identifier.
	 */
	T fromDocumentIdentifier(String documentIdentifier);

	/**
	 * Close any resource before the bridge is discarded.
	 */
	@Override
	default void close() {
	}

}
