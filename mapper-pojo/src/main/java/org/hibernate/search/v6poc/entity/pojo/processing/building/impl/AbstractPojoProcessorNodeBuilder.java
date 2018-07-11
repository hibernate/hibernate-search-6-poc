/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.processing.building.impl;

import org.hibernate.search.v6poc.entity.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.v6poc.entity.pojo.logging.spi.PojoEventContexts;
import org.hibernate.search.v6poc.entity.pojo.mapping.building.impl.PojoMappingHelper;
import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPath;
import org.hibernate.search.v6poc.entity.pojo.model.path.impl.BoundPojoModelPath;
import org.hibernate.search.v6poc.logging.spi.ContextualFailureCollector;

abstract class AbstractPojoProcessorNodeBuilder {

	final PojoMappingHelper mappingHelper;
	final IndexModelBindingContext bindingContext;

	AbstractPojoProcessorNodeBuilder(
			PojoMappingHelper mappingHelper, IndexModelBindingContext bindingContext) {
		this.mappingHelper = mappingHelper;
		this.bindingContext = bindingContext;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getModelPath() + "]";
	}

	abstract BoundPojoModelPath getModelPath();

	abstract void closeOnFailure();

	public final ContextualFailureCollector getFailureCollector() {
		BoundPojoModelPath modelPath = getModelPath();

		ContextualFailureCollector failureCollector = mappingHelper.getFailureCollector()
				.withContext(
						PojoEventContexts.fromType( modelPath.getRootType().getRawType() )
				);

		PojoModelPath unboundPath = modelPath.toUnboundPath();
		if ( unboundPath != null ) {
			failureCollector = failureCollector.withContext(
					PojoEventContexts.fromPath( modelPath.toUnboundPath() )
			);
		}

		return failureCollector;
	}
}
