/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model;


/**
 * A {@link PojoElement} contains a value that can be processed into
 * an index document, be it composite (an entity) or atomic
 * (a primitive value).
 * <p>
 * {@link PojoElement}s only provide access to a set of previously
 * registered paths, accessed through a {@link PojoModelElementAccessor}.
 *
 * @see PojoModelCompositeElement
 */
public interface PojoElement {

}
