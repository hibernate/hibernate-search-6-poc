/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl;

import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchQueryElementCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchTargetModel;
import org.hibernate.search.v6poc.search.SearchPredicate;
import org.hibernate.search.v6poc.search.predicate.spi.AllPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.BooleanJunctionPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.MatchPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.NestedPredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.RangePredicateBuilder;
import org.hibernate.search.v6poc.search.predicate.spi.SearchPredicateContributor;
import org.hibernate.search.v6poc.util.spi.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * @author Yoann Rodiere
 */
// TODO have one version of the factory per dialect, if necessary
public class SearchPredicateFactoryImpl implements ElasticsearchSearchPredicateFactory {

	private static final Log log = LoggerFactory.make( Log.class );

	private static final Gson GSON = new GsonBuilder().create();

	private final ElasticsearchSearchTargetModel searchTargetModel;

	public SearchPredicateFactoryImpl(ElasticsearchSearchTargetModel searchTargetModel) {
		this.searchTargetModel = searchTargetModel;
	}

	@Override
	public SearchPredicate toSearchPredicate(SearchPredicateContributor<ElasticsearchSearchPredicateCollector> contributor) {
		ElasticsearchSearchQueryElementCollector collector = new ElasticsearchSearchQueryElementCollector();
		contributor.contribute( collector );
		return new ElasticsearchSearchPredicate( collector.toJsonPredicate() );
	}

	@Override
	public SearchPredicateContributor<ElasticsearchSearchPredicateCollector> toContributor(SearchPredicate predicate) {
		if ( !( predicate instanceof ElasticsearchSearchPredicate ) ) {
			throw log.cannotMixElasticsearchSearchQueryWithOtherPredicates( predicate );
		}
		return (ElasticsearchSearchPredicate) predicate;
	}

	@Override
	public AllPredicateBuilder<ElasticsearchSearchPredicateCollector> all() {
		return new AllPredicateBuilderImpl();
	}

	@Override
	public BooleanJunctionPredicateBuilder<ElasticsearchSearchPredicateCollector> bool() {
		return new BooleanJunctionPredicateBuilderImpl();
	}

	@Override
	public MatchPredicateBuilder<ElasticsearchSearchPredicateCollector> match(String absoluteFieldPath) {
		return new MatchPredicateBuilderImpl( absoluteFieldPath, searchTargetModel.getFieldFormatter( absoluteFieldPath ) );
	}

	@Override
	public RangePredicateBuilder<ElasticsearchSearchPredicateCollector> range(String absoluteFieldPath) {
		return new RangePredicateBuilderImpl( absoluteFieldPath, searchTargetModel.getFieldFormatter( absoluteFieldPath ) );
	}

	@Override
	public NestedPredicateBuilder<ElasticsearchSearchPredicateCollector> nested(String absoluteFieldPath) {
		searchTargetModel.checkNestedField( absoluteFieldPath );
		return new NestedPredicateBuilderImpl( absoluteFieldPath );
	}

	@Override
	public SearchPredicateContributor<ElasticsearchSearchPredicateCollector> fromJsonString(String jsonString) {
		return new UserProvidedJsonPredicateContributor( GSON.fromJson( jsonString, JsonObject.class ) );
	}

}
