/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.mapping.building.spi;

import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.engine.spi.BuildContext;

/**
 * @author Yoann Rodiere
 */
public interface MetadataContributor {

	void contribute(BuildContext buildContext, ConfigurationPropertySource propertySource,
			MetadataCollector collector);

}
