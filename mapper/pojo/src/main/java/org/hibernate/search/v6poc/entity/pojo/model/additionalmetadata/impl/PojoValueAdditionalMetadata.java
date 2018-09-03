/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.additionalmetadata.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.hibernate.search.v6poc.entity.pojo.dirtiness.ReindexOnUpdate;
import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPathValueNode;

public class PojoValueAdditionalMetadata {

	public static final PojoValueAdditionalMetadata EMPTY = new PojoValueAdditionalMetadata(
			null, false, Optional.empty(), Collections.emptySet()
	);

	private final PojoModelPathValueNode inverseSidePath;
	private final boolean associationEmbedded;
	private final Optional<ReindexOnUpdate> reindexOnUpdate;
	private final Set<PojoModelPathValueNode> derivedFrom;

	public PojoValueAdditionalMetadata(PojoModelPathValueNode inverseSidePath, boolean associationEmbedded,
			Optional<ReindexOnUpdate> reindexOnUpdate, Set<PojoModelPathValueNode> derivedFrom) {
		this.inverseSidePath = inverseSidePath;
		this.associationEmbedded = associationEmbedded;
		this.reindexOnUpdate = reindexOnUpdate;
		this.derivedFrom = derivedFrom;
	}

	public Optional<PojoModelPathValueNode> getInverseSidePath() {
		return Optional.ofNullable( inverseSidePath );
	}

	public boolean isAssociationEmbedded() {
		return associationEmbedded;
	}

	public Optional<ReindexOnUpdate> getReindexOnUpdate() {
		return reindexOnUpdate;
	}

	public Set<PojoModelPathValueNode> getDerivedFrom() {
		return derivedFrom;
	}
}
