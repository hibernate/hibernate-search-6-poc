/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.search.v6poc.engine.SearchManager;


/**
 * @author Yoann Rodiere
 */
public interface PojoSearchManager extends SearchManager {

	default PojoSearchTarget<?> search() {
		return search( Collections.singleton( Object.class ) );
	}

	default <T> PojoSearchTarget<?> search(Class<T> targetedType) {
		return search( Collections.singleton( targetedType ) );
	}

	<T> PojoSearchTarget<?> search(Collection<? extends Class<? extends T>> targetedTypes);

	/**
	 * @return The main worker for this manager. Calling {@link ChangesetPojoWorker#execute()}
	 * is optional, as it will be executed upon closing this manager.
	 */
	ChangesetPojoWorker getMainWorker();

	/**
	 * @return A new worker for this manager, maintaining its changeset state independently from the manager.
	 * Calling {@link ChangesetPojoWorker#execute()} is required to actually execute works,
	 * the manager will <strong>not</strong> do it automatically upon closing.
	 */
	ChangesetPojoWorker createWorker();

	/**
	 * @return A stream worker for this manager.
	 */
	StreamPojoWorker getStreamWorker();

}
