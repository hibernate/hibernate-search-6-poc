/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import java.util.function.Supplier;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.index.spi.DocumentContributor;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.PojoIndexingProcessor;

/**
 * @param <E> The entity type mapped to the index.
 * @param <D> The document type for the index.
 */
class PojoDocumentContributor<D extends DocumentElement, E> implements DocumentContributor<D> {

	private final PojoIndexingProcessor<E> processor;

	private final Supplier<E> entitySupplier;

	PojoDocumentContributor(PojoIndexingProcessor<E> processor, Supplier<E> entitySupplier) {
		this.processor = processor;
		this.entitySupplier = entitySupplier;
	}

	@Override
	public void contribute(D state) {
		processor.process( state, entitySupplier.get() );
	}
}
