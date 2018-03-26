/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import java.util.function.Supplier;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.index.spi.DocumentContributor;
import org.hibernate.search.v6poc.backend.index.spi.DocumentReferenceProvider;
import org.hibernate.search.v6poc.backend.index.spi.IndexManager;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTargetBuilder;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoSessionContext;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoCaster;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRuntimeIntrospector;
import org.hibernate.search.v6poc.entity.pojo.processing.impl.PojoIndexingProcessor;
import org.hibernate.search.v6poc.util.spi.Closer;
import org.hibernate.search.v6poc.util.spi.ToStringTreeAppendable;
import org.hibernate.search.v6poc.util.spi.ToStringTreeBuilder;

/**
 * @author Yoann Rodiere
 */
public class PojoTypeManager<I, E, D extends DocumentElement> implements AutoCloseable, ToStringTreeAppendable {

	private final Class<E> indexedJavaClass;
	private final PojoCaster<E> caster;
	private final IdentifierMapping<I, E> identifierMapping;
	private final RoutingKeyProvider<E> routingKeyProvider;
	private final PojoIndexingProcessor<E> processor;
	private final IndexManager<D> indexManager;

	public PojoTypeManager(Class<E> indexedJavaClass,
			PojoCaster<E> caster,
			IdentifierMapping<I, E> identifierMapping,
			RoutingKeyProvider<E> routingKeyProvider,
			PojoIndexingProcessor<E> processor, IndexManager<D> indexManager) {
		this.indexedJavaClass = indexedJavaClass;
		this.caster = caster;
		this.identifierMapping = identifierMapping;
		this.routingKeyProvider = routingKeyProvider;
		this.processor = processor;
		this.indexManager = indexManager;
	}

	@Override
	public String toString() {
		return new ToStringTreeBuilder().value( this ).toString();
	}

	@Override
	public void close() {
		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.push( identifierMapping::close );
			closer.push( routingKeyProvider::close );
			closer.push( processor::close );
			closer.push( indexManager::close );
		}
	}

	@Override
	public void appendTo(ToStringTreeBuilder builder) {
		builder.attribute( "indexedJavaClass", indexedJavaClass )
				.attribute( "indexManager", indexManager )
				.attribute( "identifierMapping", identifierMapping )
				.attribute( "routingKeyProvider", routingKeyProvider )
				.attribute( "processor", processor );
	}

	public IdentifierMapping<I, E> getIdentifierMapping() {
		return identifierMapping;
	}

	public Class<E> getIndexedJavaClass() {
		return indexedJavaClass;
	}

	public Supplier<E> toEntitySupplier(PojoSessionContext sessionContext, Object entity) {
		PojoRuntimeIntrospector proxyIntrospector = sessionContext.getRuntimeIntrospector();
		return new CachingCastingEntitySupplier<>( caster, proxyIntrospector, entity );
	}

	public DocumentReferenceProvider toDocumentReferenceProvider(PojoSessionContext sessionContext,
			Object providedId, Supplier<E> entitySupplier) {
		String tenantId = sessionContext.getTenantIdentifier();
		I identifier = identifierMapping.getIdentifier( providedId, entitySupplier );
		String documentIdentifier = identifierMapping.toDocumentIdentifier( identifier );
		return new PojoDocumentReferenceProvider<>( routingKeyProvider, tenantId, identifier, documentIdentifier, entitySupplier );
	}

	public DocumentContributor<D> toDocumentContributor(Supplier<E> entitySupplier) {
		return new PojoDocumentContributor<>( processor, entitySupplier );
	}

	public ChangesetPojoTypeWorker<D, E> createWorker(PojoSessionContext sessionContext) {
		return new ChangesetPojoTypeWorker<>( this, sessionContext, indexManager.createWorker( sessionContext ) );
	}

	public StreamPojoTypeWorker<D, E> createStreamWorker(PojoSessionContext sessionContext) {
		return new StreamPojoTypeWorker<>( this, sessionContext, indexManager.createStreamWorker( sessionContext ) );
	}

	public IndexSearchTargetBuilder createSearchTarget() {
		return indexManager.createSearchTarget();
	}

	public void addToSearchTarget(IndexSearchTargetBuilder builder) {
		indexManager.addToSearchTarget( builder );
	}
}
