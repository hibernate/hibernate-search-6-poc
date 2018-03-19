/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.builtin.impl;

import org.hibernate.search.v6poc.backend.document.model.IndexSchemaFieldContext;
import org.hibernate.search.v6poc.backend.document.model.IndexSchemaFieldTypedContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.ValueBridge;

public final class DefaultIntegerValueBridge implements ValueBridge<Integer, Integer> {

	@Override
	public IndexSchemaFieldTypedContext<Integer> bind(IndexSchemaFieldContext context) {
		return context.asInteger();
	}

	@Override
	public Integer toIndexedValue(Integer value) {
		return value;
	}

}