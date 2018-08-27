/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.impl;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.hibernate.search.v6poc.entity.orm.logging.impl.Log;
import org.hibernate.search.v6poc.entity.pojo.mapping.ChangesetPojoWorker;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

/**
 * Execute final work in the after transaction synchronization.
 *
 * @author Emmanuel Bernard
 */
public class PostTransactionWorkQueueSynchronization implements Synchronization {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final ChangesetPojoWorker worker;
	private final Map<?, ?> workerPerTransaction;
	private final Object transactionIdentifier;

	PostTransactionWorkQueueSynchronization(ChangesetPojoWorker worker,
			Map<?, ?> workerPerTransaction, Object transactionIdentifier) {
		this.worker = worker;
		this.workerPerTransaction = workerPerTransaction;
		this.transactionIdentifier = transactionIdentifier;
	}

	@Override
	public void beforeCompletion() {
		log.tracef( "Processing Transaction's beforeCompletion() phase: %s", this );
		worker.prepare();
	}

	@Override
	public void afterCompletion(int i) {
		try {
			if ( Status.STATUS_COMMITTED == i ) {
				log.tracef( "Processing Transaction's afterCompletion() phase for %s. Performing work.", this );
				CompletableFuture<?> future = worker.execute();
				completionHandler.handle( future );
			}
			else {
				log.tracef(
						"Processing Transaction's afterCompletion() phase for %s. Cancelling work due to transaction status %d",
						this,
						i
				);
				// FIXME send some signal to the worker to release resources if necessary?
			}
		}
		finally {
			//clean the Synchronization per Transaction
			workerPerTransaction.remove( transactionIdentifier );
		}
	}
}
