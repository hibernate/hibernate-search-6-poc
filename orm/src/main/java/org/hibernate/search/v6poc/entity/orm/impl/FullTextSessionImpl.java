/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.orm.impl;

import java.util.Collection;

import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.search.v6poc.entity.orm.mapping.HibernateOrmSearchManager;
import org.hibernate.search.v6poc.entity.orm.hibernate.FullTextSession;
import org.hibernate.search.v6poc.entity.orm.hibernate.HibernateOrmSearchQueryQueryResultDefinitionContext;
import org.hibernate.search.v6poc.entity.orm.jpa.FullTextEntityManager;

public class FullTextSessionImpl extends SessionDelegatorBaseImpl implements FullTextSession {

	private HibernateOrmSearchManager searchManager = null;

	public FullTextSessionImpl(SessionImplementor delegate) {
		super( delegate );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> type) {
		if ( type.equals( FullTextEntityManager.class ) ) {
			return (T) this;
		}
		else if ( type.equals( FullTextSession.class ) ) {
			return (T) this;
		}
		else {
			return super.unwrap( type );
		}
	}

	@Override
	public HibernateOrmSearchQueryQueryResultDefinitionContext<Object> search() {
		return getSearchManager().searchAsFullTextQuery();
	}

	@Override
	public final <T> HibernateOrmSearchQueryQueryResultDefinitionContext<T> search(Class<T> type) {
		return getSearchManager().searchAsFullTextQuery( type );
	}

	@Override
	public final <T> HibernateOrmSearchQueryQueryResultDefinitionContext<T> search(Collection<? extends Class<? extends T>> types) {
		return getSearchManager().searchAsFullTextQuery( types );
	}

	private HibernateOrmSearchManager getSearchManager() {
		if ( searchManager == null ) {
			HibernateSearchContextService contextService = delegate.getSessionFactory().getServiceRegistry()
					.getService( HibernateSearchContextService.class );
			searchManager = contextService.getSearchManager( delegate );
		}
		return searchManager;
	}
}
