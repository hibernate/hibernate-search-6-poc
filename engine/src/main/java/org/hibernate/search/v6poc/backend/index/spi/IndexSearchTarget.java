/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.index.spi;

import java.util.function.Function;

import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.search.ObjectLoader;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.dsl.predicate.SearchPredicateContainerContext;
import org.hibernate.search.v6poc.search.spi.SearchQueryResultDefinitionContext;

public interface IndexSearchTarget {

	default SearchQueryResultDefinitionContext<DocumentReference, DocumentReference> query(SessionContext context) {
		return query( context, Function.identity(), ObjectLoader.identity() );
	}

	<R, O> SearchQueryResultDefinitionContext<R, O> query(SessionContext context,
			Function<DocumentReference, R> documentReferenceTransformer,
			ObjectLoader<R, O> objectLoader);

	SearchPredicateContainerContext<SearchPredicate> predicate();

}
