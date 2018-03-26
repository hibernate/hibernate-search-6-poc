/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.util.common.stub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.search.v6poc.util.spi.ToStringTreeAppendable;
import org.hibernate.search.v6poc.util.spi.ToStringTreeBuilder;

public abstract class StubTreeNode<N extends StubTreeNode<N>> implements ToStringTreeAppendable {

	private final Map<String, List<Object>> attributes;
	private final Map<String, List<N>> children;

	protected StubTreeNode(Builder<N> builder) {
		this.attributes = Collections.unmodifiableMap(
				builder.attributes.entrySet().stream()
						.collect( Collectors.toMap(
								Map.Entry::getKey,
								e -> Collections.unmodifiableList( new ArrayList<>( e.getValue() ) ),
								(u, v) -> {
									throw new IllegalStateException( String.format( "Duplicate key %s", u ) );
								},
								LinkedHashMap::new
						) )
		);
		this.children = Collections.unmodifiableMap(
				builder.children.entrySet().stream()
						.collect( Collectors.toMap(
								Map.Entry::getKey,
								e -> Collections.unmodifiableList(
										e.getValue().stream()
												.map( b -> b == null ? null : b.build() )
												.collect( Collectors.toList() )
								),
								(u, v) -> {
									throw new IllegalStateException( String.format( "Duplicate key %s", u ) );
								},
								LinkedHashMap::new
						) )
		);
	}

	@Override
	public String toString() {
		return new ToStringTreeBuilder().value( this ).toString();
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		for ( Map.Entry<String, List<Object>> entry : attributes.entrySet() ) {
			builder.attribute( entry.getKey(), entry.getValue() );
		}
		for ( Map.Entry<String, List<N>> entry : children.entrySet() ) {
			List<N> list = entry.getValue();
			if ( list.size() == 1 ) {
				builder.startObject( entry.getKey() );
				list.get( 0 ).appendTo( builder );
				builder.endObject();
			}
			else {
				builder.startList( entry.getKey() );
				for ( N child : entry.getValue() ) {
					builder.startObject();
					child.appendTo( builder );
					builder.endObject();
				}
				builder.endList();
			}
		}
	}

	public Map<String, List<Object>> getAttributes() {
		return attributes;
	}

	public Map<String, List<N>> getChildren() {
		return children;
	}

	public abstract static class Builder<N> {

		private final Builder<?> parent;
		private final String relativeName;
		private final String absolutePath;

		private final Map<String, List<Object>> attributes = new LinkedHashMap<>();
		private final Map<String, List<Builder<N>>> children = new LinkedHashMap<>();

		protected Builder(Builder<?> parent, String relativeName) {
			this.parent = parent;
			this.relativeName = relativeName;
			if ( parent == null ) {
				this.absolutePath = "<ROOT>";
			}
			else if ( parent.parent == null ) {
				this.absolutePath = relativeName;
			}
			else {
				this.absolutePath = parent.getAbsolutePath() + "." + relativeName;
			}
		}

		public String getRelativeName() {
			return relativeName;
		}

		public String getAbsolutePath() {
			return absolutePath;
		}

		protected void attribute(String name, Object... values) {
			List<Object> attributeValues = attributes.computeIfAbsent( name, ignored -> new ArrayList<>() );
			Collections.addAll( attributeValues, values );
		}

		protected void missingChild(String relativeName) {
			children.computeIfAbsent( relativeName, ignored -> new ArrayList<>() )
					.add( null );
		}

		protected void child(Builder<N> nodeBuilder) {
			children.computeIfAbsent( nodeBuilder.relativeName, ignored -> new ArrayList<>() )
					.add( nodeBuilder );
		}

		public abstract N build();
	}

}
