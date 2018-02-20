/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.orchestration.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.lucene.index.IndexWriter;
import org.hibernate.search.v6poc.backend.lucene.work.impl.LuceneWork;
import org.hibernate.search.v6poc.util.spi.Futures;


/**
 * @author Yoann Rodiere
 * @author Guillaume Smet
 */
public class StubLuceneWorkOrchestrator implements LuceneWorkOrchestrator {

	private final StubLuceneWorkExecutionContext context;

	// Protected by synchronization on updates
	private CompletableFuture<?> latestFuture = CompletableFuture.completedFuture( null );

	public StubLuceneWorkOrchestrator(IndexWriter indexWriter) {
		this.context = new StubLuceneWorkExecutionContext( indexWriter );
	}

	@Override
	public void close() {
		latestFuture.join();
	}

	@Override
	public synchronized <T> CompletableFuture<T> submit(LuceneWork<T> work) {
		// Ignore errors in unrelated changesets
		latestFuture = latestFuture.exceptionally( ignore -> null );
		CompletableFuture<T> future = latestFuture.thenCompose( Futures.safeComposer(
				ignored -> work.execute( context )
		) );
		latestFuture = future;
		return future;
	}

	@Override
	public synchronized CompletableFuture<?> submit(List<LuceneWork<?>> works) {
		// Ignore errors in unrelated changesets
		latestFuture = latestFuture.exceptionally( ignore -> null );
		for ( LuceneWork<?> work : works ) {
			latestFuture = latestFuture.thenCompose( Futures.safeComposer(
					ignored -> work.execute( context )
			) );
		}
		return latestFuture;
	}
}
