/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.model.impl;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.Locale;
import java.util.Objects;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.hibernate.search.v6poc.backend.document.impl.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.Sortable;
import org.hibernate.search.v6poc.backend.document.model.Store;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneDocumentBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexFieldAccessor;

/**
 * @author Guillaume Smet
 */
class IndexSchemaFieldLocalDateContext extends AbstractLuceneIndexSchemaFieldTypedContext<LocalDate> {

	private Sortable sortable;

	public IndexSchemaFieldLocalDateContext(String fieldName) {
		super( fieldName );
	}

	@Override
	public IndexSchemaFieldLocalDateContext sortable(Sortable sortable) {
		this.sortable = sortable;
		return this;
	}

	@Override
	protected void contribute(DeferredInitializationIndexFieldAccessor<LocalDate> accessor, LuceneIndexSchemaNodeCollector collector,
			LuceneIndexSchemaObjectNode parentNode) {
		LocalDateFieldFormatter localDateFieldFormatter = new LocalDateFieldFormatter( getStore(), sortable );

		LuceneIndexSchemaFieldNode<LocalDate> schemaNode = new LuceneIndexSchemaFieldNode<>(
				parentNode,
				getFieldName(),
				localDateFieldFormatter,
				new LocalDateFieldQueryBuilder( localDateFieldFormatter )
		);

		accessor.initialize( new LuceneIndexFieldAccessor<>( schemaNode ) );

		collector.collectFieldNode( schemaNode.getAbsoluteFieldPath(), schemaNode );
	}

	private static final class LocalDateFieldFormatter implements LuceneFieldFormatter<LocalDate> {

		private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
				.appendValue( YEAR, 4, 9, SignStyle.EXCEEDS_PAD )
				.appendLiteral( '-' )
				.appendValue( MONTH_OF_YEAR, 2 )
				.appendLiteral( '-' )
				.appendValue( DAY_OF_MONTH, 2 )
				.toFormatter( Locale.ROOT )
				.withResolverStyle( ResolverStyle.STRICT );

		private final Store store;

		private final Sortable sortable;

		private final int hashCode;

		private LocalDateFieldFormatter(Store store, Sortable sortable) {
			this.store = store;
			this.sortable = sortable;

			this.hashCode = buildHashCode();
		}

		@Override
		public void addFields(LuceneDocumentBuilder documentBuilder, LuceneIndexSchemaObjectNode parentNode, String fieldName, LocalDate value) {
			if ( value == null ) {
				return;
			}

			if ( Store.YES.equals( store ) ) {
				documentBuilder.addField( parentNode, new StoredField( fieldName, FORMATTER.format( value ) ) );
			}

			long valueToEpochDay = value.toEpochDay();

			if ( Sortable.YES.equals( sortable ) ) {
				documentBuilder.addField( parentNode, new NumericDocValuesField( fieldName, valueToEpochDay ) );
			}

			documentBuilder.addField( parentNode, new LongPoint( fieldName, valueToEpochDay ) );
		}

		@Override
		public Object format(Object value) {
			return ((LocalDate) value).toEpochDay();
		}

		@Override
		public LocalDate parse(Document document, String fieldName) {
			IndexableField field = document.getField( fieldName );

			if ( field == null ) {
				return null;
			}

			String value = field.stringValue();

			if ( value == null ) {
				return null;
			}

			return LocalDate.parse( value, FORMATTER );
		}

		@Override
		public Type getDefaultSortFieldType() {
			return SortField.Type.LONG;
		}

		@Override
		public Object getSortMissingFirst() {
			return Long.MIN_VALUE;
		}

		@Override
		public Object getSortMissingLast() {
			return Long.MAX_VALUE;
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( LocalDateFieldFormatter.class != obj.getClass() ) {
				return false;
			}

			LocalDateFieldFormatter other = (LocalDateFieldFormatter) obj;

			return Objects.equals( store, other.store );
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		private int buildHashCode() {
			return Objects.hashCode( store );
		}
	}

	private static final class LocalDateFieldQueryBuilder implements LuceneFieldQueryFactory {

		private final LocalDateFieldFormatter localDateFieldFormatter;

		private LocalDateFieldQueryBuilder(LocalDateFieldFormatter localDateFieldFormatter) {
			this.localDateFieldFormatter = localDateFieldFormatter;
		}

		@Override
		public Query createMatchQuery(String fieldName, Object value, MatchQueryOptions matchQueryOptions) {
			return LongPoint.newExactQuery( fieldName, ((LocalDate) value).toEpochDay() );
		}

		@Override
		public Query createRangeQuery(String fieldName, Object lowerLimit, Object upperLimit, RangeQueryOptions rangeQueryOptions) {
			return LongPoint.newRangeQuery(
					fieldName,
					getLowerValue( lowerLimit, rangeQueryOptions.isExcludeLowerLimit() ),
					getUpperValue( upperLimit, rangeQueryOptions.isExcludeUpperLimit() )
			);
		}

		private long getLowerValue(Object lowerLimit, boolean excludeLowerLimit) {
			if ( lowerLimit == null ) {
				return excludeLowerLimit ? Math.addExact( Long.MIN_VALUE, 1 ) : Long.MIN_VALUE;
			}
			else {
				long lowerLimitAsLong = (long) localDateFieldFormatter.format( lowerLimit );
				return excludeLowerLimit ? Math.addExact( lowerLimitAsLong, 1 ) : lowerLimitAsLong;
			}
		}

		private long getUpperValue(Object upperLimit, boolean excludeUpperLimit) {
			if ( upperLimit == null ) {
				return excludeUpperLimit ? Math.addExact( Long.MAX_VALUE, -1 ) : Long.MAX_VALUE;
			}
			else {
				long upperLimitAsLong = (long) localDateFieldFormatter.format( upperLimit );
				return excludeUpperLimit ? Math.addExact( upperLimitAsLong, -1 ) : upperLimitAsLong;
			}
		}
	}
}
