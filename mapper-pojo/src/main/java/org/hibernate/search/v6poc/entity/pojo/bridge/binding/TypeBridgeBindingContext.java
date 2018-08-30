/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.binding;

import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.v6poc.entity.pojo.bridge.TypeBridge;
import org.hibernate.search.v6poc.entity.pojo.model.PojoModelType;

/**
 * The context provided to the {@link TypeBridge#bind(TypeBridgeBindingContext)} method.
 */
public interface TypeBridgeBindingContext {

	/**
	 * @return An entry point allowing to declare expectations and retrieve accessors to the bridged POJO type.
	 */
	PojoModelType getBridgedElement();

	/**
	 * @return An entry point allowing to declare expectations and retrieve accessors to the index schema.
	 */
	IndexSchemaElement getIndexSchemaElement();

}
