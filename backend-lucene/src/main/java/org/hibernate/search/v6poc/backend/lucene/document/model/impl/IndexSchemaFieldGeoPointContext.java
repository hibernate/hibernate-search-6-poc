/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.document.model.impl;

import static org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneFields.internalFieldName;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField.Type;
import org.hibernate.search.v6poc.backend.document.impl.DeferredInitializationIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.Store;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneDocumentBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneIndexFieldAccessor;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.backend.spatial.ImmutableGeoPoint;
import org.hibernate.search.v6poc.util.impl.CollectionHelper;

/**
 * @author Guillaume Smet
 */
class IndexSchemaFieldGeoPointContext extends AbstractLuceneIndexSchemaFieldTypedContext<GeoPoint> {

	public IndexSchemaFieldGeoPointContext(String fieldName) {
		super( fieldName );
	}

	@Override
	protected void contribute(DeferredInitializationIndexFieldAccessor<GeoPoint> accessor, LuceneIndexSchemaNodeCollector collector,
			LuceneIndexSchemaObjectNode parentNode) {
		LuceneIndexSchemaFieldNode<GeoPoint> schemaNode = new LuceneIndexSchemaFieldNode<>(
				parentNode,
				getFieldName(),
				new LuceneGeoPointFieldFormatter( parentNode.getAbsolutePath( getFieldName() ), getStore() ),
				null // for now we don't have a query builder for GeoPoint
		);

		accessor.initialize( new LuceneIndexFieldAccessor<>( schemaNode ) );

		collector.collectFieldNode( schemaNode.getAbsoluteFieldPath(), schemaNode );
	}

	private static final class LuceneGeoPointFieldFormatter implements LuceneFieldFormatter<GeoPoint> {

		private static final String LATITUDE = "latitude";
		private static final String LONGITUDE = "longitude";

		private final Store store;

		private final String latitudeFieldName;
		private final String longitudeFieldName;

		private final int hashCode;

		private final Set<String> storedFields;

		private LuceneGeoPointFieldFormatter(String fieldName, Store store) {
			this.store = store;

			if ( Store.YES.equals( store ) ) {
				latitudeFieldName = internalFieldName( fieldName, LATITUDE );
				longitudeFieldName = internalFieldName( fieldName, LONGITUDE );
				storedFields = CollectionHelper.asSet( latitudeFieldName, longitudeFieldName );
			}
			else {
				latitudeFieldName = null;
				longitudeFieldName = null;
				storedFields = Collections.emptySet();
			}

			this.hashCode = buildHashCode();
		}

		@Override
		public void addFields(LuceneDocumentBuilder documentBuilder, LuceneIndexSchemaObjectNode parentNode, String fieldName, GeoPoint value) {
			if ( value == null ) {
				return;
			}

			if ( Store.YES.equals( store ) ) {
				documentBuilder.addField( parentNode, new StoredField( latitudeFieldName, value.getLatitude() ) );
				documentBuilder.addField( parentNode, new StoredField( longitudeFieldName, value.getLongitude() ) );
			}

			documentBuilder.addField( parentNode, new LatLonPoint( fieldName, value.getLatitude(), value.getLongitude() ) );
		}

		@Override
		public GeoPoint parse(Document document, String fieldName) {
			IndexableField latitudeField = document.getField( latitudeFieldName );
			IndexableField longitudeField = document.getField( longitudeFieldName );

			if ( latitudeField == null || longitudeField == null ) {
				return null;
			}

			return new ImmutableGeoPoint( (double) latitudeField.numericValue(), (double) longitudeField.numericValue() );
		}

		@Override
		public Set<String> getOverriddenStoredFields() {
			return storedFields;
		}

		@Override
		public Object format(Object value) {
			// TODO see what we should do here.
			throw new UnsupportedOperationException( "format() not supported for GeoPoint" );
		}

		@Override
		public Type getDefaultSortFieldType() {
			// TODO see what we should do here.
			throw new UnsupportedOperationException( "getDefaultSortFieldType() not supported for GeoPoint" );
		}

		@Override
		public Object getSortMissingFirst() {
			// TODO see what we should do here.
			throw new UnsupportedOperationException( "getSortMissingFirst() not supported for GeoPoint" );
		}

		@Override
		public Object getSortMissingLast() {
			// TODO see what we should do here.
			throw new UnsupportedOperationException( "getSortMissingLast() not supported for GeoPoint" );
		}

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) {
				return true;
			}
			if ( obj == null ) {
				return false;
			}
			if ( LuceneGeoPointFieldFormatter.class != obj.getClass() ) {
				return false;
			}

			LuceneGeoPointFieldFormatter other = (LuceneGeoPointFieldFormatter) obj;

			return Objects.equals( store, other.store ) &&
					Objects.equals( latitudeFieldName, other.latitudeFieldName ) &&
					Objects.equals( longitudeFieldName, other.longitudeFieldName );
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		private int buildHashCode() {
			return Objects.hash( store, latitudeFieldName, longitudeFieldName );
		}
	}
}
