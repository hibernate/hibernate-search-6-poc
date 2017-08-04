/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.util.spi;

import org.jboss.logging.Logger;

public final class LoggerFactory {

	private LoggerFactory() {
		//not allowed
	}

	public static <T> T make(Class<T> logClass) {
		Throwable t = new Throwable();
		StackTraceElement directCaller = t.getStackTrace()[1];
		return Logger.getMessageLogger( logClass, directCaller.getClassName() );
	}

}
