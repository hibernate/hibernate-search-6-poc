/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.search.v6poc.backend.elasticsearch.logging.impl;

import java.util.Collection;
import java.util.Map;

import org.hibernate.search.v6poc.backend.elasticsearch.util.impl.URLEncodedString;
import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.esnative.DataType;
import org.hibernate.search.v6poc.backend.elasticsearch.index.impl.ElasticsearchIndexManager;
import org.hibernate.search.v6poc.backend.elasticsearch.types.codec.impl.ElasticsearchFieldCodec;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTargetBuilder;
import org.hibernate.search.v6poc.backend.spi.BackendImplementor;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.SearchSort;
import org.hibernate.search.v6poc.util.SearchException;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "HSEARCH-ES")
public interface Log extends BasicLogger {

	// -----------------------
	// Pre-existing messages
	// -----------------------

	@LogMessage(level = Level.WARN)
	@Message(id = /*ES_BACKEND_MESSAGES_START_ID +*/ 73,
			value = "Hibernate Search will connect to Elasticsearch server '%1$s' with authentication over plain HTTP (not HTTPS)."
					+ " The password will be sent in clear text over the network."
	)
	void usingPasswordOverHttp(String serverUris);

	@LogMessage(level = Level.DEBUG)
	@Message(id = /*ES_BACKEND_MESSAGES_START_ID +*/ 82,
			value = "Executed Elasticsearch HTTP %s request to path '%s' with query parameters %s in %dms."
					+ " Response had status %d '%s'."
	)
	void executedRequest(String method, String path, Map<String, String> getParameters, long timeInMs,
			int responseStatusCode, String responseStatusMessage);

	@Message(id = /*ES_BACKEND_MESSAGES_START_ID +*/ 89,
			value = "Failed to parse Elasticsearch response. Status code was '%1$d', status phrase was '%2$s'." )
	SearchException failedToParseElasticsearchResponse(int statusCode, String statusPhrase, @Cause Exception cause);

	@LogMessage(level = Level.TRACE)
	@Message(id = /*ES_BACKEND_MESSAGES_START_ID +*/ 93,
			value = "Executed Elasticsearch HTTP %s request to path '%s' with query parameters %s in %dms."
					+ " Response had status %d '%s'. Request body: <%s>. Response body: <%s>"
	)
	void executedRequest(String method, String path, Map<String, String> getParameters, long timeInMs,
			int responseStatusCode, String responseStatusMessage,
			String requestBodyParts, String responseBody);

	// -----------------------
	// New messages
	// -----------------------

	@Message(id = 502, value = "A search query cannot target both an Elasticsearch index and other types of index."
			+ " First target was: '%1$s', other target was: '%2$s'" )
	SearchException cannotMixElasticsearchSearchTargetWithOtherType(IndexSearchTargetBuilder firstTarget, ElasticsearchIndexManager otherTarget);

	@Message(id = 503, value = "A search query cannot target multiple Elasticsearch backends."
			+ " First target was: '%1$s', other target was: '%2$s'" )
	SearchException cannotMixElasticsearchSearchTargetWithOtherBackend(IndexSearchTargetBuilder firstTarget, ElasticsearchIndexManager otherTarget);

	@Message(id = 504, value = "Unknown field '%1$s' in indexes %2$s." )
	SearchException unknownFieldForSearch(String absoluteFieldPath, Collection<URLEncodedString> indexNames);

	@Message(id = 505, value = "Multiple conflicting types for field '%1$s': '%2$s' in index '%3$s', but '%4$s' in index '%5$s'." )
	SearchException conflictingFieldCodecsForSearch(String absoluteFieldPath,
			ElasticsearchFieldCodec codec1, URLEncodedString indexName1,
			ElasticsearchFieldCodec codec2, URLEncodedString indexName2);

	@Message(id = 506, value = "The Elasticsearch extension can only be applied to objects"
			+ " derived from the Elasticsearch backend. Was applied to '%1$s' instead." )
	SearchException elasticsearchExtensionOnUnknownType(Object context);

