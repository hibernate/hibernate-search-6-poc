/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.entity.model.spi.Indexable;
import org.hibernate.search.v6poc.entity.pojo.model.impl.PojoIndexable;
import org.hibernate.search.v6poc.entity.pojo.model.spi.ReadableProperty;
import org.hibernate.search.v6poc.entity.processing.spi.ValueProcessor;

/**
 * @author Yoann Rodiere
 */
public class PojoPropertyNodeProcessor {

	private final ReadableProperty property;
	private final Collection<ValueProcessor> processors;
	private final Collection<PojoTypeNodeProcessor> indexedEmbeddedProcessors;

	public PojoPropertyNodeProcessor(ReadableProperty property,
			Collection<ValueProcessor> processors,
			Collection<PojoTypeNodeProcessor> indexedEmbeddedProcessors) {
		this.property = property;
		this.processors = processors.isEmpty() ? Collections.emptyList() : new ArrayList<>( processors );
		this.indexedEmbeddedProcessors = indexedEmbeddedProcessors.isEmpty() ? Collections.emptyList() : new ArrayList<>( indexedEmbeddedProcessors );
	}

	public final void process(Object source, DocumentState destination) {
		Object nestedValue = property.invoke( source );
		if ( !processors.isEmpty() ) {
			Indexable indexable = new PojoIndexable( nestedValue );
			for ( ValueProcessor processor : processors ) {
				processor.process( indexable, destination );
			}
		}
		for ( PojoTypeNodeProcessor processor : indexedEmbeddedProcessors ) {
			processor.process( nestedValue, destination );
		}
	}

}
