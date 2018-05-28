/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.engine.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.model.dsl.spi.IndexSchemaRootNodeBuilder;
import org.hibernate.search.v6poc.backend.index.spi.IndexManager;
import org.hibernate.search.v6poc.backend.index.spi.IndexManagerBuilder;
import org.hibernate.search.v6poc.backend.index.spi.IndexManagerImplementor;
import org.hibernate.search.v6poc.backend.spi.BackendImplementor;
import org.hibernate.search.v6poc.backend.spi.BackendFactory;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.cfg.spi.ConfigurationProperty;
import org.hibernate.search.v6poc.engine.spi.BeanProvider;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.mapping.building.impl.RootIndexModelBindingContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexManagerBuildingState;
import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.util.AssertionFailure;
import org.hibernate.search.v6poc.util.impl.common.SuppressingCloser;


/**
 * @author Yoann Rodiere
 */
// TODO close every backend built so far (which should close index managers) in case of failure
class IndexManagerBuildingStateHolder {

	private static final ConfigurationProperty<Optional<String>> INDEX_BACKEND_NAME =
			ConfigurationProperty.forKey( "backend" ).asString().build();

	private static final ConfigurationProperty<Optional<String>> BACKEND_TYPE =
			ConfigurationProperty.forKey( "type" ).asString().build();

	private final BuildContext buildContext;
	private final ConfigurationPropertySource propertySource;
	private final ConfigurationPropertySource defaultIndexPropertySource;

	private final Map<String, BackendImplementor<?>> backendsByName = new HashMap<>();
	private final Map<String, IndexMappingBuildingStateImpl<?>> indexManagerBuildingStateByName = new HashMap<>();

	IndexManagerBuildingStateHolder(BuildContext buildContext,
			ConfigurationPropertySource propertySource) {
		this.buildContext = buildContext;
		this.propertySource = propertySource;
		this.defaultIndexPropertySource = propertySource.withMask( "index.default" );
	}

	public IndexManagerBuildingState<?> startBuilding(String rawIndexName, boolean multiTenancyEnabled) {
		ConfigurationPropertySource indexPropertySource = propertySource.withMask( "index." + rawIndexName )
				.withFallback( defaultIndexPropertySource );
		// TODO more checks on the backend name (is non-null, non-empty)
		String backendName = INDEX_BACKEND_NAME.get( indexPropertySource ).get();
		BackendImplementor<?> backend = backendsByName.computeIfAbsent( backendName, this::createBackend );
		String normalizedIndexName = backend.normalizeIndexName( rawIndexName );

		IndexMappingBuildingStateImpl<?> state = indexManagerBuildingStateByName.get( normalizedIndexName );
		if ( state == null ) {
			state = createIndexManagerBuildingState( backend, normalizedIndexName, multiTenancyEnabled, indexPropertySource );
			indexManagerBuildingStateByName.put( normalizedIndexName, state );
		}
		return state;
	}

	Map<String, BackendImplementor<?>> getBackendsByName() {
		return backendsByName;
	}

	Map<String, IndexManagerImplementor<?>> getIndexManagersByName() {
		Map<String, IndexManagerImplementor<?>> indexManagersByName = new HashMap<>();
		for ( Map.Entry<String, IndexMappingBuildingStateImpl<?>> entry : indexManagerBuildingStateByName.entrySet() ) {
			indexManagersByName.put( entry.getKey(), entry.getValue().getBuilt() );
		}
		return indexManagersByName;
	}

	void closeOnFailure(SuppressingCloser closer) {
		closer.pushAll( state -> state.closeOnFailure( closer ), indexManagerBuildingStateByName.values() );
		closer.pushAll( BackendImplementor::close, backendsByName.values() );
	}

	private <D extends DocumentElement> IndexMappingBuildingStateImpl<D> createIndexManagerBuildingState(
			BackendImplementor<D> backend, String normalizedIndexName, boolean multiTenancyEnabled,
			ConfigurationPropertySource indexPropertySource) {
		IndexManagerBuilder<D> builder = backend.createIndexManagerBuilder( normalizedIndexName, multiTenancyEnabled, buildContext, indexPropertySource );
		IndexSchemaRootNodeBuilder schemaRootNodeBuilder = builder.getSchemaRootNodeBuilder();
		IndexModelBindingContext bindingContext = new RootIndexModelBindingContext( schemaRootNodeBuilder );
		return new IndexMappingBuildingStateImpl<>( normalizedIndexName, builder, bindingContext );
	}

	private BackendImplementor<?> createBackend(String backendName) {
		ConfigurationPropertySource backendPropertySource = propertySource.withMask( "backend." + backendName );
		// TODO more checks on the backend type (non-null, non-empty)
		String backendType = BACKEND_TYPE.get( backendPropertySource ).get();

		BeanProvider beanProvider = buildContext.getServiceManager().getBeanProvider();
		BackendFactory backendFactory = beanProvider.getBean( backendType, BackendFactory.class );
		return backendFactory.create( backendName, buildContext, backendPropertySource );
	}

	private class IndexMappingBuildingStateImpl<D extends DocumentElement> implements IndexManagerBuildingState<D> {

		private final String indexName;
		private final IndexManagerBuilder<D> builder;
		private final IndexModelBindingContext bindingContext;

		private IndexManagerImplementor<D> built;

		IndexMappingBuildingStateImpl(String indexName,
				IndexManagerBuilder<D> builder,
				IndexModelBindingContext bindingContext) {
			this.indexName = indexName;
			this.builder = builder;
			this.bindingContext = bindingContext;
		}

		void closeOnFailure(SuppressingCloser closer) {
			if ( built != null ) {
				closer.push( IndexManagerImplementor::close, built );
			}
			else {
				closer.push( IndexManagerBuilder::closeOnFailure, builder );
			}
		}

		@Override
		public String getIndexName() {
			return indexName;
		}

		@Override
		public IndexModelBindingContext getRootBindingContext() {
			return bindingContext;
		}

		@Override
		public IndexManager<D> build() {
			if ( built != null ) {
				throw new AssertionFailure(
						"Trying to build index manager " + indexName + " twice."
						+ " There is probably a bug in the mapper implementation."
				);
			}
			built = builder.build();
			return built;
		}

		public IndexManagerImplementor<D> getBuilt() {
			if ( built == null ) {
				throw new AssertionFailure(
						"Index manager " + indexName + " was not built by the mapper as expected."
						+ " There is probably a bug in the mapper implementation."
				);
			}
			return built;
		}
	}

}
