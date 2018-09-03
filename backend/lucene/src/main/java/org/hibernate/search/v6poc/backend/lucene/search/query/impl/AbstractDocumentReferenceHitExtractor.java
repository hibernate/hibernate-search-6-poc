/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.query.impl;

import java.util.Set;

import org.apache.lucene.document.Document;
import org.hibernate.search.v6poc.backend.lucene.util.impl.LuceneFields;
import org.hibernate.search.v6poc.backend.lucene.search.impl.LuceneDocumentReference;
import org.hibernate.search.v6poc.search.DocumentReference;

abstract class AbstractDocumentReferenceHitExtractor<T> implements HitExtractor<T> {

	protected AbstractDocumentReferenceHitExtractor() {
	}

	@Override
	public void contributeCollectors(LuceneCollectorsBuilder luceneCollectorBuilder) {
		luceneCollectorBuilder.requireTopDocsCollector();
	}

	@Override
	public void contributeFields(Set<String> absoluteFieldPaths) {
		absoluteFieldPaths.add( LuceneFields.indexFieldName() );
		absoluteFieldPaths.add( LuceneFields.idFieldName() );
	}

	protected DocumentReference extractDocumentReference(Document document) {
		DocumentReference documentReference = new LuceneDocumentReference(
				document.get( LuceneFields.indexFieldName() ),
				document.get( LuceneFields.idFieldName() )
		);
		return documentReference;
	}
}
