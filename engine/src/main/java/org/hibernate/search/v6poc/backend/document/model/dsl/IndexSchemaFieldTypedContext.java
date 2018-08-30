/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.model.dsl;

import org.hibernate.search.v6poc.backend.document.converter.ToIndexFieldValueConverter;

/**
 * @param <F> The type of field values.
 */
public interface IndexSchemaFieldTypedContext<F> extends IndexSchemaFieldTerminalContext<F> {

	IndexSchemaFieldTypedContext<F> dslConverter(ToIndexFieldValueConverter<?, ? extends F> toIndexConverter);

}
