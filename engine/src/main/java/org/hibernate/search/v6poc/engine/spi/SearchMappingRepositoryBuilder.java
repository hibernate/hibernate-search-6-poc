/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.engine.spi;

import java.util.Properties;

import org.hibernate.search.v6poc.entity.mapping.building.spi.MappingInitiator;
import org.hibernate.search.v6poc.entity.mapping.spi.MappingKey;

/**
 * @author Yoann Rodiere
 */
public interface SearchMappingRepositoryBuilder {

	SearchMappingRepositoryBuilder setBeanResolver(BeanResolver beanResolver);

	SearchMappingRepositoryBuilder setProperty(String name, String value);

	SearchMappingRepositoryBuilder setProperties(Properties properties);

	<M> SearchMappingRepositoryBuilder addMappingInitiator(MappingKey<M> mappingKey, MappingInitiator<?, M> initiator);

	SearchMappingRepository build();

}
