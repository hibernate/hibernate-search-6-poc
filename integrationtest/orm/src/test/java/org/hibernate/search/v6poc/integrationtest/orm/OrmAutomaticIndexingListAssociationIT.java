/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.orm;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.search.v6poc.entity.orm.cfg.SearchOrmSettings;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.Field;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.rule.BackendMock;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.index.impl.StubBackendFactory;
import org.hibernate.search.v6poc.util.impl.integrationtest.orm.OrmUtils;
import org.hibernate.service.ServiceRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test automatic indexing based on Hibernate ORM entity events.
 *
 * This test only checks updates involving a multi-valued, List association.
 * Other tests in the same package check more basic, direct updates or updates involving different associations.
 */
public class OrmAutomaticIndexingListAssociationIT {

	private static final String PREFIX = SearchOrmSettings.PREFIX;

	@Rule
	public BackendMock backendMock = new BackendMock( "stubBackend" );

	private SessionFactory sessionFactory;

	@Before
	public void setup() {
		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
				.applySetting( PREFIX + "backend.stubBackend.type", StubBackendFactory.class.getName() )
				.applySetting( PREFIX + "index.default.backend", "stubBackend" );

		ServiceRegistry serviceRegistry = registryBuilder.build();

		MetadataSources ms = new MetadataSources( serviceRegistry )
				.addAnnotatedClass( IndexedEntity.class )
				.addAnnotatedClass( ContainedEntity.class );

		Metadata metadata = ms.buildMetadata();

		final SessionFactoryBuilder sfb = metadata.getSessionFactoryBuilder();

		backendMock.expectSchema( IndexedEntity.INDEX, b -> b
				.objectField( "containedList", b2 -> b2
						.field( "indexedField", String.class )
				)
				.objectField( "child", b3 -> b3
						.objectField( "containedList", b2 -> b2
								.field( "indexedField", String.class )
						)
				)
		);

		sessionFactory = sfb.build();
		backendMock.verifyExpectationsMet();
	}

