/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.builtin.impl;

import org.hibernate.search.v6poc.entity.pojo.bridge.spi.IdentifierBridge;

public final class DefaultIntegerIdentifierBridge implements IdentifierBridge<Integer> {

	@Override
	public String toDocumentIdentifier(Integer id) {
		return id.toString();
	}

	@Override
	public Integer fromDocumentIdentifier(String idString) {
		return Integer.parseInt( idString );
	}

}