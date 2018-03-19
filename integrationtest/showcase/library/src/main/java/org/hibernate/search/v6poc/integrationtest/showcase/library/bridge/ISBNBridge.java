/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.showcase.library.bridge;

import org.hibernate.search.v6poc.entity.pojo.bridge.ValueBridge;
import org.hibernate.search.v6poc.integrationtest.showcase.library.model.ISBN;

public class ISBNBridge implements ValueBridge<ISBN, String> {

	// TODO use a default normalizer that removes hyphens

	@Override
	public String toIndexedValue(ISBN value) {
		return value == null ? null : value.getStringValue();
	}

	@Override
	public Object fromIndexedValue(String indexedValue) {
		return indexedValue == null ? null : new ISBN( indexedValue );
	}
}
