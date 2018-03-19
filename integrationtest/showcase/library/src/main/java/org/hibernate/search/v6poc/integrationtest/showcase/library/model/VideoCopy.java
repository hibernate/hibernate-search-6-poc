/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.integrationtest.showcase.library.model;

import javax.persistence.Entity;

import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.Field;
import org.hibernate.search.v6poc.entity.pojo.mapping.definition.annotation.ValueBridgeBeanReference;
import org.hibernate.search.v6poc.integrationtest.showcase.library.bridge.VideoMediumBridge;

/**
 * A concrete copy of a video document.
 *
 * @see DocumentCopy
 */
@Entity
public class VideoCopy extends DocumentCopy<Video> {

	// TODO add default bridge (and maybe field type?) for enums
	@Field(valueBridge = @ValueBridgeBeanReference(type = VideoMediumBridge.class))
	// TODO facet
	private VideoMedium medium;

	public VideoMedium getMedium() {
		return medium;
	}

	public void setMedium(VideoMedium medium) {
		this.medium = medium;
	}
}
