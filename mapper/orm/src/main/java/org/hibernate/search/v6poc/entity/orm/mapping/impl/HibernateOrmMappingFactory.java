/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.mapping.impl;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.entity.mapping.spi.MappingImplementor;
import org.hibernate.search.v6poc.entity.orm.mapping.HibernateOrmMapping;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoMappingDelegate;
import org.hibernate.search.v6poc.entity.pojo.mapping.spi.PojoMappingFactory;

public final class HibernateOrmMappingFactory implements PojoMappingFactory<HibernateOrmMapping> {

	private final SessionFactoryImplementor sessionFactoryImplementor;

	public HibernateOrmMappingFactory(SessionFactoryImplementor sessionFactoryImplementor) {
		this.sessionFactoryImplementor = sessionFactoryImplementor;
	}

	@Override
	public MappingImplementor<HibernateOrmMapping> createMapping(ConfigurationPropertySource propertySource,
			PojoMappingDelegate mappingDelegate) {
		return new HibernateOrmMappingImpl( mappingDelegate, sessionFactoryImplementor );
	}
}
