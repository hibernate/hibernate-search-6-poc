/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.entity.pojo.model.path.impl;

import org.hibernate.search.v6poc.entity.pojo.model.path.PojoModelPathValueNode;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PojoTypeModel;
import org.hibernate.search.v6poc.entity.pojo.model.spi.PropertyHandle;

/**
 * @param <T> The type represented by this node.
 */
public abstract class BoundPojoModelPathTypeNode<T> extends BoundPojoModelPath {

	BoundPojoModelPathTypeNode() {
	}

	@Override
	public PojoTypeModel<?> getRootType() {
		BoundPojoModelPathValueNode<?, ?, ?> parent = getParent();
		if ( parent == null ) {
			return getTypeModel();
		}
		else {
			return parent.getRootType();
		}
	}

	public BoundPojoModelPathPropertyNode<T, ?> property(PropertyHandle propertyHandle) {
		return new BoundPojoModelPathPropertyNode<>(
				this, propertyHandle, getTypeModel().getProperty( propertyHandle.getName() )
		);
	}

	@Override
	public abstract BoundPojoModelPathValueNode<?, ?, ?> getParent();

	@Override
	public PojoModelPathValueNode toUnboundPath() {
		BoundPojoModelPathValueNode<?, ?, ?> parent = getParent();
		if ( parent == null ) {
			return null;
		}
		else {
			return parent.toUnboundPath();
		}
	}

	public abstract PojoTypeModel<T> getTypeModel();
}
