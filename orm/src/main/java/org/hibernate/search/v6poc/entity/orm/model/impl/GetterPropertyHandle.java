/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.model.impl;

import org.hibernate.property.access.spi.Getter;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;
import org.hibernate.search.v6poc.util.SearchException;

/**
 * @author Yoann Rodiere
 */
final class GetterPropertyHandle implements PropertyHandle {

	private final String propertyName;
	private final Getter getter;

	GetterPropertyHandle(String propertyName, Getter getter) {
		this.propertyName = propertyName;
		this.getter = getter;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + propertyName + "]";
	}

	@Override
	public String getName() {
		return propertyName;
	}

	@Override
	public Object get(Object thiz) {
		try {
			return getter.get( thiz );
		}
		catch (RuntimeException e) {
			throw new SearchException( "Exception while invoking '" + getter + "' on '" + thiz + "'" , e );
		}
	}

}
