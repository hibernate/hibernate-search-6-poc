/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic;

import org.hibernate.search.v6poc.backend.document.model.Store;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.FunctionBridge;

/**
 * @author Yoann Rodiere
 */
public interface PropertyFieldMappingContext extends PropertyMappingContext {

	PropertyFieldMappingContext name(String name);

	PropertyFieldMappingContext bridge(String bridgeName);

	PropertyFieldMappingContext bridge(Class<? extends FunctionBridge<?, ?>> bridgeClass);

	PropertyFieldMappingContext bridge(String bridgeName, Class<? extends FunctionBridge<?, ?>> bridgeClass);

	PropertyFieldMappingContext store(Store store);

}
