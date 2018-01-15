/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMetadataContributorProvider;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoIndexModelBinder;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoPropertyNodeMappingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeIdentityMappingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeMappingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.TypeModel;

/**
 * @author Yoann Rodiere
 */
public class PojoTypeNodeProcessorBuilder extends AbstractPojoProcessorBuilder
		implements PojoTypeNodeMappingCollector {

	private final Map<String, PojoPropertyNodeProcessorBuilder> propertyProcessorBuilders = new HashMap<>();

	public PojoTypeNodeProcessorBuilder(
			TypeModel<?> typeModel,
			TypeMetadataContributorProvider<PojoTypeNodeMetadataContributor> contributorProvider,
			PojoIndexModelBinder indexModelBinder, IndexModelBindingContext bindingContext,
			PojoTypeNodeIdentityMappingCollector identityMappingCollector) {
		super( typeModel, contributorProvider, indexModelBinder, bindingContext,
				identityMappingCollector );
	}

	@Override
	public void routingKeyBridge(BridgeBuilder<? extends RoutingKeyBridge> builder) {
		RoutingKeyBridge bridge = indexModelBinder.addRoutingKeyBridge( bindingContext, indexableModel, builder );
		identityMappingCollector.routingKeyBridge( bridge );
	}

	@Override
	public PojoPropertyNodeMappingCollector property(String name) {
		return propertyProcessorBuilders.computeIfAbsent( name, this::createPropertyProcessorBuilder );
	}

	private PojoPropertyNodeProcessorBuilder createPropertyProcessorBuilder(String name) {
		PropertyModel<?> propertyModel = indexableModel.property( name ).getPropertyModel();
		return new PojoPropertyNodeProcessorBuilder( propertyModel,
				contributorProvider, indexModelBinder, bindingContext, identityMappingCollector
		);
	}

	public PojoTypeNodeProcessor build() {
		return new PojoTypeNodeProcessor( processors, propertyProcessorBuilders.values() );
	}

}
