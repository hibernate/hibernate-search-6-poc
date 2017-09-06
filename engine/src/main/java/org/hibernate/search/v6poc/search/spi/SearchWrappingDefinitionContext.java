/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.spi;

import java.util.function.Function;

import org.hibernate.search.v6poc.search.dsl.SearchContext;

/**
 * @author Yoann Rodiere
 */
public interface SearchWrappingDefinitionContext<Q> extends SearchContext<Q> {

	default SearchContext<Q> asSearchQuery() {
		return asWrappedQuery( Function.identity() );
	}

	<R> SearchContext<R> asWrappedQuery(Function<Q, R> wrapperFactory);

}
