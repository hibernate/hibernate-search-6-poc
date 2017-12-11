/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.bridge.builtin.impl;

import java.time.LocalDate;

import org.hibernate.search.v6poc.backend.document.model.spi.FieldModelContext;
import org.hibernate.search.v6poc.backend.document.model.spi.TypedFieldModelContext;
import org.hibernate.search.v6poc.entity.pojo.bridge.spi.FunctionBridge;

public final class DefaultLocalDateFunctionBridge implements FunctionBridge<LocalDate, LocalDate> {

	@Override
	public TypedFieldModelContext<LocalDate> bind(FieldModelContext context) {
		return context.asLocalDate();
	}

	@Override
	public LocalDate toDocument(LocalDate propertyValue) {
		return propertyValue;
	}

}