/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.mapping.building.spi;

import java.util.Optional;
import java.util.Set;

import org.hibernate.search.v6poc.backend.document.IndexObjectFieldAccessor;
import org.hibernate.search.v6poc.backend.document.model.IndexSchemaElement;
import org.hibernate.search.v6poc.backend.document.model.ObjectFieldStorage;
import org.hibernate.search.v6poc.entity.model.SearchModel;
import org.hibernate.search.v6poc.entity.model.spi.IndexedTypeIdentifier;

public interface IndexModelBindingContext {

	Iterable<IndexObjectFieldAccessor> getParentIndexObjectAccessors();

	IndexSchemaElement getSchemaElement();

	SearchModel getSearchModel();

	/**
	 * Inform the model collector that documents will always be provided along
	 * with an explicit routing key,
	 * to be used to route the document to a specific shard.
	 */
	void explicitRouting();

	Optional<IndexModelBindingContext> addIndexedEmbeddedIfIncluded(IndexedTypeIdentifier parentTypeId,
			String relativePrefix, ObjectFieldStorage storage, Integer maxDepth, Set<String> includePaths);
}
