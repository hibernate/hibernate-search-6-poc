/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.model.spi;

import org.hibernate.search.v6poc.backend.document.model.Store;
import org.hibernate.search.v6poc.backend.document.spi.IndexFieldReference;

/**
 * @author Yoann Rodiere
 */
public interface TypedFieldModelContext<T> {

	// TODO add common options: stored, sortable, ...

	TypedFieldModelContext<T> store(Store store);

	IndexFieldReference<T> asReference();

}
