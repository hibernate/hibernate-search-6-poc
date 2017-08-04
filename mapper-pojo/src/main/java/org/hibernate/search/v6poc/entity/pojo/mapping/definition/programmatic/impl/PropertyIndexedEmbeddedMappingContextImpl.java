/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.search.v6poc.entity.mapping.building.spi.TypeMappingContributor;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoPropertyNodeMappingCollector;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyIndexedEmbeddedMappingContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.programmatic.PropertyMappingContext;


/**
 * @author Yoann Rodiere
 */
public class PropertyIndexedEmbeddedMappingContextImpl extends DelegatingPropertyMappingContext
		implements PropertyIndexedEmbeddedMappingContext, TypeMappingContributor<PojoPropertyNodeMappingCollector> {

	private String prefix;

	private int maxDepth;

	private final Set<String> pathFilters = new HashSet<>();

	public PropertyIndexedEmbeddedMappingContextImpl(PropertyMappingContext parent) {
		super( parent );
	}

	@Override
	public void contribute(PojoPropertyNodeMappingCollector collector) {
		collector.indexedEmbedded( prefix, maxDepth, pathFilters );
	}

	@Override
	public PropertyIndexedEmbeddedMappingContext prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	@Override
	public PropertyIndexedEmbeddedMappingContext maxDepth(int depth) {
		this.maxDepth = depth;
		return this;
	}

	@Override
	public PropertyIndexedEmbeddedMappingContext includePaths(Collection<String> paths) {
		this.pathFilters.addAll( paths );
		return this;
	}

}
