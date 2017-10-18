/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.model.spi;

import java.util.Optional;

import org.hibernate.search.v6poc.backend.document.spi.IndexObjectReference;

/**
 * @author Yoann Rodiere
 */
public interface IndexModelCollector {

	FieldModelContext field(String relativeName);

	IndexModelCollector childObject(String relativeName);

	IndexObjectReference asReference();

	<T extends IndexModelCollector> Optional<T> unwrap(Class<T> clazz);

}
