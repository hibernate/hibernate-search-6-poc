/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.search.v6poc.entity.javabean.log.impl;

import org.hibernate.search.v6poc.entity.pojo.logging.spi.PojoTypeModelFormatter;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoRawTypeModel;
import org.hibernate.search.v6poc.util.SearchException;
import org.hibernate.search.v6poc.util.impl.common.MessageConstants;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.annotations.FormatWith;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode = MessageConstants.PROJECT_CODE)
@ValidIdRange(min = MessageConstants.MAPPER_JAVABEAN_ID_RANGE_MIN, max = MessageConstants.MAPPER_JAVABEAN_ID_RANGE_MAX)
public interface Log extends BasicLogger {

	int ID_OFFSET_1 = MessageConstants.MAPPER_JAVABEAN_ID_RANGE_MIN;

	@Message(id = ID_OFFSET_1 + 1,
			value = "Unable to find property '%2$s' on type '%1$s'.")
	SearchException cannotFindProperty(@FormatWith(PojoTypeModelFormatter.class) PojoRawTypeModel<?> typeModel,
			String propertyName);

	@Message(id = ID_OFFSET_1 + 2,
			value = "Cannot read property '%2$s' on type '%1$s'.")
	SearchException cannotReadProperty(@FormatWith(PojoTypeModelFormatter.class) PojoRawTypeModel<?> typeModel,
			String propertyName);

}
