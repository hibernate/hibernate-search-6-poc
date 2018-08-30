/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.document.model.dsl;

import org.hibernate.search.v6poc.backend.document.converter.FromIndexFieldValueConverter;
import org.hibernate.search.v6poc.backend.document.converter.ToIndexFieldValueConverter;

/**
 * @param <F> The type of field values.
 */
public interface StandardIndexSchemaFieldTypedContext<F> extends IndexSchemaFieldTypedContext<F> {

	@Override
	StandardIndexSchemaFieldTypedContext<F> dslConverter(ToIndexFieldValueConverter<?, ? extends F> toIndexConverter);

	@Override
	StandardIndexSchemaFieldTypedContext<F> projectionConverter(FromIndexFieldValueConverter<? super F, ?> fromIndexConverter);

	// TODO add common options: stored, sortable, ...

	StandardIndexSchemaFieldTypedContext<F> analyzer(String analyzerName);

	StandardIndexSchemaFieldTypedContext<F> normalizer(String normalizerName);

	StandardIndexSchemaFieldTypedContext<F> store(Store store);

	StandardIndexSchemaFieldTypedContext<F> sortable(Sortable sortable);

}
