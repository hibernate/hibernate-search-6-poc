/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import java.util.Collection;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.entity.pojo.bridge.PropertyBridge;
import org.hibernate.search.v6poc.entity.pojo.model.PojoElement;
import org.hibernate.search.v6poc.entity.pojo.model.impl.PojoElementImpl;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.util.impl.common.Closer;
import org.hibernate.search.v6poc.util.impl.common.ToStringTreeBuilder;

/**
 * A node inside a {@link PojoIndexingProcessor} responsible for extracting the value of a property,
 * and applying nested processor nodes as well as {@link PropertyBridge}s to this value.
 *
 * @param <T> The property holder type
 * @param <P> The property type
 */
public class PojoIndexingProcessorPropertyNode<T, P> extends PojoIndexingProcessor<T> {

	private final PropertyHandle handle;
	private final Collection<PropertyBridge> propertyBridges;
	private final Collection<PojoIndexingProcessor<? super P>> nestedNodes;

	public PojoIndexingProcessorPropertyNode(PropertyHandle handle,
			Collection<PropertyBridge> propertyBridges,
			Collection<PojoIndexingProcessor<? super P>> nestedNodes) {
		this.handle = handle;
		this.propertyBridges = propertyBridges;
		this.nestedNodes = nestedNodes;
	}

	@Override
	public void close() {
		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.pushAll( PropertyBridge::close, propertyBridges );
			closer.pushAll( PojoIndexingProcessor::close, nestedNodes );
		}
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "class", getClass().getSimpleName() );
		builder.attribute( "handle", handle );
		builder.startList( "bridges" );
		for ( PropertyBridge bridge : propertyBridges ) {
			builder.value( bridge );
		}
		builder.endList();
		builder.startList( "nestedNodes" );
		for ( PojoIndexingProcessor<?> nestedNode : nestedNodes ) {
			builder.value( nestedNode );
		}
		builder.endList();
	}

	@Override
	public final void process(DocumentElement target, T source) {
		// TODO add generic type parameters to property handles
		P propertyValue = (P) handle.get( source );
		if ( !propertyBridges.isEmpty() ) {
			PojoElement bridgedElement = new PojoElementImpl( propertyValue );
			for ( PropertyBridge bridge : propertyBridges ) {
				bridge.write( target, bridgedElement );
			}
		}
		for ( PojoIndexingProcessor<? super P> nestedNode : nestedNodes ) {
			nestedNode.process( target, propertyValue );
		}
	}
}
