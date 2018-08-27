/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search;

import java.util.concurrent.CompletableFuture;

/**
 * @author Yoann Rodiere
 */
public interface SearchQuery<T> {

	void setFirstResult(Long firstResultIndex);

	void setMaxResults(Long maxResultsCount);

	String getQueryString();

	SearchResult<T> execute();

	CompletableFuture<SearchResult<T>> executeAsync();

}
