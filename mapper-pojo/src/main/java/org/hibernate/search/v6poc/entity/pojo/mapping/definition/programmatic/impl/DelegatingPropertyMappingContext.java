/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.impl;

import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeDefinition;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.MarkerDefinition;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyDocumentIdMappingContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyFieldMappingContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyIndexedEmbeddedMappingContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyMappingContext;


/**
 * @author Yoann Rodiere
 */
public class DelegatingPropertyMappingContext implements PropertyMappingContext {

	private final PropertyMappingContext delegate;

	protected DelegatingPropertyMappingContext(PropertyMappingContext delegate) {
		this.delegate = delegate;
	}

	@Override
	public PropertyDocumentIdMappingContext documentId() {
		return delegate.documentId();
	}

	@Override
	public PropertyMappingContext property(String propertyName) {
		return delegate.property( propertyName );
	}

	@Override
	public PropertyMappingContext bridge(BridgeDefinition<?> definition) {
		return delegate.bridge( definition );
	}

	@Override
	public PropertyMappingContext marker(MarkerDefinition<?> definition) {
		return delegate.marker( definition );
	}

	@Override
	public PropertyFieldMappingContext field() {
		return delegate.field();
	}

	@Override
	public PropertyIndexedEmbeddedMappingContext indexedEmbedded() {
		return delegate.indexedEmbedded();
	}

	@Override
	public PropertyMappingContext containedIn() {
		return delegate.containedIn();
	}

}
