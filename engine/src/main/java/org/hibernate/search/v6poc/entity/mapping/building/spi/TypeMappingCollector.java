/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.mapping.building.spi;

import java.util.Collection;

import org.hibernate.search.v6poc.entity.model.spi.IndexedTypeIdentifier;

/**
 * @author Yoann Rodiere
 */
public interface TypeMappingCollector {

	<C> void collect(MapperImplementor<C, ?, ?> mapper,
			IndexedTypeIdentifier typeId, String indexName,
			Collection<? extends TypeMappingContributor<? super C>> contributors);

}
