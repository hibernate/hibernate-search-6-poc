/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.sort.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaFieldNode;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchQueryElementCollector;
import org.hibernate.search.v6poc.backend.elasticsearch.search.impl.ElasticsearchSearchTargetModel;
import org.hibernate.search.v6poc.logging.spi.EventContexts;
import org.hibernate.search.v6poc.search.SearchSort;
import org.hibernate.search.v6poc.search.sort.spi.DistanceSortBuilder;
import org.hibernate.search.v6poc.search.sort.spi.FieldSortBuilder;
import org.hibernate.search.v6poc.search.sort.spi.ScoreSortBuilder;
import org.hibernate.search.v6poc.spatial.GeoPoint;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * @author Yoann Rodiere
 */
// TODO have one version of the factory per dialect, if necessary
public class SearchSortFactoryImpl implements ElasticsearchSearchSortFactory {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final Gson GSON = new GsonBuilder().create();

	private final ElasticsearchSearchTargetModel searchTargetModel;

	public SearchSortFactoryImpl(ElasticsearchSearchTargetModel searchTargetModel) {
		this.searchTargetModel = searchTargetModel;
	}

	@Override
	public SearchSort toSearchSort(List<ElasticsearchSearchSortBuilder> builders) {
		ElasticsearchSearchQueryElementCollector collector = new ElasticsearchSearchQueryElementCollector();
		for ( ElasticsearchSearchSortBuilder builder : builders ) {
			builder.buildAndAddTo( collector );
		}
		return new ElasticsearchSearchSort( collector.toJsonSort() );
	}

	@Override
	public void toImplementation(SearchSort sort, Consumer<ElasticsearchSearchSortBuilder> implementationConsumer) {
		if ( !( sort instanceof ElasticsearchSearchSort ) ) {
			throw log.cannotMixElasticsearchSearchSortWithOtherSorts( sort );
		}
		implementationConsumer.accept( (ElasticsearchSearchSort) sort );
	}

	@Override
	public void contribute(ElasticsearchSearchSortCollector collector, List<ElasticsearchSearchSortBuilder> builders) {
		for ( ElasticsearchSearchSortBuilder builder : builders ) {
			builder.buildAndAddTo( collector );
		}
	}

	@Override
	public ScoreSortBuilder<ElasticsearchSearchSortBuilder> score() {
		return new ScoreSortBuilderImpl();
	}

	@Override
	public FieldSortBuilder<ElasticsearchSearchSortBuilder> field(String absoluteFieldPath) {
		ElasticsearchIndexSchemaFieldNode<?> node = searchTargetModel.getSchemaNode( absoluteFieldPath );
		return new FieldSortBuilderImpl<>( absoluteFieldPath, node );
	}

	@Override
	public DistanceSortBuilder<ElasticsearchSearchSortBuilder> distance(String absoluteFieldPath, GeoPoint location) {
		if ( !searchTargetModel.getSchemaNode( absoluteFieldPath ).getCodec().supportsSortingByDistance() ) {
			throw log.distanceOperationsNotSupportedByFieldType(
					EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath )
			);
		}

		return new DistanceSortBuilderImpl( absoluteFieldPath, location );
	}

	@Override
	public ElasticsearchSearchSortBuilder indexOrder() {
		return IndexOrderSortContributor.INSTANCE;
	}

	@Override
	public ElasticsearchSearchSortBuilder fromJsonString(String jsonString) {
		return new UserProvidedJsonSortContributor( GSON.fromJson( jsonString, JsonObject.class ) );
	}
}
