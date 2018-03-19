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
import org.hibernate.search.v6poc.integrationtest.showcase.library.bridge.BookMediumBridge;

/**
 * A concrete copy of a book document.
 *
 * @see DocumentCopy
 */
@Entity
public class BookCopy extends DocumentCopy<Book> {

	// TODO add default bridge (and maybe field type?) for enums
	@Field(valueBridge = @ValueBridgeBeanReference(type = BookMediumBridge.class))
	// TODO facet
	private BookMedium medium;

	public BookMedium getMedium() {
		return medium;
	}

	public void setMedium(BookMedium medium) {
		this.medium = medium;
	}
}
