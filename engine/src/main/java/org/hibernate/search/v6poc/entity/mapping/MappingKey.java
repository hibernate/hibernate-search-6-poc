/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.mapping;

import org.hibernate.search.v6poc.engine.SearchManager;
import org.hibernate.search.v6poc.engine.SearchManagerBuilder;
import org.hibernate.search.v6poc.engine.SearchManagerFactory;

/**
 * Tagging interface for objects used as a key to retrieve mappings in
 * {@link SearchManagerFactory#createSearchManager(MappingKey)}.
 *
 * @author Yoann Rodiere
 */
public interface MappingKey<T extends SearchManager, B extends SearchManagerBuilder<T>> {

}
