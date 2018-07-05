/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.index.impl;

import org.hibernate.search.v6poc.backend.Backend;
import org.hibernate.search.v6poc.backend.index.spi.IndexManagerBuilder;
import org.hibernate.search.v6poc.backend.spi.BackendImplementor;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.backend.spi.BackendBuildContext;
import org.hibernate.search.v6poc.util.AssertionFailure;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.StubBackendBehavior;
import org.hibernate.search.v6poc.util.impl.integrationtest.common.stub.backend.document.impl.StubDocumentElement;

public class StubBackend implements BackendImplementor<StubDocumentElement>, Backend {

	private final String name;

	StubBackend(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return StubBackend.class.getSimpleName() + "[" + name + "]";
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		throw new AssertionFailure( getClass().getName() + " cannot be unwrapped" );
	}

	@Override
	public Backend toAPI() {
		return this;
	}

	public StubBackendBehavior getBehavior() {
		return StubBackendBehavior.get( name );
	}

	@Override
	public IndexManagerBuilder<StubDocumentElement> createIndexManagerBuilder(String indexName, boolean isMultiTenancyEnabled, BackendBuildContext context,
			ConfigurationPropertySource propertySource) {
		return new StubIndexManagerBuilder( this, indexName );
	}

	@Override
	public void close() {
		// No-op
	}
}