	@After
	public void cleanup() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Test
	public void directAssociationUpdate() {
		OrmUtils.withinTransaction( sessionFactory, session -> {
			IndexedEntity entity1 = new IndexedEntity();
			entity1.setId( 1 );

			session.persist( entity1 );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.add( "1", b -> { } )
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test adding a value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			IndexedEntity entity1 = session.get( IndexedEntity.class, 1 );

			ContainedEntity containedEntity = new ContainedEntity();
			containedEntity.setId( 2 );
			containedEntity.setIndexedField( "firstValue" );

			entity1.getContainedList().add( containedEntity );
			containedEntity.getContainingAsList().add( entity1 );

			session.persist( containedEntity );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "containedList", b2 -> b2
									.field( "indexedField", "firstValue" )
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test adding a second value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			IndexedEntity entity1 = session.get( IndexedEntity.class, 1 );

			ContainedEntity containedEntity = new ContainedEntity();
			containedEntity.setId( 3 );
			containedEntity.setIndexedField( "secondValue" );

			entity1.getContainedList().add( containedEntity );
			containedEntity.getContainingAsList().add( entity1 );

			session.persist( containedEntity );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "containedList", b2 -> b2
									.field( "indexedField", "firstValue" )
							)
							.objectField( "containedList", b2 -> b2
									.field( "indexedField", "secondValue" )
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test removing a value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			IndexedEntity entity1 = session.get( IndexedEntity.class, 1 );

			ContainedEntity containedEntity = entity1.getContainedList().get( 0 );

			containedEntity.getContainingAsList().clear();
			entity1.getContainedList().remove( containedEntity );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "containedList", b2 -> b2
									.field( "indexedField", "secondValue" )
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();
	}

	@Test
	public void indirectAssociationUpdate() {
		OrmUtils.withinTransaction( sessionFactory, session -> {
			IndexedEntity entity1 = new IndexedEntity();
			entity1.setId( 1 );

			ContainingEntity containingEntity1 = new ContainingEntity();
			containingEntity1.setId( 2 );
			entity1.setChild( containingEntity1 );
			containingEntity1.setParent( entity1 );

			ContainingEntity deeplyNestedContainingEntity = new ContainingEntity();
			deeplyNestedContainingEntity.setId( 3 );
			containingEntity1.setChild( deeplyNestedContainingEntity );
			deeplyNestedContainingEntity.setParent( containingEntity1 );

			session.persist( deeplyNestedContainingEntity );
			session.persist( containingEntity1 );
			session.persist( entity1 );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.add( "1", b -> b
							.objectField( "child", b2 -> { } )
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test adding a value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ContainingEntity containingEntity1 = session.get( ContainingEntity.class, 2 );

			ContainedEntity containedEntity = new ContainedEntity();
			containedEntity.setId( 4 );
			containedEntity.setIndexedField( "firstValue" );

			containingEntity1.getContainedList().add( containedEntity );
			containedEntity.getContainingAsList().add( containingEntity1 );

			session.persist( containedEntity );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "child", b2 -> b2
									.objectField( "containedList", b3 -> b3
											.field( "indexedField", "firstValue" )
									)
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test adding another value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ContainingEntity containingEntity1 = session.get( ContainingEntity.class, 2 );

			ContainedEntity containedEntity = new ContainedEntity();
			containedEntity.setId( 5 );
			containedEntity.setIndexedField( "secondValue" );

			containingEntity1.getContainedList().add( containedEntity );
			containedEntity.getContainingAsList().add( containingEntity1 );

			session.persist( containedEntity );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "child", b2 -> b2
									.objectField( "containedList", b3 -> b3
											.field( "indexedField", "firstValue" )
									)
									.objectField( "containedList", b3 -> b3
											.field( "indexedField", "secondValue" )
									)
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test adding a value that is too deeply nested to matter (it's out of the IndexedEmbedded scope)
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ContainingEntity deeplyNestedContainingEntity1 = session.get( ContainingEntity.class, 3 );

			ContainedEntity containedEntity = new ContainedEntity();
			containedEntity.setId( 6 );
			containedEntity.setIndexedField( "outOfScopeValue" );

			deeplyNestedContainingEntity1.getContainedList().add( containedEntity );
			containedEntity.getContainingAsList().add( deeplyNestedContainingEntity1 );

			session.persist( containedEntity );

			// Do not expect any work
		} );
		backendMock.verifyExpectationsMet();

		// Test removing a value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ContainingEntity containingEntity1 = session.get( ContainingEntity.class, 2 );

			ContainedEntity containedEntity = containingEntity1.getContainedList().get( 0 );

			containedEntity.getContainingAsList().clear();
			containingEntity1.getContainedList().remove( containedEntity );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "child", b2 -> b2
									.objectField( "containedList", b3 -> b3
											.field( "indexedField", "secondValue" )
									)
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();
	}

	@Test
	public void indirectValueUpdate() {
		OrmUtils.withinTransaction( sessionFactory, session -> {
			IndexedEntity entity1 = new IndexedEntity();
			entity1.setId( 1 );

			ContainingEntity containingEntity1 = new ContainingEntity();
			containingEntity1.setId( 2 );
			entity1.setChild( containingEntity1 );
			containingEntity1.setParent( entity1 );

			ContainingEntity deeplyNestedContainingEntity = new ContainingEntity();
			deeplyNestedContainingEntity.setId( 3 );
			containingEntity1.setChild( deeplyNestedContainingEntity );
			deeplyNestedContainingEntity.setParent( containingEntity1 );

			ContainedEntity containedEntity1 = new ContainedEntity();
			containedEntity1.setId( 4 );
			containedEntity1.setIndexedField( "initialValue" );
			containingEntity1.getContainedList().add( containedEntity1 );
			containedEntity1.getContainingAsList().add( containingEntity1 );

			ContainedEntity containedEntity2 = new ContainedEntity();
			containedEntity2.setId( 5 );
			containedEntity2.setIndexedField( "initialOutOfScopeValue" );
			deeplyNestedContainingEntity.getContainedList().add( containedEntity2 );
			containedEntity2.getContainingAsList().add( deeplyNestedContainingEntity );

			session.persist( containedEntity1 );
			session.persist( containedEntity2 );
			session.persist( deeplyNestedContainingEntity );
			session.persist( containingEntity1 );
			session.persist( entity1 );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.add( "1", b -> b
							.objectField( "child", b2 -> b2
									.objectField( "containedList", b3 -> b3
											.field( "indexedField", "initialValue" )
									)
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test updating the value
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ContainedEntity containedEntity = session.get( ContainedEntity.class, 4 );
			containedEntity.setIndexedField( "updatedValue" );

			backendMock.expectWorks( IndexedEntity.INDEX )
					.update( "1", b -> b
							.objectField( "child", b2 -> b2
									.objectField( "containedList", b3 -> b3
											.field( "indexedField", "updatedValue" )
									)
							)
					)
					.preparedThenExecuted();
		} );
		backendMock.verifyExpectationsMet();

		// Test updating a value that is too deeply nested to matter (it's out of the IndexedEmbedded scope)
		OrmUtils.withinTransaction( sessionFactory, session -> {
			ContainedEntity containedEntity = session.get( ContainedEntity.class, 5 );
			containedEntity.setIndexedField( "updatedOutOfScopeValue" );

			// Do not expect any work
		} );
		backendMock.verifyExpectationsMet();
	}

	@Entity(name = "containing")
	public static class ContainingEntity {

		@Id
		@DocumentId
		private Integer id;

		@OneToOne
		private ContainingEntity parent;

		@OneToOne(mappedBy = "parent")
		@IndexedEmbedded(includePaths = {
				"containedList.indexedField"
		})
		private ContainingEntity child;

		@ManyToMany
		@JoinTable(name = "indexed_list")
		@IndexedEmbedded(includePaths = "indexedField")
		private List<ContainedEntity> containedList = new ArrayList<>();

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public ContainingEntity getParent() {
			return parent;
		}

		public void setParent(ContainingEntity parent) {
			this.parent = parent;
		}

		public ContainingEntity getChild() {
			return child;
		}

		public void setChild(ContainingEntity child) {
			this.child = child;
		}

		public List<ContainedEntity> getContainedList() {
			return containedList;
		}
	}

	@Entity(name = "indexed")
	@Indexed(index = IndexedEntity.INDEX)
	public static class IndexedEntity extends ContainingEntity {

		static final String INDEX = "IndexedEntity";

	}

	@Entity(name = "contained")
	public static class ContainedEntity {

		@Id
		private Integer id;

		@ManyToMany(mappedBy = "containedList")
		@OrderBy("id asc") // Make sure the iteration order is predictable
		private List<ContainingEntity> containingAsList = new ArrayList<>();

		@Basic
		@Field
		private String indexedField;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public List<ContainingEntity> getContainingAsList() {
			return containingAsList;
		}

		public String getIndexedField() {
			return indexedField;
		}

		public void setIndexedField(String indexedField) {
			this.indexedField = indexedField;
		}

	}

}
