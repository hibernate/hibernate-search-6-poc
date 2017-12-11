/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.impl;

import org.hibernate.search.v6poc.entity.pojo.model.spi.BridgedElement;
import org.hibernate.search.v6poc.entity.pojo.model.spi.BridgedElementReader;

class PojoRootBridgedElementReader<T> implements BridgedElementReader<T> {

	private final Class<T> type;

	PojoRootBridgedElementReader(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public T read(BridgedElement bridgedElement) {
		return type.cast( ((PojoBridgedElement) bridgedElement).get() );
	}

}