/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.spi;

import java.lang.annotation.Annotation;

import org.hibernate.search.v6poc.backend.document.model.spi.IndexSchemaElement;
import org.hibernate.search.v6poc.backend.document.spi.DocumentState;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoState;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoModelElement;
import org.hibernate.search.v6poc.entity.model.spi.SearchModel;

/**
 * @author Yoann Rodiere
 */
public interface Bridge<A extends Annotation> extends AutoCloseable {

	/* Solves HSEARCH-1306 */
	default void initialize(BuildContext buildContext, A parameters) {
		// Default does nothing
	}

	void contribute(IndexSchemaElement indexSchemaElement, PojoModelElement bridgedPojoModelElement,
			SearchModel searchModel);

	void write(DocumentState target, PojoState source);

	@Override
	default void close() {
	}

}
