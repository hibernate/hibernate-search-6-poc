/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.building.impl;

import org.hibernate.search.v6poc.backend.document.model.spi.FieldModelContext;
import org.hibernate.search.v6poc.backend.document.model.spi.TypedFieldModelContext;
import org.hibernate.search.v6poc.backend.document.spi.IndexFieldAccessor;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.FieldModelContributor;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.impl.BridgeResolver;
import org.hibernate.search.v6poc.entity.pojo.bridge.impl.FunctionBridgeUtil;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.Bridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.FunctionBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.RoutingKeyBridge;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoModelElement;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoModelElementAccessor;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.BridgeValueProcessor;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.FunctionBridgeValueProcessor;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.ValueProcessor;
import org.hibernate.search.v6poc.util.SearchException;

/**
 * @author Yoann Rodiere
 */
public class PojoIndexModelBinderImpl implements PojoIndexModelBinder {

	private final BuildContext buildContext;
	private final BridgeResolver bridgeResolver;

	public PojoIndexModelBinderImpl(BuildContext buildContext, BridgeResolver bridgeResolver) {
		this.buildContext = buildContext;
		this.bridgeResolver = bridgeResolver;
	}

	@Override
	public <T> IdentifierBridge<T> createIdentifierBridge(Class<T> sourceType,
			BridgeBuilder<? extends IdentifierBridge<?>> builder) {
		BridgeBuilder<? extends IdentifierBridge<?>> defaultedBuilder = builder;
		if ( builder == null ) {
			defaultedBuilder = bridgeResolver.resolveIdentifierBridgeForType( sourceType );
		}
		/*
		 * TODO check that the bridge is suitable for the given sourceType
		 * (use introspection, similarly to what we do to detect the function bridges field type?)
		 */
		IdentifierBridge<?> bridge = defaultedBuilder.build( buildContext );

		return (IdentifierBridge<T>) bridge;
	}

	@Override
	public RoutingKeyBridge addRoutingKeyBridge(IndexModelBindingContext bindingContext,
			PojoModelElement pojoModelElement, BridgeBuilder<? extends RoutingKeyBridge> builder) {
		RoutingKeyBridge bridge = builder.build( buildContext );
		bridge.bind( pojoModelElement );

		bindingContext.explicitRouting();

		return bridge;
	}

	@Override
	public ValueProcessor addBridge(IndexModelBindingContext bindingContext,
			PojoModelElement pojoModelElement, BridgeBuilder<? extends Bridge> builder) {
		return doAddBridge( bindingContext, pojoModelElement, builder );
	}

	@Override
	public ValueProcessor addFunctionBridge(IndexModelBindingContext bindingContext,
			PojoModelElement pojoModelElement, Class<?> sourceType,
			BridgeBuilder<? extends FunctionBridge<?, ?>> builder,
			String fieldName, FieldModelContributor contributor) {
		BridgeBuilder<? extends FunctionBridge<?, ?>> defaultedBuilder = builder;
		if ( builder == null ) {
			defaultedBuilder = bridgeResolver.resolveFunctionBridgeForType( sourceType );
		}

		// TODO check that the bridge is suitable for the given sourceType?
		FunctionBridge<?, ?> bridge = defaultedBuilder.build( buildContext );

		return doAddFunctionBridge( bindingContext, pojoModelElement, bridge, fieldName, contributor );
	}

	private ValueProcessor doAddBridge(IndexModelBindingContext bindingContext,
			PojoModelElement pojoModelElement, BridgeBuilder<? extends Bridge> builder) {
		Bridge bridge = builder.build( buildContext );

		// FIXME if all fields are filtered out, we should ignore the processor
		bridge.contribute( bindingContext.getSchemaElement(), pojoModelElement, bindingContext.getSearchModel() );

		return new BridgeValueProcessor( bridge );
	}

	private <T, R> ValueProcessor doAddFunctionBridge(IndexModelBindingContext bindingContext,
			PojoModelElement pojoModelElement, FunctionBridge<T, R> bridge,
			String fieldName, FieldModelContributor contributor) {
		PojoModelElementAccessor<? extends T> pojoModelElementAccessor = getReferenceForBridge( pojoModelElement, bridge );
		return doAddFunctionBridge( bindingContext, pojoModelElementAccessor, bridge, fieldName, contributor );
	}

	@SuppressWarnings("unchecked")
	private <T> PojoModelElementAccessor<? extends T> getReferenceForBridge(PojoModelElement pojoModelElement,
			FunctionBridge<T, ?> bridge) {
		return FunctionBridgeUtil.inferParameterType( bridge )
				.map( c -> pojoModelElement.createAccessor( c ) )
				.orElse( (PojoModelElementAccessor<T>) pojoModelElement.createAccessor() );
	}

	private <T, R> ValueProcessor doAddFunctionBridge(IndexModelBindingContext bindingContext,
			PojoModelElementAccessor<? extends T> pojoModelElementAccessor,
			FunctionBridge<T, R> bridge, String fieldName, FieldModelContributor contributor) {
		FieldModelContext fieldContext = bindingContext.getSchemaElement().field( fieldName );

		// First give the bridge a chance to contribute to the model
		TypedFieldModelContext<R> typedFieldContext = bridge.bind( fieldContext );
		if ( typedFieldContext == null ) {
			Class<R> returnType = FunctionBridgeUtil.inferReturnType( bridge )
					.orElseThrow( () -> new SearchException( "Could not auto-detect the return type for bridge "
							+ bridge + "; configure encoding explicitly in the bridge." ) );
			typedFieldContext = fieldContext.as( returnType );
		}
		// Then give the mapping a chance to override some of the model (add storage, ...)
		contributor.contribute( typedFieldContext );

		// FIXME if the field is filtered out, we should ignore the processor

		IndexFieldAccessor<R> indexFieldAccessor = typedFieldContext.createAccessor();
		return new FunctionBridgeValueProcessor<>( bridge, pojoModelElementAccessor, indexFieldAccessor );
	}

}
