/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.converter.impl;

import org.hibernate.search.v6poc.backend.document.spi.UserIndexFieldConverter;

public final class StandardFieldConverter<F> extends AbstractFieldConverter<F, F> {

	public StandardFieldConverter(UserIndexFieldConverter<F> userConverter) {
		super( userConverter );
	}

	@Override
	public F convertFromDsl(Object value) {
		return userConverter.convertFromDsl( value );
	}
}
