/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoSessionContext;

abstract class PojoTypeWorkPlan {

	final PojoSessionContext sessionContext;

	PojoTypeWorkPlan(PojoSessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	abstract void add(Object id, Object entity);

	abstract void update(Object id, Object entity);

	abstract void update(Object id, Object entity, String... dirtyPaths);

	abstract void delete(Object id, Object entity);

}
