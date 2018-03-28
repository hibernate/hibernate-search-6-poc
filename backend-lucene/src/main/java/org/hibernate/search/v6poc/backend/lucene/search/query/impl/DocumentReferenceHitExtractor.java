/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.query.impl;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.hibernate.search.v6poc.search.query.spi.DocumentReferenceHitCollector;

class DocumentReferenceHitExtractor extends AbstractDocumentReferenceHitExtractor<DocumentReferenceHitCollector> {

	private static final DocumentReferenceHitExtractor INSTANCE = new DocumentReferenceHitExtractor();

	public static DocumentReferenceHitExtractor get() {
		return INSTANCE;
	}

	private DocumentReferenceHitExtractor() {
	}

	@Override
	public void extract(DocumentReferenceHitCollector collector, IndexSearcher indexSearcher, ScoreDoc scoreDoc) throws IOException {
		collector.collectReference( extractDocumentReference( indexSearcher, scoreDoc.doc ) );
	}
}
