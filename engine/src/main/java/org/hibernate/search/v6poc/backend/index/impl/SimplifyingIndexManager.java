/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.index.impl;

import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.backend.index.spi.ChangesetIndexWorker;
import org.hibernate.search.v6poc.backend.index.spi.IndexManager;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTargetBuilder;
import org.hibernate.search.v6poc.backend.index.spi.StreamIndexWorker;
import org.hibernate.search.v6poc.engine.spi.SessionContext;


/**
 * @author Yoann Rodiere
 */
public class SimplifyingIndexManager<D extends DocumentState> implements IndexManager<D> {

	private final IndexManager<D> delegate;

	public SimplifyingIndexManager(IndexManager<D> delegate) {
		this.delegate = delegate;
	}

	@Override
	public ChangesetIndexWorker<D> createWorker(SessionContext context) {
		ChangesetIndexWorker<D> delegateWorker = delegate.createWorker( context );
		return new SimplifyingChangesetIndexWorker<>( delegateWorker );
	}

	@Override
	public StreamIndexWorker<D> createStreamWorker(SessionContext context) {
		return delegate.createStreamWorker( context );
	}

	@Override
	public IndexSearchTargetBuilder createSearchTarget() {
		return delegate.createSearchTarget();
	}

	@Override
	public void addToSearchTarget(IndexSearchTargetBuilder searchTargetBuilder) {
		delegate.addToSearchTarget( searchTargetBuilder );
	}
}
