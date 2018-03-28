/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.search.query.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.hibernate.search.v6poc.search.query.spi.ProjectionHitCollector;

class ProjectionHitExtractor implements HitExtractor<ProjectionHitCollector> {

	private final List<ProjectionDocumentExtractor> documentExtractors;

	private final ReusableDocumentStoredFieldVisitor storedFieldVisitor;

	ProjectionHitExtractor(List<ProjectionDocumentExtractor> documentExtractors) {
		this.documentExtractors = documentExtractors;

		Set<String> absoluteFieldPaths = new HashSet<>();
		for ( ProjectionDocumentExtractor documentExtractor : documentExtractors ) {
			documentExtractor.contributeFields( absoluteFieldPaths );
		}

		storedFieldVisitor = new ReusableDocumentStoredFieldVisitor( absoluteFieldPaths );
	}

	@Override
	public void contributeCollectors(LuceneCollectorsBuilder luceneCollectorBuilder) {
		for ( ProjectionDocumentExtractor documentExtractor : documentExtractors ) {
			documentExtractor.contributeCollectors( luceneCollectorBuilder );
		}
	}

	@Override
	public void extract(ProjectionHitCollector collector, IndexSearcher indexSearcher, ScoreDoc scoreDoc) throws IOException {
		indexSearcher.doc( scoreDoc.doc, storedFieldVisitor );
		Document document = storedFieldVisitor.getDocumentAndReset();

		for ( ProjectionDocumentExtractor documentExtractor : documentExtractors ) {
			documentExtractor.extract( collector, scoreDoc, document );
		}
	}
}
