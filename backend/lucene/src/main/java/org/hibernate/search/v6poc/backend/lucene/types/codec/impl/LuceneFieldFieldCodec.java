/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.codec.impl;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneDocumentBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.model.LuceneFieldContributor;
import org.hibernate.search.v6poc.backend.lucene.document.model.LuceneFieldValueExtractor;
import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.logging.spi.EventContexts;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

public final class LuceneFieldFieldCodec<F> implements LuceneFieldCodec<F> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private LuceneFieldContributor<F> fieldContributor;

	private LuceneFieldValueExtractor<F> fieldValueExtractor;

	public LuceneFieldFieldCodec(LuceneFieldContributor<F> fieldContributor, LuceneFieldValueExtractor<F> fieldValueExtractor) {
		this.fieldContributor = fieldContributor;
		this.fieldValueExtractor = fieldValueExtractor;
	}

	@Override
	public void encode(LuceneDocumentBuilder documentBuilder, String absoluteFieldPath, F value) {
		if ( value == null ) {
			return;
		}

		fieldContributor.contribute( absoluteFieldPath, value, f -> contributeField( documentBuilder, absoluteFieldPath, f ) );
	}

	@Override
	public F decode(Document document, String absoluteFieldPath) {
		if ( fieldValueExtractor == null ) {
			throw log.unsupportedProjection(
					EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath )
			);
		}

		IndexableField field = document.getField( absoluteFieldPath );

		if ( field == null ) {
			return null;
		}

		return fieldValueExtractor.extract( field );
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( LuceneFieldFieldCodec.class != obj.getClass() ) {
			return false;
		}

		LuceneFieldFieldCodec<?> other = (LuceneFieldFieldCodec<?>) obj;

		return Objects.equals( fieldContributor, other.fieldContributor )
				&& Objects.equals( fieldValueExtractor, other.fieldValueExtractor );
	}

	@Override
	public int hashCode() {
		return Objects.hash( fieldContributor, fieldValueExtractor );
	}

	private static void contributeField(LuceneDocumentBuilder documentBuilder, String absoluteFieldPath, IndexableField field) {
		if ( !absoluteFieldPath.equals( field.name() ) ) {
			throw log.invalidFieldPath( absoluteFieldPath, field.name() );
		}
		documentBuilder.addField( field );
	}
}
