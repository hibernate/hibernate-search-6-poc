/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.mapping.spi;

import java.util.Collection;

import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.entity.pojo.mapping.PojoWorkPlan;
import org.hibernate.search.v6poc.entity.pojo.mapping.PojoMapping;

public interface PojoMappingDelegate extends PojoMapping, AutoCloseable {

	@Override
	void close();

	PojoWorkPlan createWorkPlan(PojoSessionContext sessionContext);

	<T> PojoSearchTargetDelegate<T> createPojoSearchTarget(Collection<? extends Class<? extends T>> targetedTypes,
			SessionContext sessionContext);

}
