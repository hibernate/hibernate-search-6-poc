/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.mapping.spi;

import org.hibernate.search.v6poc.engine.spi.ServiceManager;
import org.hibernate.search.v6poc.logging.spi.ContextualFailureCollector;

/**
 * A build context for mappings.
 */
public interface MappingBuildContext {

	ServiceManager getServiceManager();

	/**
	 * A collector of (non-fatal) failures, allowing to notify Hibernate Search
	 * that something went wrong and bootstrap should be aborted at some point,
	 * while still continuing the bootstrap process for some time to collect other errors
	 * that could be relevant to users.
	 *
	 * @return A failure collector.
	 */
	ContextualFailureCollector getFailureCollector();

}
