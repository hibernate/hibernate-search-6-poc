/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative;

import com.google.gson.annotations.JsonAdapter;

/**
 * An enum for Elasticsearch "_routing" attribute values.
 *
 * See https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-routing-field.html
 * @author Yoann Rodiere
 */
@JsonAdapter( ElasticsearchRoutingTypeJsonAdapter.class )
public enum RoutingType {

	REQUIRED,
	OPTIONAL,
	;
}
