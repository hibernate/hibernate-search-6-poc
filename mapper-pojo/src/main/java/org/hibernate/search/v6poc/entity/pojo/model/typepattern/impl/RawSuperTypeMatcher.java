/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.typepattern.impl;

import java.util.Optional;

import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoGenericTypeModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;

class RawSuperTypeMatcher implements TypePatternMatcher {
	private final PojoRawTypeModel<?> matchedRawType;
	private final PojoGenericTypeModel<?> resultType;

	RawSuperTypeMatcher(PojoRawTypeModel<?> matchedRawType, PojoGenericTypeModel<?> resultType) {
		this.matchedRawType = matchedRawType;
		this.resultType = resultType;
	}

	@Override
	public String toString() {
		return matchedRawType.getName() + " => " + resultType.getName();
	}

	@Override
	public Optional<? extends PojoGenericTypeModel<?>> match(PojoGenericTypeModel<?> typeToInspect) {
		if ( typeToInspect.getRawType().isSubTypeOf( matchedRawType ) ) {
			return Optional.of( resultType );
		}
		else {
			return Optional.empty();
		}
	}
}
