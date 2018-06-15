/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.impl;

import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoSessionContext;

abstract class ChangesetPojoTypeWorker extends PojoTypeWorker {

	ChangesetPojoTypeWorker(PojoSessionContext sessionContext) {
		super( sessionContext );
	}

	abstract void update(Object id, Object entity, String... dirtyPaths);

}
