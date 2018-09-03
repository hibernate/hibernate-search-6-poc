/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import java.util.function.Supplier;

import org.hibernate.search.v6poc.backend.index.spi.DocumentReferenceProvider;

/**
 * @param <E> The entity type mapped to an index.
 */
class PojoDocumentReferenceProvider<E> implements DocumentReferenceProvider {

	private final RoutingKeyProvider<E> routingKeyProvider;

	private final String tenantIdentifier;
	private final Object identifier;
	private final String documentIdentifier;
	private final Supplier<E> entitySupplier;

	PojoDocumentReferenceProvider(
			RoutingKeyProvider<E> routingKeyProvider,
			String tenantIdentifier,
			Object identifier,
			String documentIdentifier,
			Supplier<E> entitySupplier) {
		this.routingKeyProvider = routingKeyProvider;
		this.tenantIdentifier = tenantIdentifier;
		this.identifier = identifier;
		this.documentIdentifier = documentIdentifier;
		this.entitySupplier = entitySupplier;
	}

	@Override
	public String getIdentifier() {
		return documentIdentifier;
	}

	@Override
	public String getRoutingKey() {
		return routingKeyProvider.toRoutingKey( tenantIdentifier, identifier, entitySupplier );
	}

}
