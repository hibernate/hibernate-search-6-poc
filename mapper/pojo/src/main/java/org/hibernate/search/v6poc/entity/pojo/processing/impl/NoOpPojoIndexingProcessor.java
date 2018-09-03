/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.util.impl.common.ToStringTreeBuilder;

class NoOpPojoIndexingProcessor extends PojoIndexingProcessor<Object> {

	private static NoOpPojoIndexingProcessor INSTANCE = new NoOpPojoIndexingProcessor();

	@SuppressWarnings( "unchecked" ) // This instance works for any T
	public static <T> PojoIndexingProcessor<T> get() {
		return (PojoIndexingProcessor<T>) INSTANCE;
	}

	@Override
	public void process(DocumentElement target, Object source) {
		// No-op
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "class", getClass().getSimpleName() );
	}
}
