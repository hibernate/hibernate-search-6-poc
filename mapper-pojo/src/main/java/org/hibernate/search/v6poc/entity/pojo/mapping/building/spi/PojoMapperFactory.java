/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.building.spi;

import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.mapping.building.spi.MapperFactory;
import org.hibernate.search.v6poc.entity.mapping.spi.MappingImplementor;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoMapper;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoTypeNodeMetadataContributor;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoMappingDelegate;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoBootstrapIntrospector;


/**
 * @author Yoann Rodiere
 */
public abstract class PojoMapperFactory<M extends MappingImplementor>
		implements MapperFactory<PojoTypeNodeMetadataContributor, M> {

	private final PojoBootstrapIntrospector introspector;
	private final boolean implicitProvidedId;

	protected PojoMapperFactory(PojoBootstrapIntrospector introspector, boolean implicitProvidedId) {
		this.introspector = introspector;
		this.implicitProvidedId = implicitProvidedId;
	}

	@Override
	public final PojoMapper<M> createMapper(BuildContext buildContext, ConfigurationPropertySource propertySource) {
		return new PojoMapper<>( buildContext, propertySource, introspector, implicitProvidedId, this::createMapping );
	}

	protected abstract M createMapping(ConfigurationPropertySource propertySource,
			PojoMappingDelegate mappingDelegate);

}
