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
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoState;
import org.hibernate.search.v6poc.entity.pojo.model.impl.PojoStateImpl;

/**
 * @author Yoann Rodiere
 */
public class PojoTypeNodeProcessor {

	private final Collection<ValueProcessor> typeScopedProcessors;
	private final Collection<PojoPropertyNodeProcessor> propertyScopedProcessors;

	public PojoTypeNodeProcessor(Collection<ValueProcessor> typeScopedProcessors,
			Collection<PojoPropertyNodeProcessorBuilder> propertyScopedProcessorBuilders) {
		this.typeScopedProcessors = typeScopedProcessors.isEmpty() ? Collections.emptyList() : new ArrayList<>( typeScopedProcessors );
		this.propertyScopedProcessors = propertyScopedProcessorBuilders.isEmpty() ?
				Collections.emptyList() : new ArrayList<>( propertyScopedProcessorBuilders.size() );
		propertyScopedProcessorBuilders.forEach( builder -> this.propertyScopedProcessors.add( builder.build() ) );
	}

	public final void process(Object source, DocumentState destination) {
		if ( source == null ) {
			return;
		}
		if ( !typeScopedProcessors.isEmpty() ) {
			PojoState bridgedElement = new PojoStateImpl( source );
			for ( ValueProcessor processor : typeScopedProcessors ) {
				processor.process( destination, bridgedElement );
			}
		}
		for ( PojoPropertyNodeProcessor processor : propertyScopedProcessors ) {
			// Recursion here
			processor.process( source, destination );
		}
	}

}
