/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.backend.elasticsearch.util;

import org.hibernate.search.v6poc.integrationtest.backend.tck.util.TckBackendFeatures;

public class ElasticsearchTckBackendFeatures extends TckBackendFeatures {

	@Override
	public boolean localDateTypeOnMissingValueUse() {
		// See https://hibernate.atlassian.net/browse/HSEARCH-3255
		return false;
	}

}
