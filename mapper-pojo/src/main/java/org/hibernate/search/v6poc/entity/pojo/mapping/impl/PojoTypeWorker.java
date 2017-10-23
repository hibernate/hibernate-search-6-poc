/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.backend.index.spi.IndexWorker;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoSessionContext;


/**
 * @author Yoann Rodiere
 */
class PojoTypeWorker<D extends DocumentState, C extends IndexWorker<D>> {

	private final PojoTypeManager<?, ?, D> typeManager;
	private final PojoSessionContext sessionContext;
	private final C delegate;

	public PojoTypeWorker(PojoTypeManager<?, ?, D> typeManager, PojoSessionContext sessionContext, C delegate) {
		this.typeManager = typeManager;
		this.sessionContext = sessionContext;
		this.delegate = delegate;
	}

	protected C getDelegate() {
		return delegate;
	}

	public void add(Object entity) {
		add( null, entity );
	}

	public void add(Object id, Object entity) {
		getDelegate().add(
				typeManager.toDocumentIdentifier( sessionContext, id, entity ),
				typeManager.toDocumentContributor( sessionContext, entity )
		);
	}

	public void update(Object entity) {
		update( null, entity );
	}

	public void update(Object id, Object entity) {
		getDelegate().update(
				typeManager.toDocumentIdentifier( sessionContext, id, entity ),
				typeManager.toDocumentContributor( sessionContext, entity )
		);
	}

	public void delete(Object id) {
		getDelegate().delete( typeManager.toDocumentIdentifier( sessionContext, id, null ) );
	}

}
