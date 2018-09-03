/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import java.util.Collection;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.entity.pojo.bridge.TypeBridge;
import org.hibernate.search.v6poc.entity.pojo.model.PojoElement;
import org.hibernate.search.v6poc.entity.pojo.model.impl.PojoElementImpl;
import org.hibernate.search.v6poc.util.impl.common.Closer;
import org.hibernate.search.v6poc.util.impl.common.ToStringTreeBuilder;

/**
 * A node inside a {@link PojoIndexingProcessor} responsible for applying processor property nodes
 * as well as {@link TypeBridge}s to a value.
 *
 * @param <T> The processed type
 */
public class PojoIndexingProcessorTypeNode<T> extends PojoIndexingProcessor<T> {

	private final Iterable<IndexObjectFieldAccessor> parentObjectAccessors;
	private final Collection<TypeBridge> bridges;
	private final Collection<PojoIndexingProcessorPropertyNode<? super T, ?>> propertyNodes;

	public PojoIndexingProcessorTypeNode(Iterable<IndexObjectFieldAccessor> parentObjectAccessors,
			Collection<TypeBridge> bridges,
			Collection<PojoIndexingProcessorPropertyNode<? super T, ?>> propertyNodes) {
		this.parentObjectAccessors = parentObjectAccessors;
		this.bridges = bridges;
		this.propertyNodes = propertyNodes;
	}

	@Override
	public void close() {
		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.pushAll( TypeBridge::close, bridges );
			closer.pushAll( PojoIndexingProcessor::close, propertyNodes );
		}
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "class", getClass().getSimpleName() );
		builder.attribute( "objectAccessors", parentObjectAccessors );
		builder.startList( "bridges" );
		for ( TypeBridge bridge : bridges ) {
			builder.value( bridge );
		}
		builder.endList();
		builder.startList( "propertyNodes" );
		for ( PojoIndexingProcessorPropertyNode<? super T, ?> propertyNode : propertyNodes ) {
			builder.value( propertyNode );
		}
		builder.endList();
	}

	@Override
	public final void process(DocumentElement target, T source) {
		if ( source == null ) {
			return;
		}
		DocumentElement parentObject = target;
		for ( IndexObjectFieldAccessor objectAccessor : parentObjectAccessors ) {
			parentObject = objectAccessor.add( parentObject );
		}
		if ( !bridges.isEmpty() ) {
			PojoElement bridgedElement = new PojoElementImpl( source );
			for ( TypeBridge bridge : bridges ) {
				bridge.write( parentObject, bridgedElement );
			}
		}
		for ( PojoIndexingProcessorPropertyNode<? super T, ?> propertyNode : propertyNodes ) {
			// Recursion here
			propertyNode.process( parentObject, source );
		}
	}

}
