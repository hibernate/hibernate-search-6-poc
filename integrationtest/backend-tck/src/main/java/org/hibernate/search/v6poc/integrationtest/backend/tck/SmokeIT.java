/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.backend.tck;

import java.time.LocalDate;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.v6poc.backend.document.model.dsl.ObjectFieldStorage;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaObjectField;
import org.hibernate.search.v6poc.backend.index.spi.ChangesetIndexWorker;
import org.hibernate.search.v6poc.backend.index.spi.IndexManager;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTarget;
import org.hibernate.search.v6poc.backend.spatial.GeoPoint;
import org.hibernate.search.v6poc.backend.spatial.ImmutableGeoPoint;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.integrationtest.backend.tck.util.rule.SearchSetupHelper;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.StubSessionContext;
import org.hibernate.search.v6poc.search.DocumentReference;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.SearchQuery;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hibernate.search.v6poc.util.impl.integrationtest.common.assertion.DocumentReferencesSearchResultAssert.assertThat;
import static org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.mapper.StubMapperUtils.referenceProvider;

/**
 * @author Yoann Rodiere
 */
public class SmokeIT {

	@Rule
	public SearchSetupHelper setupHelper = new SearchSetupHelper();

	private IndexAccessors indexAccessors;
	private IndexManager<?> indexManager;
	private String indexName;
	private SessionContext sessionContext = new StubSessionContext();

	@Before
	public void setup() {
		setupHelper.withDefaultConfiguration()
				.withIndex(
						"MappedType", "IndexName",
						ctx -> this.indexAccessors = new IndexAccessors( ctx.getSchemaElement() ),
						(indexManager, indexName) -> {
							this.indexManager = indexManager;
							this.indexName = indexName;
						}
				)
				.setup();

		initData();
	}

	@Test
	public void predicate_match() {
		IndexSearchTarget searchTarget = indexManager.createSearchTarget().build();

		SearchQuery<DocumentReference> query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().match().onField( "string" ).matching( "text 1" )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().match().onField( "string_analyzed" ).matching( "text" )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1", "2", "3" )
				.hasHitCount( 3 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().match().onField( "integer" ).matching( 1 )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().match().onField( "localDate" ).matching( LocalDate.of( 2018, 1, 1 ) )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().match().onField( "flattenedObject.string" ).matching( "text 1_1" )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );
	}

