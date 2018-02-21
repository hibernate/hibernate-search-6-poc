/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.index.spi;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.engine.spi.SessionContext;

/**
 * @author Yoann Rodiere
 */
public interface IndexManager<D extends DocumentElement> extends AutoCloseable {

	@Override
	void close();

	ChangesetIndexWorker<D> createWorker(SessionContext context);

	StreamIndexWorker<D> createStreamWorker(SessionContext context);

	IndexSearchTargetBuilder createSearchTarget();

	void addToSearchTarget(IndexSearchTargetBuilder searchTargetBuilder);
}
