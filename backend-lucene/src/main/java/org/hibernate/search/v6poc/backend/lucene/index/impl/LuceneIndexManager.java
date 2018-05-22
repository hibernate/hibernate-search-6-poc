/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.index.impl;

import org.hibernate.search.v6poc.backend.index.spi.IndexManagerImplementor;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneRootDocumentBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexModel;
import org.hibernate.search.v6poc.backend.lucene.index.spi.ReaderProvider;

/**
 * @author Guillaume Smet
 */
public interface LuceneIndexManager extends IndexManagerImplementor<LuceneRootDocumentBuilder> {

	String getName();

	LuceneIndexModel getModel();

	ReaderProvider getReaderProvider();
}
