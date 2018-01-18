/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.impl;

import org.hibernate.search.v6poc.backend.document.DocumentElement;
import org.hibernate.search.v6poc.backend.document.IndexFieldAccessor;
import org.hibernate.search.v6poc.entity.pojo.bridge.FunctionBridge;
import org.hibernate.search.v6poc.entity.pojo.model.PojoElement;
import org.hibernate.search.v6poc.entity.pojo.model.PojoModelElementAccessor;


/**
 * @author Yoann Rodiere
 */
public class FunctionBridgeValueProcessor<T, R> implements ValueProcessor {

	private final FunctionBridge<T, R> bridge;
	private final PojoModelElementAccessor<? extends T> pojoModelElementAccessor;
	private final IndexFieldAccessor<R> indexFieldAccessor;

	public FunctionBridgeValueProcessor(FunctionBridge<T, R> bridge,
			PojoModelElementAccessor<? extends T> pojoModelElementAccessor,
			IndexFieldAccessor<R> indexFieldAccessor) {
		this.bridge = bridge;
		this.pojoModelElementAccessor = pojoModelElementAccessor;
		this.indexFieldAccessor = indexFieldAccessor;
	}

	@Override
	public void process(DocumentElement target, PojoElement source) {
		T bridgedElement = pojoModelElementAccessor.read( source );
		R indexFieldValue = bridge.toIndexedValue( bridgedElement );
		indexFieldAccessor.write( target, indexFieldValue );
	}

	@Override
	public void close() {
		bridge.close();
	}

}
