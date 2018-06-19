/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.search.predicate.impl;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.search.v6poc.backend.elasticsearch.gson.impl.JsonAccessor;
import org.hibernate.search.v6poc.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.v6poc.search.predicate.spi.BooleanJunctionPredicateBuilder;
import org.hibernate.search.v6poc.util.impl.common.LoggerFactory;

import com.google.gson.JsonObject;


/**
 * @author Yoann Rodiere
 */
class BooleanJunctionPredicateBuilderImpl extends AbstractSearchPredicateBuilder
		implements BooleanJunctionPredicateBuilder<ElasticsearchSearchPredicateCollector> {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final JsonAccessor<JsonObject> MUST = JsonAccessor.root().property( "must" ).asObject();
	private static final JsonAccessor<JsonObject> MUST_NOT = JsonAccessor.root().property( "must_not" ).asObject();
	private static final JsonAccessor<JsonObject> SHOULD = JsonAccessor.root().property( "should" ).asObject();
	private static final JsonAccessor<JsonObject> FILTER = JsonAccessor.root().property( "filter" ).asObject();

	private static final JsonAccessor<String> MINIMUM_SHOULD_MATCH =
			JsonAccessor.root().property( "minimum_should_match" ).asString();

	private Map<Integer, MinimumShouldMatchConstraint> minimumShouldMatchConstraints;

	@Override
	public ElasticsearchSearchPredicateCollector getMustCollector() {
		return this::must;
	}

	@Override
	public ElasticsearchSearchPredicateCollector getMustNotCollector() {
		return this::mustNot;
	}

	@Override
	public ElasticsearchSearchPredicateCollector getShouldCollector() {
		return this::should;
	}

	@Override
	public ElasticsearchSearchPredicateCollector getFilterCollector() {
		return this::filter;
	}

	@Override
	public void minimumShouldMatchNumber(int ignoreConstraintCeiling, int matchingClausesNumber) {
		addMinimumShouldMatchConstraint(
				ignoreConstraintCeiling,
				new MinimumShouldMatchConstraint( matchingClausesNumber, null )
		);
	}

	@Override
	public void minimumShouldMatchRatio(int ignoreConstraintCeiling, double matchingClausesRatio) {
		addMinimumShouldMatchConstraint(
				ignoreConstraintCeiling,
				new MinimumShouldMatchConstraint( null, matchingClausesRatio )
		);
	}

	private void addMinimumShouldMatchConstraint(int ignoreConstraintCeiling,
			MinimumShouldMatchConstraint constraint) {
		if ( minimumShouldMatchConstraints == null ) {
			// We'll need to go through the data in ascending order, so use a TreeMap
			minimumShouldMatchConstraints = new TreeMap<>();
		}
		Object previous = minimumShouldMatchConstraints.put( ignoreConstraintCeiling, constraint );
		if ( previous != null ) {
			throw log.minimumShouldMatchConflictingConstraints( ignoreConstraintCeiling );
		}
	}

	private void must(JsonObject query) {
		MUST.add( getInnerObject(), query );
	}

	private void mustNot(JsonObject query) {
		MUST_NOT.add( getInnerObject(), query );
	}

	private void should(JsonObject query) {
		SHOULD.add( getInnerObject(), query );
	}

	private void filter(JsonObject query) {
		FILTER.add( getInnerObject(), query );
	}

	@Override
	public void contribute(ElasticsearchSearchPredicateCollector collector) {
		JsonObject innerObject = getInnerObject();
		if ( minimumShouldMatchConstraints != null ) {
			MINIMUM_SHOULD_MATCH.set(
					innerObject,
					formatMinimumShouldMatchConstraints( minimumShouldMatchConstraints )
			);
		}

		JsonObject outerObject = getOuterObject();
		outerObject.add( "bool", innerObject );
		collector.collectPredicate( outerObject );
	}

	private String formatMinimumShouldMatchConstraints(Map<Integer, MinimumShouldMatchConstraint> minimumShouldMatchConstraints) {
		StringBuilder builder = new StringBuilder();
		Iterator<Map.Entry<Integer, MinimumShouldMatchConstraint>> iterator =
				minimumShouldMatchConstraints.entrySet().iterator();

		// Process the first constraint differently
		Map.Entry<Integer, MinimumShouldMatchConstraint> entry = iterator.next();
		Integer ignoreConstraintCeiling = entry.getKey();
		MinimumShouldMatchConstraint constraint = entry.getValue();
		if ( ignoreConstraintCeiling.equals( 0 ) && minimumShouldMatchConstraints.size() == 1 ) {
			// Special case: if there's only one constraint and its ignore ceiling is 0, do not mention the ceiling
			constraint.appendTo( builder, null );
			return builder.toString();
		}
		else {
			entry.getValue().appendTo( builder, ignoreConstraintCeiling );
		}

		// Process the other constraints normally
		while ( iterator.hasNext() ) {
			entry = iterator.next();
			ignoreConstraintCeiling = entry.getKey();
			constraint = entry.getValue();
			builder.append( ' ' );
			constraint.appendTo( builder, ignoreConstraintCeiling );
		}

		return builder.toString();
	}

	private static final class MinimumShouldMatchConstraint {
		private final Integer matchingClausesNumber;
		private final Double matchingClausesRatio;

		MinimumShouldMatchConstraint(Integer matchingClausesNumber, Double matchingClausesRatio) {
			this.matchingClausesNumber = matchingClausesNumber;
			this.matchingClausesRatio = matchingClausesRatio;
		}

		/**
		 * Format the constraint according to
		 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html">
		 * the format specified in the Elasticsearch documentation
		 * </a>.
		 *
		 * @param builder The builder to append the formatted value to.
		 * @param ignoreConstraintCeiling The ceiling above which this constraint is no longer ignored.
		 */
		void appendTo(StringBuilder builder, Integer ignoreConstraintCeiling) {
			if ( ignoreConstraintCeiling != null ) {
				builder.append( ignoreConstraintCeiling ).append( '<' );
			}
			if ( matchingClausesNumber != null ) {
				builder.append( matchingClausesNumber );
			}
			else {
				builder.append( matchingClausesRatio * 100.0 ).append( '%' );
			}
		}
	}

}
