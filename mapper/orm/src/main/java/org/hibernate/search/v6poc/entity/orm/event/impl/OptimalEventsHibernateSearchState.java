/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.event.impl;

import org.hibernate.search.v6poc.entity.orm.impl.HibernateSearchContextService;
import org.hibernate.search.v6poc.util.impl.common.Contracts;

/**
 * The implementation of EventsHibernateSearchState used at runtime,
 * after initialization of the ExtendedSearchIntegrator has been
 * performed.
 *
 * @author Sanne Grinovero
 */
final class OptimalEventsHibernateSearchState implements EventsHibernateSearchState {

	private final HibernateSearchContextService context;

	public OptimalEventsHibernateSearchState(HibernateSearchContextService context) {
		Contracts.assertNotNull( context, "context" );
		this.context = context;
	}

	@Override
	public HibernateSearchContextService getHibernateSearchContext() {
		return context;
	}

}
