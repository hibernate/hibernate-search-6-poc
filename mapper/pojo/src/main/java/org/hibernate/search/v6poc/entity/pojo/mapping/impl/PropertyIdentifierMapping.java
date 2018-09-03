/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import java.util.function.Supplier;

import org.hibernate.search.v6poc.entity.pojo.bridge.IdentifierBridge;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoCaster;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.util.SearchException;

/**
 * @author Yoann Rodiere
 */
public class PropertyIdentifierMapping<I, E> implements IdentifierMapping<I, E> {

	private final PojoCaster<? super I> caster;
	private final PropertyHandle property;
	private final IdentifierBridge<I> bridge;

	@SuppressWarnings("unchecked")
	public PropertyIdentifierMapping(PojoCaster<? super I> caster, PropertyHandle property, IdentifierBridge<I> bridge) {
		this.caster = caster;
		this.property = property;
		this.bridge = bridge;
	}

	@Override
	public void close() {
		bridge.close();
	}

	@Override
	@SuppressWarnings( "unchecked" ) // We can only cast to the raw type, if I is generic we need an unchecked cast
	public I getIdentifier(Object providedId, Supplier<? extends E> entitySupplier) {
		if ( providedId != null ) {
			return (I) caster.cast( providedId );
		}
		else if ( property != null ) {
			Object id = property.get( entitySupplier.get() );
			// TODO avoid this cast? By construction, the property handle should always return type I
			return (I) caster.cast( id );
		}
		else {
			throw new SearchException( "No identifier was provided, and this mapping does not define"
					+ " how to extract the identifier from the entity" );
		}
	}

	@Override
	public String toDocumentIdentifier(I identifier) {
		return bridge.toDocumentIdentifier( identifier );
	}

	@Override
	public I fromDocumentIdentifier(String documentId) {
		return bridge.fromDocumentIdentifier( documentId );
	}

}
