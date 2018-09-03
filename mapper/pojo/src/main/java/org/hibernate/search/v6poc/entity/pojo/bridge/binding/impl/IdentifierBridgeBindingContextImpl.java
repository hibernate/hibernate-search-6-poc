/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.binding.impl;

import org.hibernate.search.v6poc.entity.pojo.bridge.binding.IdentifierBridgeBindingContext;
import org.hibernate.search.v6poc.entity.pojo.model.PojoModelValue;

public class IdentifierBridgeBindingContextImpl<T> implements IdentifierBridgeBindingContext<T> {
	private final PojoModelValue<T> bridgedElement;

	public IdentifierBridgeBindingContextImpl(PojoModelValue<T> bridgedElement) {
		this.bridgedElement = bridgedElement;
	}

	@Override
	public PojoModelValue<T> getBridgedElement() {
		return bridgedElement;
	}
}
