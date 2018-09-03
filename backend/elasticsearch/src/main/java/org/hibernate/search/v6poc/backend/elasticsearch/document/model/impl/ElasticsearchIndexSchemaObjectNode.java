/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.elasticsearch.document.model.impl;

import org.hibernate.search.v6poc.backend.document.model.dsl.ObjectFieldStorage;

/**
 * @author Yoann Rodiere
 */
public class ElasticsearchIndexSchemaObjectNode {

	private static final ElasticsearchIndexSchemaObjectNode ROOT =
			new ElasticsearchIndexSchemaObjectNode( null, null, null );

	public static ElasticsearchIndexSchemaObjectNode root() {
		return ROOT;
	}

	private final ElasticsearchIndexSchemaObjectNode parent;

	private final String absolutePath;

	private final ObjectFieldStorage storage;

	public ElasticsearchIndexSchemaObjectNode(ElasticsearchIndexSchemaObjectNode parent, String absolutePath,
			ObjectFieldStorage storage) {
		this.parent = parent;
		this.absolutePath = absolutePath;
		this.storage = storage;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[absolutePath=" + absolutePath + "]";
	}

	public ElasticsearchIndexSchemaObjectNode getParent() {
		return parent;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getAbsolutePath(String relativeFieldName) {
		return absolutePath == null ? relativeFieldName : absolutePath + "." + relativeFieldName;
	}

	public ObjectFieldStorage getStorage() {
		return storage;
	}
}
