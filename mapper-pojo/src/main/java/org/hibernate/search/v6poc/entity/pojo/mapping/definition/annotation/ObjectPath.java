/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({}) // Only used as a component in other annotations
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectPath {

	/**
	 * @return A list of components in the paths, each representing a property and a way to extract its value.
	 */
	PropertyValue[] value();

}
