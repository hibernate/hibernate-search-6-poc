/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.binding;

import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.v6poc.entity.pojo.bridge.PropertyBridge;
import org.hibernate.search.v6poc.entity.pojo.model.PojoModelProperty;

/**
 * The context provided to the {@link PropertyBridge#bind(PropertyBridgeBindingContext)} method.
 */
public interface PropertyBridgeBindingContext {

	/**
	 * @return An entry point allowing to declare expectations and retrieve accessors to the bridged POJO property.
	 */
	PojoModelProperty getBridgedElement();

	/**
	 * @return An entry point allowing to declare expectations and retrieve accessors to the index schema.
	 */
	IndexSchemaElement getIndexSchemaElement();

	// TODO add methods to register search predicate bridging (from single POJO model value to single index field value)

	// TODO add methods to register backward bridging (from single index field value to single POJO model value)

	// TODO add methods to register multiple backward bridging (from multiple index field value to single POJO model value)

}
