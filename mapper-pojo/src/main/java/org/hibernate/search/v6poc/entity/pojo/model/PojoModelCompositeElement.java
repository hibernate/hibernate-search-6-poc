/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model;

import java.util.stream.Stream;

/**
 * A potentially composite element in the POJO model.
 * <p>
 * Offers ways to create {@link PojoModelElementAccessor accessors} allowing
 * to retrieve data from {@link PojoElement instances} passed to bridges.
 *
 * @see PojoModelType
 * @see PojoModelProperty
 */
public interface PojoModelCompositeElement extends PojoModelElement {

	// FIXME what if I want a PojoModelElementAccessor<List<MyType>>?
	<T> PojoModelElementAccessor<T> createAccessor(Class<T> type);

	PojoModelElementAccessor<?> createAccessor();

	PojoModelProperty property(String relativeFieldName);

	Stream<? extends PojoModelProperty> properties();

}
