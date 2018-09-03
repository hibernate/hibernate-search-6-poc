/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.mapper.pojo.bridge.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.PropertyBridgeMapping;
import org.hibernate.search.v6poc.entity.pojo.bridge.declaration.PropertyBridgeReference;
import org.hibernate.search.v6poc.integrationtest.mapper.pojo.bridge.CustomMarkerConsumingPropertyBridge;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@PropertyBridgeMapping(bridge = @PropertyBridgeReference(type = CustomMarkerConsumingPropertyBridge.class))
public @interface CustomMarkerConsumingPropertyBridgeAnnotation {

}
