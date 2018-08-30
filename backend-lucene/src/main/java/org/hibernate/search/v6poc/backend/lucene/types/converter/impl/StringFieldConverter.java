/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.converter.impl;

import java.util.Objects;

import org.apache.lucene.analysis.Analyzer;
import org.hibernate.search.v6poc.backend.lucene.util.impl.AnalyzerUtils;

public final class StringFieldConverter implements LuceneFieldConverter<String> {

	private final Analyzer analyzerOrNormalizer;

	public StringFieldConverter(Analyzer analyzerOrNormalizer) {
		this.analyzerOrNormalizer = analyzerOrNormalizer;
	}

	@Override
	public String convertFromDsl(Object value) {
		return (String) value;
	}

	public String normalize(String absoluteFieldPath, String value) {
		if ( value == null ) {
			return null;
		}

		if ( analyzerOrNormalizer == null ) {
			return value;
		}

		return AnalyzerUtils.normalize( analyzerOrNormalizer, absoluteFieldPath, value );
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( StringFieldConverter.class != obj.getClass() ) {
			return false;
		}

		StringFieldConverter other = (StringFieldConverter) obj;

		return Objects.equals( analyzerOrNormalizer, other.analyzerOrNormalizer );
	}

	@Override
	public int hashCode() {
		return Objects.hash( analyzerOrNormalizer );
	}
}
