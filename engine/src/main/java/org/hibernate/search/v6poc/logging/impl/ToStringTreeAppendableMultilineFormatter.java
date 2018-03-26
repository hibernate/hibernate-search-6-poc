/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.logging.impl;

import org.hibernate.search.v6poc.util.spi.ToStringStyle;
import org.hibernate.search.v6poc.util.spi.ToStringTreeAppendable;
import org.hibernate.search.v6poc.util.spi.ToStringTreeBuilder;

/**
 * Used with JBoss Logging's {@link org.jboss.logging.annotations.FormatWith} to display
 * {@link org.hibernate.search.v6poc.util.spi.ToStringTreeAppendable} objects in log messages.
 */
public class ToStringTreeAppendableMultilineFormatter {

	private final String stringRepresentation;

	public ToStringTreeAppendableMultilineFormatter(ToStringTreeAppendable appendable) {
		this.stringRepresentation = new ToStringTreeBuilder( ToStringStyle.MULTILINE ).value( appendable ).toString();
	}

	@Override
	public String toString() {
		return stringRepresentation;
	}
}