	@Test
	public void predicate_range() {
		IndexSearchTarget searchTarget = indexManager.createSearchTarget().build();

		SearchQuery<DocumentReference> query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().range().onField( "string" ).from( "text 2" ).to( "text 42" )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "2", "3" )
				.hasHitCount( 2 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().range().onField( "string_analyzed" ).from( "2" ).to( "42" )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "2", "3" )
				.hasHitCount( 2 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().range().onField( "integer" ).from( 2 ).to( 42 )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "2", "3" )
				.hasHitCount( 2 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().range().onField( "localDate" )
						.from( LocalDate.of( 2018, 1, 2 ) )
						.to( LocalDate.of( 2018, 2, 23 ) )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "2" )
				.hasHitCount( 1 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().range().onField( "flattenedObject.integer" ).from( 201 ).to( 242 )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "2" )
				.hasHitCount( 1 );
	}

	@Test
	public void predicate_boolean() {
		IndexSearchTarget searchTarget = indexManager.createSearchTarget().build();

		SearchQuery<DocumentReference> query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().bool( b -> {
					b.should().match().onField( "integer" ).matching( 1 );
					b.should().match().onField( "integer" ).matching( 2 );
				} )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1", "2" )
				.hasHitCount( 2 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().bool( b -> {
					b.must().match().onField( "string_analyzed" ).matching( "text" );
					b.filter().match().onField( "integer" ).matching( 1 );
				} )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );

		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().bool( b -> {
					b.must().match().onField( "string_analyzed" ).matching( "text" );
					b.mustNot().match().onField( "integer" ).matching( 2 );
				} )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1", "3" )
				.hasHitCount( 2 );
	}

	@Test
	public void predicate_nested() {
		IndexSearchTarget searchTarget = indexManager.createSearchTarget().build();

		// Without nested storage, we expect predicates to be able to match on different objects
		SearchQuery<DocumentReference> query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().bool( b -> {
					b.must().match().onField( "flattenedObject.string" ).matching( "text 1_2" );
					b.must().match().onField( "flattenedObject.integer" ).matching( 101 );
				} )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );

		// With nested storage, we expect direct queries to never match
		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().match().onField( "nestedObject.integer" ).matching( 101 )
				.build();
		assertThat( query )
				.hasNoHits()
				.hasHitCount( 0 );

		// ... and predicates within nested queries to be unable to match on different objects
		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().nested().onObjectField( "nestedObject" ).bool( b -> {
					b.must().match().onField( "nestedObject.string" ).matching( "text 1_2" );
					b.must().match().onField( "nestedObject.integer" ).matching( 101 );
				} )
				.build();
		assertThat( query )
				.hasNoHits()
				.hasHitCount( 0 );

		// ... but predicates should still be able to match on the same object
		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().nested().onObjectField( "nestedObject" ).bool( b -> {
					b.must().match().onField( "nestedObject.string" ).matching( "text 1_1" );
					b.must().match().onField( "nestedObject.integer" ).matching( 101 );
				} )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );
	}

	@Test
	public void separatePredicate() {
		IndexSearchTarget searchTarget = indexManager.createSearchTarget().build();

		SearchPredicate predicate = searchTarget.predicate().match().onField( "string" ).matching( "text 1" );
		SearchQuery<DocumentReference> query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate( predicate )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1" )
				.hasHitCount( 1 );

		predicate = searchTarget.predicate().range().onField( "integer" ).from( 1 ).to( 2 );
		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate( predicate )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1", "2" )
				.hasHitCount( 2 );

		predicate = searchTarget.predicate().bool()
				.should().match().onField( "integer" ).matching( 1 )
				.should().match().onField( "integer" ).matching( 2 )
				.end();
		query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate( predicate )
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1", "2" )
				.hasHitCount( 2 );
	}

	private void initData() {
		ChangesetIndexWorker<? extends DocumentElement> worker = indexManager.createWorker( sessionContext );
		worker.add( referenceProvider( "1" ), document -> {
			indexAccessors.string.write( document, "text 1" );
			indexAccessors.string_analyzed.write( document, "text 1" );
			indexAccessors.integer.write( document, 1 );
			indexAccessors.localDate.write( document, LocalDate.of( 2018, 1, 1 ) );
			indexAccessors.geoPoint.write( document, new ImmutableGeoPoint( 0, 1 ) );

			DocumentElement flattenedObject = indexAccessors.flattenedObject.self.add( document );
			indexAccessors.flattenedObject.string.write( flattenedObject, "text 1_1" );
			indexAccessors.flattenedObject.integer.write( flattenedObject, 101 );
			flattenedObject = indexAccessors.flattenedObject.self.add( document );
			indexAccessors.flattenedObject.string.write( flattenedObject, "text 1_2" );
			indexAccessors.flattenedObject.integer.write( flattenedObject, 102 );

			DocumentElement nestedObject = indexAccessors.nestedObject.self.add( document );
			indexAccessors.nestedObject.string.write( nestedObject, "text 1_1" );
			indexAccessors.nestedObject.integer.write( nestedObject, 101 );
			nestedObject = indexAccessors.nestedObject.self.add( document );
			indexAccessors.nestedObject.string.write( nestedObject, "text 1_2" );
			indexAccessors.nestedObject.integer.write( nestedObject, 102 );
		} );

		worker.add( referenceProvider( "2" ), document -> {
			indexAccessors.string.write( document, "text 2" );
			indexAccessors.string_analyzed.write( document, "text 2" );
			indexAccessors.integer.write( document, 2 );
			indexAccessors.localDate.write( document, LocalDate.of( 2018, 1, 2 ) );
			indexAccessors.geoPoint.write( document, new ImmutableGeoPoint( 0, 2 ) );

			DocumentElement flattenedObject = indexAccessors.flattenedObject.self.add( document );
			indexAccessors.flattenedObject.string.write( flattenedObject, "text 2_1" );
			indexAccessors.flattenedObject.integer.write( flattenedObject, 201 );
			flattenedObject = indexAccessors.flattenedObject.self.add( document );
			indexAccessors.flattenedObject.string.write( flattenedObject, "text 2_2" );
			indexAccessors.flattenedObject.integer.write( flattenedObject, 202 );

			DocumentElement nestedObject = indexAccessors.nestedObject.self.add( document );
			indexAccessors.nestedObject.string.write( nestedObject, "text 2_1" );
			indexAccessors.nestedObject.integer.write( nestedObject, 201 );
			nestedObject = indexAccessors.nestedObject.self.add( document );
			indexAccessors.nestedObject.string.write( nestedObject, "text 2_2" );
			indexAccessors.nestedObject.integer.write( nestedObject, 202 );
		} );

		worker.add( referenceProvider( "3" ), document -> {
			indexAccessors.string.write( document, "text 3" );
			indexAccessors.string_analyzed.write( document, "text 3" );
			indexAccessors.integer.write( document, 3 );
		} );

		worker.add( referenceProvider( "neverMatching" ), document -> {
			indexAccessors.string.write( document, "never matching" );
			indexAccessors.string_analyzed.write( document, "never matching" );
			indexAccessors.integer.write( document, 9484 );
		} );

		worker.add( referenceProvider( "empty" ), document -> { } );

		worker.execute().join();

		// Check that all documents are searchable
		IndexSearchTarget searchTarget = indexManager.createSearchTarget().build();
		SearchQuery<DocumentReference> query = searchTarget.query( sessionContext )
				.asReferences()
				.predicate().all().end()
				.build();
		assertThat( query )
				.hasReferencesHitsAnyOrder( indexName, "1", "2", "3", "neverMatching", "empty" );
	}

	private static class IndexAccessors {
		final IndexFieldAccessor<String> string;
		final IndexFieldAccessor<String> string_analyzed;
		final IndexFieldAccessor<Integer> integer;
		final IndexFieldAccessor<LocalDate> localDate;
		final IndexFieldAccessor<GeoPoint> geoPoint;
		final ObjectAccessors flattenedObject;
		final ObjectAccessors nestedObject;

		IndexAccessors(IndexSchemaElement root) {
			string = root.field( "string" ).asString().createAccessor();
			string_analyzed = root.field( "string_analyzed" ).asString()
					.analyzer( "default" )
					.createAccessor();
			integer = root.field( "integer" ).asInteger().createAccessor();
			localDate = root.field( "localDate" ).asLocalDate().createAccessor();
			geoPoint = root.field( "geoPoint" ).asGeoPoint().createAccessor();
			IndexSchemaObjectField flattenedObjectField = root.objectField( "flattenedObject", ObjectFieldStorage.FLATTENED );
			flattenedObject = new ObjectAccessors( flattenedObjectField );
			IndexSchemaObjectField nestedObjectField = root.objectField( "nestedObject", ObjectFieldStorage.NESTED );
			nestedObject = new ObjectAccessors( nestedObjectField );
		}
	}

	private static class ObjectAccessors {
		final IndexObjectFieldAccessor self;
		final IndexFieldAccessor<String> string;
		final IndexFieldAccessor<Integer> integer;

		ObjectAccessors(IndexSchemaObjectField objectField) {
			self = objectField.createAccessor();
			string = objectField.field( "string" ).asString().createAccessor();
			integer = objectField.field( "integer" ).asInteger().createAccessor();
		}
	}
}
