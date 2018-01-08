/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.spi;

import java.util.function.Function;

import org.hibernate.search.v6poc.search.dsl.query.SearchQueryResultContext;

/**
 * @author Yoann Rodiere
 */
public interface SearchQueryWrappingDefinitionResultContext<Q> extends SearchQueryResultContext<Q> {

	<R> SearchQueryResultContext<R> asWrappedQuery(Function<Q, R> wrapperFactory);

}
