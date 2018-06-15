/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.hibernate.search.v6poc.entity.pojo.dirtiness.impl.PojoReindexingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoSessionContext;

/**
 * @param <E> The contained entity type.
 */
class ChangesetPojoContainedTypeWorker<E> extends ChangesetPojoTypeWorker {

	private final PojoContainedTypeManager<E> typeManager;

	// Use a LinkedHashMap for deterministic iteration
	private final Map<Object, WorkPlanPerDocument> workPlansPerId = new LinkedHashMap<>();

	ChangesetPojoContainedTypeWorker(PojoContainedTypeManager<E> typeManager, PojoSessionContext sessionContext) {
		super( sessionContext );
		this.typeManager = typeManager;
	}

	@Override
	void add(Object providedId, Object entity) {
		Supplier<E> entitySupplier = typeManager.toEntitySupplier( sessionContext, entity );
		getWork( providedId ).add( entitySupplier );
	}

	@Override
	void update(Object providedId, Object entity) {
		Supplier<E> entitySupplier = typeManager.toEntitySupplier( sessionContext, entity );
		getWork( providedId ).update( entitySupplier );
	}

	@Override
	void update(Object providedId, Object entity, String... dirtyPaths) {
		Supplier<E> entitySupplier = typeManager.toEntitySupplier( sessionContext, entity );
		getWork( providedId ).update( entitySupplier, dirtyPaths );
	}

	@Override
	void delete(Object providedId, Object entity) {
		Supplier<E> entitySupplier = typeManager.toEntitySupplier( sessionContext, entity );
		getWork( providedId ).delete( entitySupplier );
	}

	void resolveDirty(PojoReindexingCollector containingEntityCollector) {
		for ( WorkPlanPerDocument workPerDocument : workPlansPerId.values() ) {
			workPerDocument.resolveDirty( containingEntityCollector );
		}
	}

	private WorkPlanPerDocument getWork(Object identifier) {
		WorkPlanPerDocument work = workPlansPerId.get( identifier );
		if ( work == null ) {
			work = new WorkPlanPerDocument();
			workPlansPerId.put( identifier, work );
		}
		return work;
	}

	private class WorkPlanPerDocument {
		private Supplier<E> entitySupplier;

		private Boolean createdInThisChangeset;

		private boolean shouldResolveToReindex;
		private boolean considerAllDirty;
		private Set<String> dirtyPaths;

		void add(Supplier<E> entitySupplier) {
			this.entitySupplier = entitySupplier;
			shouldResolveToReindex = true;
			if ( createdInThisChangeset == null ) {
				// No update yet, so we actually did create the entity in this changeset
				createdInThisChangeset = true;
			}
		}

		void update(Supplier<E> entitySupplier) {
			doUpdate( entitySupplier );
			shouldResolveToReindex = true;
			considerAllDirty = true;
			dirtyPaths = null;
		}

		void update(Supplier<E> entitySupplier, String... dirtyPaths) {
			doUpdate( entitySupplier );
			shouldResolveToReindex = true;
			if ( !considerAllDirty ) {
				for ( String dirtyPath : dirtyPaths ) {
					addDirtyPath( dirtyPath );
				}
			}
		}

		void delete(Supplier<E> entitySupplier) {
			this.entitySupplier = entitySupplier;
			if ( createdInThisChangeset == null ) {
				// No add or update yet, and we're performing a delete, so we did not create the entity in this changeset
				createdInThisChangeset = false;
			}
			else if ( createdInThisChangeset ) {
				/*
				 * We called the first add() in the same changeset, so we don't expect the entity to be contained
				 * in existing documents.
				 * Cancel everything.
				 */
				shouldResolveToReindex = false;
				considerAllDirty = false;
				dirtyPaths = null;
				createdInThisChangeset = null;
			}
		}

		void resolveDirty(PojoReindexingCollector containingEntityCollector) {
			if ( shouldResolveToReindex ) {
				shouldResolveToReindex = false; // Avoid infinite looping
				typeManager.resolveEntitiesToReindex(
						containingEntityCollector, sessionContext.getRuntimeIntrospector(), entitySupplier,
						considerAllDirty ? null : dirtyPaths
				);
			}
		}

		private void doUpdate(Supplier<E> entitySupplier) {
			this.entitySupplier = entitySupplier;
			if ( createdInThisChangeset == null ) {
				// No add yet, and we're performing an update, so we did not create the entity in this changeset
				createdInThisChangeset = false;
			}
		}

		private void addDirtyPath(String dirtyPath) {
			if ( dirtyPaths == null ) {
				dirtyPaths = new HashSet<>();
			}
			dirtyPaths.add( dirtyPath );
		}
	}

}
