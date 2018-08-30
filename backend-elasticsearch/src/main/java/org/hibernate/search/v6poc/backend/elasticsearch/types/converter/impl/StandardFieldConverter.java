/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.types.converter.impl;

import org.hibernate.search.v6poc.backend.document.spi.UserIndexFieldConverter;
import org.hibernate.search.v6poc.backend.elasticsearch.types.codec.impl.ElasticsearchFieldCodec;

import com.google.gson.JsonElement;

public final class StandardFieldConverter<F> implements ElasticsearchFieldConverter {

	private final UserIndexFieldConverter<F> userConverter;
	private final ElasticsearchFieldCodec<F> codec;

	public StandardFieldConverter(UserIndexFieldConverter<F> userConverter, ElasticsearchFieldCodec<F> codec) {
		this.userConverter = userConverter;
		this.codec = codec;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + userConverter + "]";
	}

	@SuppressWarnings("unchecked")
	@Override
	public JsonElement convertFromDsl(Object value) {
		F rawValue = userConverter.convertFromDsl( value );
		return codec.encode( rawValue );
	}

	@Override
	public Object convertFromProjection(JsonElement element) {
		F rawValue = codec.decode( element );
		return userConverter.convertFromProjection( rawValue );
	}

	@Override
	public boolean isDslCompatibleWith(ElasticsearchFieldConverter other) {
		if ( !getClass().equals( other.getClass() ) ) {
			return false;
		}
		StandardFieldConverter<?> castedOther = (StandardFieldConverter<?>) other;
		return userConverter.isDslCompatibleWith( castedOther.userConverter )
				&& codec.equals( castedOther.codec );
	}

}
