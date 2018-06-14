/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.declaration;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.AnnotationBridgeBuilder;
import org.hibernate.search.v6poc.entity.pojo.bridge.mapping.BridgeBuilder;

/**
 * Allows to map a type bridge to an annotation type,
 * so that whenever the annotation is found on a type in the domain model,
 * the type bridge mapped to the annotation will be applied.
 */
@Documented
@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeBridgeMapping {

	/**
	 * Map a type bridge builder to an annotation type.
	 * <p>
	 * Each time the mapped annotation is encountered, an instance of the type bridge builder will be created.
	 * The builder will be passed the annotation through its
	 * {@link AnnotationBridgeBuilder#initialize(Annotation)} method,
	 * and then the bridge will be retrieved by calling {@link BridgeBuilder#build(BuildContext)}.
	 * <p>
	 * Type bridges mapped this way can be parameterized:
	 * the bridge will be able to take any attribute of the mapped annotation into account
	 * in its {@link AnnotationBridgeBuilder#initialize(Annotation)} method.
	 *
	 * @return A reference to the builder to use to build the type bridge.
	 */
	TypeBridgeAnnotationBuilderReference builder();

}
