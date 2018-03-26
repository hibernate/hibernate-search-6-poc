/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.spi;

public class ToStringStyle {
	public static ToStringStyle INLINE = new ToStringStyle( " ", "", "," );
	public static ToStringStyle MULTILINE = new ToStringStyle( "\n", "\t", "" );

	final String newline;
	final String indent;
	final String separator;

	private ToStringStyle(String newline, String indent, String separator) {
		this.newline = newline;
		this.indent = indent;
		this.separator = separator;
	}
}
