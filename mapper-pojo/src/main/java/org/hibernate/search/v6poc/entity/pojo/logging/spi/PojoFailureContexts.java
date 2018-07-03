/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.logging.spi;

import java.lang.annotation.Annotation;

import org.hibernate.search.v6poc.entity.pojo.logging.impl.PojoFailureContextMessages;
import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPath;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;
import org.hibernate.search.v6poc.logging.spi.AbstractSimpleFailureContextElement;
import org.hibernate.search.v6poc.logging.spi.FailureContextElement;
import org.hibernate.search.v6poc.logging.spi.FailureContexts;

import org.jboss.logging.Messages;

public final class PojoFailureContexts {

	private static final PojoFailureContextMessages MESSAGES = Messages.getBundle( PojoFailureContextMessages.class );

	private PojoFailureContexts() {
	}

	public static FailureContextElement fromType(PojoRawTypeModel<?> typeModel) {
		return FailureContexts.fromType( typeModel );
	}

	public static FailureContextElement fromPath(PojoModelPath unboundPath) {
		return new AbstractSimpleFailureContextElement<PojoModelPath>( unboundPath ) {
			@Override
			public String render(PojoModelPath param) {
				String pathString = param == null ? "" : param.toPathString();
				return MESSAGES.path( pathString );
			}
		};
	}

	public static FailureContextElement fromAnnotation(Annotation annotation) {
		return new AbstractSimpleFailureContextElement<Annotation>( annotation ) {
			@Override
			public String render(Annotation annotation) {
				String annotationString = annotation.toString();
				return MESSAGES.annotation( annotationString );
			}
		};
	}
}
