/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.building.impl;

import org.hibernate.search.v6poc.backend.document.model.spi.IndexModelCollector;
import org.hibernate.search.v6poc.engine.spi.BeanReference;
import org.hibernate.search.v6poc.entity.mapping.building.spi.FieldModelContributor;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.pojo.model.spi.IndexableModel;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeDefinition;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.FunctionBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.ValueProcessor;

/**
 * Provides the ability to bind the entity model to the index model
 * by creating bridges and
 * {@link org.hibernate.search.v6poc.entity.pojo.bridge.spi.Bridge#bind(IndexableModel, IndexModelCollector) binding}
 * them.
 * <p>
 * Incidentally, this will also generate the index model,
 * due to bridges contributing to the index model as we bind them.
 *
 * @author Yoann Rodiere
 */
public interface PojoIndexModelBinder {

	<T> IdentifierBridge<T> createIdentifierBridge(Class<T> sourceType,
			BeanReference<? extends IdentifierBridge<?>> reference);

	RoutingKeyBridge addRoutingKeyBridge(IndexModelBindingContext bindingContext,
			IndexableModel indexableModel, BeanReference<? extends RoutingKeyBridge> reference);

	ValueProcessor addBridge(IndexModelBindingContext bindingContext,
			IndexableModel indexableModel, BridgeDefinition<?> definition);

	ValueProcessor addFunctionBridge(IndexModelBindingContext bindingContext,
			IndexableModel indexableModel, Class<?> sourceType,
			BeanReference<? extends FunctionBridge<?, ?>> bridgeReference,
			String fieldName, FieldModelContributor contributor);

}
