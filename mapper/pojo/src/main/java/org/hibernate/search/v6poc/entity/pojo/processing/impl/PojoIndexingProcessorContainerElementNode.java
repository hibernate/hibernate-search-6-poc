/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import java.util.Collection;
import java.util.stream.Stream;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.entity.pojo.extractor.ContainerValueExtractor;
import org.hibernate.search.v6poc.util.impl.common.Closer;
import org.hibernate.search.v6poc.util.impl.common.ToStringTreeBuilder;

/**
 * A node inside a {@link PojoIndexingProcessor} responsible for extracting elements from a container
 * and applying nested processor nodes to the elements.
 *
 * @param <C> The container type
 * @param <V> The extracted value type
 */
public class PojoIndexingProcessorContainerElementNode<C, V> extends PojoIndexingProcessor<C> {

	private final ContainerValueExtractor<C, V> extractor;
	private final Collection<PojoIndexingProcessor<? super V>> nestedNodes;

	public PojoIndexingProcessorContainerElementNode(ContainerValueExtractor<C, V> extractor,
			Collection<PojoIndexingProcessor<? super V>> nestedNodes) {
		this.extractor = extractor;
		this.nestedNodes = nestedNodes;
	}

	@Override
	public void close() {
		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.pushAll( PojoIndexingProcessor::close, nestedNodes );
		}
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "class", getClass().getSimpleName() );
		builder.attribute( "extractor", extractor );
		builder.startList( "nestedNodes" );
		for ( PojoIndexingProcessor<?> nestedNode : nestedNodes ) {
			builder.value( nestedNode );
		}
		builder.endList();
	}

	@Override
	public final void process(DocumentElement target, C source) {
		try ( Stream<V> stream = extractor.extract( source ) ) {
			stream.forEach( sourceItem -> processItem( target, sourceItem ) );
		}
	}

	private void processItem(DocumentElement target, V sourceItem) {
		for ( PojoIndexingProcessor<? super V> nestedNode : nestedNodes ) {
			nestedNode.process( target, sourceItem );
		}
	}

}
