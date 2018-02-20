/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.work.impl;

import java.util.Set;

import org.apache.lucene.search.Query;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexEntry;
import org.hibernate.search.v6poc.search.SearchResult;


/**
 * @author Guillaume Smet
 */
public class StubLuceneWorkFactory implements LuceneWorkFactory {

	@Override
	public LuceneWork<?> add(String indexName, String id, String routingKey, LuceneIndexEntry indexEntry) {
		return new AddEntryLuceneWork( indexName, id, indexEntry );
	}

	@Override
	public LuceneWork<?> update(String indexName, String id, String routingKey, LuceneIndexEntry indexEntry) {
		return new UpdateEntryLuceneWork( indexName, id, indexEntry );
	}

	@Override
	public LuceneWork<?> delete(String indexName, String id, String routingKey) {
		return new DeleteEntryLuceneWork( indexName, id );
	}

	@Override
	public LuceneWork<?> flush(String indexName) {
		return new FlushIndexLuceneWork( indexName );
	}

	@Override
	public LuceneWork<?> commit(String indexName) {
		return new FlushIndexLuceneWork( indexName );
	}

	@Override
	public LuceneWork<?> optimize(String indexName) {
		return new OptimizeIndexLuceneWork( indexName );
	}

	@Override
	public <T> LuceneWork<SearchResult<T>> search(Set<String> indexNames, Query query, Long offset, Long limit) {
//		return new StubLuceneWork<SearchResult<T>>( "query", query )
//				.addParameter( "indexName", indexNames )
//				.addParameter( "offset", offset, String::valueOf )
//				.addParameter( "limit", limit, String::valueOf );
		return null;
	}

}
