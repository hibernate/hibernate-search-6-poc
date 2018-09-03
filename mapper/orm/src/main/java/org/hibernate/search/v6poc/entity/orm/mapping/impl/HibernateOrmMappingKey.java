/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.mapping.impl;

import org.hibernate.search.v6poc.entity.mapping.spi.MappingKey;
import org.hibernate.search.v6poc.entity.orm.logging.impl.HibernateOrmEventContextMessages;
import org.hibernate.search.v6poc.entity.orm.mapping.HibernateOrmMapping;

import org.jboss.logging.Messages;

public final class HibernateOrmMappingKey implements MappingKey<HibernateOrmMapping> {
	private static final HibernateOrmEventContextMessages MESSAGES =
			Messages.getBundle( HibernateOrmEventContextMessages.class );

	@Override
	public String render() {
		return MESSAGES.mapping();
	}

}