	@Message(id = 507, value = "Unknown projections %1$s in indexes %2$s." )
	SearchException unknownProjectionForSearch(Collection<String> projections, Collection<URLEncodedString> indexNames);

	@Message(id = 508, value = "An Elasticsearch query cannot include search predicates built using a non-Elasticsearch search target."
			+ " Given predicate was: '%1$s'" )
	SearchException cannotMixElasticsearchSearchQueryWithOtherPredicates(SearchPredicate predicate);

	@Message(id = 509, value = "Field '%2$s' is not an object field in index '%1$s'." )
	SearchException nonObjectFieldForNestedQuery(URLEncodedString indexName, String absoluteFieldPath);

	@Message(id = 510, value = "Object field '%2$s' is not stored as nested in index '%1$s'." )
	SearchException nonNestedFieldForNestedQuery(URLEncodedString indexName, String absoluteFieldPath);

	@Message(id = 511, value = "An Elasticsearch query cannot include search sorts built using a non-Elasticsearch search target."
			+ " Given sort was: '%1$s'" )
	SearchException cannotMixElasticsearchSearchSortWithOtherSorts(SearchSort sort);

	@Message(id = 512, value = "An analyzer was set on field '%1$s' of type '%2$s', but fields of this type cannot be analyzed." )
	SearchException cannotUseAnalyzerOnFieldType(String absoluteFieldPath, DataType fieldType);

	@Message(id = 513, value = "A normalizer was set on field '%1$s' of type '%2$s', but fields of this type cannot be analyzed." )
	SearchException cannotUseNormalizerOnFieldType(String absoluteFieldPath, DataType fieldType);

	@Message(id = 514, value = "Index '%2$s' requires multi-tenancy but backend '%1$s' does not support it in its current configuration.")
	SearchException multiTenancyRequiredButNotSupportedByBackend(String backendName, String indexName);

	@Message(id = 515, value = "Unknown multi-tenancy strategy '%1$s'.")
	SearchException unknownMultiTenancyStrategyConfiguration(String multiTenancyStrategy);

	@Message(id = 516, value = "Tenant identifier '%2$s' is provided, but multi-tenancy is disabled for the backend '%1$s'.")
	SearchException tenantIdProvidedButMultiTenancyDisabled(BackendImplementor<?> backend, String tenantId);

	@Message(id = 517, value = "Backend '%1$s' has multi-tenancy enabled, but no tenant identifier is provided.")
	SearchException multiTenancyEnabledButNoTenantIdProvided(BackendImplementor<?> backend);

	@Message(id = 518, value = "Attempt to unwrap the Elasticsearch low-level client to %1$s,"
			+ " but the client can only be unwrapped to %2$s." )
	SearchException clientUnwrappingWithUnkownType(Class<?> requestedClass, Class<?> actualClass);

	@Message(id = 519, value = "Attempt to unwrap an Elasticsearch backend to %1$s,"
			+ " but this backend can only be unwrapped to %2$s." )
	SearchException backendUnwrappingWithUnknownType(Class<?> requestedClass, Class<?> actualClass);

	@Message(id = 520, value = "The index schema node '%2$s' was added twice at path '%1$s'."
			+ " Multiple bridges may be trying to access the same index field, "
			+ " or two indexed-embeddeds may have prefixes that lead to conflicting field names,"
			+ " or you may have declared multiple conflicting mappings."
			+ " In any case, there is something wrong with your mapping and you should fix it." )
	SearchException indexSchemaNodeNameConflict(String absolutePath, String name);

	@Message(id = 521, value = "Field name '%1$s' is invalid: field names cannot be null or empty." )
	SearchException relativeFieldNameCannotBeNullOrEmpty(String relativeFieldName);

	@Message(id = 522, value = "Field name '%1$s' is invalid: field names cannot contain a dot ('.')."
			+ " Remove the dot from your field name,"
			+ " or if you are declaring the field in a bridge and want a tree of fields,"
			+ " declare an object field using the objectField() method." )
	SearchException relativeFieldNameCannotContainDot(String relativeFieldName);
}
