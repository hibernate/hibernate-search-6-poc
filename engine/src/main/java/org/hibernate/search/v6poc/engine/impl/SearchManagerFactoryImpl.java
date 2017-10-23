/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.engine.impl;

import java.util.Map;

import org.hibernate.search.v6poc.engine.SearchManager;
import org.hibernate.search.v6poc.engine.SearchManagerBuilder;
import org.hibernate.search.v6poc.engine.SearchManagerFactory;
import org.hibernate.search.v6poc.entity.mapping.MappingKey;
import org.hibernate.search.v6poc.entity.mapping.spi.Mapping;
import org.hibernate.search.v6poc.util.SearchException;


/**
 * @author Yoann Rodiere
 */
public class SearchManagerFactoryImpl implements SearchManagerFactory {

	private final Map<MappingKey<?, ?>, Mapping<?>> mappings;

	public SearchManagerFactoryImpl(Map<MappingKey<?, ?>, Mapping<?>> mappings) {
		super();
		this.mappings = mappings;
	}

	@Override
	public <T extends SearchManager> T createSearchManager(MappingKey<T, ?> mappingKey) {
		return getMapping( mappingKey ).createManagerBuilder().build();
	}

	@Override
	public <B extends SearchManagerBuilder<?>> B withOptions(MappingKey<?, B> mappingKey) {
		return getMapping( mappingKey ).createManagerBuilder();
	}

	private <B extends SearchManagerBuilder<?>> Mapping<B> getMapping(MappingKey<?, B> mappingKey) {
		@SuppressWarnings("unchecked") // See SearchManagerFactoryBuilderImpl: we are sure that, if there is a mapping, it implements Mapping<B>
		Mapping<B> mapping = (Mapping<B>) mappings.get( mappingKey );
		if ( mapping == null ) {
			throw new SearchException( "No mapping registered for mapping key '" + mappingKey + "'" );
		}
		return mapping;
	}

}
