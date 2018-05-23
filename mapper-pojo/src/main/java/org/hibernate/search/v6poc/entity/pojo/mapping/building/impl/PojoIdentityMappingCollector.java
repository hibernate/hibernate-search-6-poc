/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.building.impl;

import org.hibernate.search.v6poc.entity.pojo.bridge.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.entity.pojo.bridge.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoTypeModel;

public interface PojoIdentityMappingCollector {

	<T> void identifierBridge(PojoTypeModel<T> propertyTypeModel, PropertyHandle handle, IdentifierBridge<T> bridge);

	void routingKeyBridge(RoutingKeyBridge bridge);
}
