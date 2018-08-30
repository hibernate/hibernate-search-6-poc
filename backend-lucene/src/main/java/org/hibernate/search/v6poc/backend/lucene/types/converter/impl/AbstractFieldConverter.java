/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.types.converter.impl;

import org.hibernate.search.v6poc.backend.document.spi.UserIndexFieldConverter;

abstract class AbstractFieldConverter<F, T> implements LuceneFieldConverter<F, T> {

	final UserIndexFieldConverter<F> userConverter;

	AbstractFieldConverter(UserIndexFieldConverter<F> userConverter) {
		this.userConverter = userConverter;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + userConverter + "]";
	}

	@Override
	public boolean isDslCompatibleWith(LuceneFieldConverter<?, ?> other) {
		if ( !getClass().equals( other.getClass() ) ) {
			return false;
		}
		AbstractFieldConverter<?, ?> castedOther = (AbstractFieldConverter<?, ?>) other;
		return userConverter.isDslCompatibleWith( castedOther.userConverter );
	}

}
