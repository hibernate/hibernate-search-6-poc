/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.model.spi;

import java.util.Optional;
import java.util.Set;

import org.hibernate.search.v6poc.backend.document.spi.IndexFieldReference;
import org.hibernate.search.v6poc.backend.document.spi.IndexObjectReference;
import org.hibernate.search.v6poc.backend.projection.spi.Projection;
import org.hibernate.search.v6poc.entity.model.spi.IndexableReference;

/**
 * @author Yoann Rodiere
 */
public interface IndexModelCollector {

	FieldModelContext field(String relativeName);

	IndexModelCollector childObject(String relativeName);

	/**
	 * Add a projection that will re-create the object represented by this index model builder
	 * from the values of the given required fields.
	 */
	// TODO move projections setup to another class: this has nothing to do with the index itself, it's more about mapping
	void projection(Set<IndexFieldReference<?>> requiredFields, Projection projection);

	// TODO move projections setup to another class: this has nothing to do with the index itself, it's more about mapping
	void projection(String relativeName, Set<IndexableReference<?>> requiredFields, Projection projection);

	IndexObjectReference asReference();

	<T extends IndexModelCollector> Optional<T> unwrap(Class<T> clazz);

}
