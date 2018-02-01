/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.util.common;

import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.search.v6poc.backend.elasticsearch.cfg.ElasticsearchBackendSettings;
import org.hibernate.search.v6poc.backend.elasticsearch.client.impl.ElasticsearchClientFactory;
import org.hibernate.search.v6poc.backend.elasticsearch.client.impl.ElasticsearchClientImplementor;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.cfg.spi.ConfigurationProperty;

public class StubElasticsearchClientFactory implements ElasticsearchClientFactory {

	private static final ConfigurationProperty<List<String>> HOST =
			ConfigurationProperty.forKey( ElasticsearchBackendSettings.HOST )
					.asString().multivalued( Pattern.compile( "\\s+" ) )
					.withDefault( ElasticsearchBackendSettings.Defaults.HOST )
					.build();

	@Override
	public ElasticsearchClientImplementor create(ConfigurationPropertySource propertySource) {
		return new StubElasticsearchClient( HOST.get( propertySource ) );
	}
}
