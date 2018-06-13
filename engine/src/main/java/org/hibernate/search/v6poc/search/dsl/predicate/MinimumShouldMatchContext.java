/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.search.dsl.predicate;

/**
 * The context used when defining "minimum should match" constraints.
 * <p>
 * <h3 id="minimumshouldmatch">"minimumShouldMatch" constraints</h3>
 * <p>
 * "minimumShouldMatch" constraints define a minimum number of "should" clauses that have to match
 * in order for the boolean predicate to match.
 * <p>
 * The feature is similar, and will work identically, to
 * <a href="https://lucene.apache.org/solr/7_3_0/solr-core/org/apache/solr/util/doc-files/min-should-match.html">"Min Number Should Match"</a>
 * in Solr or
 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html">{@code minimum_should_match}</a>
 * in Elasticsearch.
 *
 * <h4 id="minimumshouldmatch-minimum">Definition of the minimum</h4>
 * <p>
 * The minimum may be defined either directly as a positive number, or indirectly as a negative number
 * or positive or negative percentage representing a ratio of the total number of "should" clauses in this boolean predicate.
 * <p>
 * Here is how each type of input is interpreted:
 * <dl>
 *     <dt>Positive number</dt>
 *     <dd>
 *         The value is interpreted directly as the minimum number of "should" clauses that have to match.
 *     </dd>
 *     <dt>Negative number</dt>
 *     <dd>
 *         The absolute value is interpreted as the maximum number of "should" clauses that may not match:
 *         the absolute value is subtracted from the total number of "should" clauses.
 *     </dd>
 *     <dt>Positive percentage</dt>
 *     <dd>
 *         The value is interpreted as the minimum percentage of the total number of "should" clauses that have to match:
 *         the percentage is applied to the total number of "should" clauses, then rounded down.
 *     </dd>
 *     <dt>Negative percentage</dt>
 *     <dd>
 *         The absolute value is interpreted as the maximum percentage of the total number of "should" clauses that may not match:
 *         the absolute value of the percentage is applied to the total number of "should" clauses, then rounded down,
 *         then subtracted from the total number of "should" clauses.
 *     </dd>
 * </dl>
 * <p>
 * In any case, if the computed minimum is 0 or less, or higher than the total number of "should" clauses,
 * behavior is backend-specific (it may throw an exception, or produce unpredictable results,
 * or fall back to some default behavior).
 *
 * <h4 id="minimumshouldmatch-conditionalconstraints">Conditional constraints</h4>
 * <p>
 * Multiple conditional constraints may be defined,
 * only one of them being applied depending on the total number of "should" clauses.
 * <p>
 * Each constraint is attributed a minimum number of "should" clauses
 * that have to match, <strong>and an additional number</strong>.
 * The additional number is unique to each constraint.
 * <p>
 * The additional number will be compared to the total number of "should" clauses,
 * and the one closest while still strictly lower will be picked: its associated constraint will be applied.
 * If no number matches, the minimum number of matching "should" clauses
 * will be set to the total number of "should" clauses.
 * <p>
 * Examples:
 * <pre><code>
 *     // Example 1: at least 3 "should" clauses have to match
 *     booleanContext1.minimumShouldMatchNumber( 3 );
 *     // Example 2: at most 2 "should" clauses may not match
 *     booleanContext2.minimumShouldMatchNumber( -2 );
 *     // Example 3: at least 75% of "should" clauses have to match (rounded down)
 *     booleanContext3.minimumShouldMatchPercent( 75 );
 *     // Example 4: at most 25% of "should" clauses may not match (rounded down)
 *     booleanContext4.minimumShouldMatchPercent( -25 );
 *     // Example 5: if there are 3 "should" clauses or less, all "should" clauses have to match.
 *     // If there are 4 "should" clauses or more, at least 90% of "should" clauses have to match (rounded down).
 *     booleanContext5.minimumShouldMatchPercent( 3, 90 );
 *     // Example 6: if there are 4 "should" clauses or less, all "should" clauses have to match.
 *     // If there are 5 to 9 "should" clauses, at most 25% of "should" clauses may not match (rounded down).
 *     // If there are 10 "should" clauses or more, at most 3 "should" clauses may not match.
 *     booleanContext6.minimumShouldMatch()
 *             .ifMoreThan( 4 ).thenRequirePercent( 25 )
 *             .ifMoreThan( 9 ).thenRequireNumber( -3 )
 *             .end();
 * </code></pre>
 *
 * @param <N> The type of the next context (returned by {@link MinimumShouldMatchNonEmptyContext#end()}).
 */
public interface MinimumShouldMatchContext<N> {

	/**
	 * Start adding a <a href="#minimumshouldmatch">"minimumShouldMatch" constraint</a> that will be applied
	 * if (and only if) there are strictly more than {@code ignoreConstraintCeiling} "should" clauses.
	 * <p>
	 * See <a href="#minimumshouldmatch-conditionalconstraints">Conditional constraints</a> for the detailed rules
	 * defining whether a constraint is applied or not.
	 *
	 * @param ignoreConstraintCeiling The number of "should" clauses above which the constraint
	 * will cease to be ignored.
	 * @return A context allowing to define the constraint to apply when there are strictly more
	 * than {@code ignoreConstraintCeiling} "should" clauses.
	 */
	MinimumShouldMatchConditionContext<N> ifMoreThan(int ignoreConstraintCeiling);

}
