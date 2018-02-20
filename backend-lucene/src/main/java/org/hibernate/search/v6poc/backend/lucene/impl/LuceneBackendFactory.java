/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.impl;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.hibernate.search.v6poc.backend.lucene.logging.impl.Log;
import org.hibernate.search.v6poc.backend.lucene.work.impl.StubLuceneWorkFactory;
import org.hibernate.search.v6poc.backend.spi.Backend;
import org.hibernate.search.v6poc.backend.spi.BackendFactory;
import org.hibernate.search.v6poc.cfg.ConfigurationPropertySource;
import org.hibernate.search.v6poc.cfg.spi.ConfigurationProperty;
import org.hibernate.search.v6poc.engine.spi.BuildContext;
import org.hibernate.search.v6poc.util.spi.LoggerFactory;


/**
 * @author Guillaume Smet
 */
public class LuceneBackendFactory implements BackendFactory {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private static final ConfigurationProperty<Optional<String>> DIRECTORY_PROVIDER = ConfigurationProperty.forKey( "lucene.directory_provider" )
			.asString()
			.build();

	// TODO GSM: probably better to introduce a parser for Paths
	private static final ConfigurationProperty<Optional<String>> ROOT_DIRECTORY = ConfigurationProperty.forKey( "lucene.root_directory" )
			.asString()
			.build();

	@Override
	public Backend<?> create(String name, BuildContext context, ConfigurationPropertySource propertySource) {
		// TODO be more clever about the type, also supports providing a class
		Optional<String> directoryProviderProperty = DIRECTORY_PROVIDER.get( propertySource );

		if ( !directoryProviderProperty.isPresent() ) {
			throw log.undefinedLuceneDirectoryProvider( name );
		}

		String directoryProvider = directoryProviderProperty.get();

		if ( "local_directory".equals( directoryProvider ) ) {
			// TODO GSM: implement the checks properly
			Path rootDirectory = Paths.get( ROOT_DIRECTORY.get( propertySource ).get() ).toAbsolutePath();

			return new LuceneLocalDirectoryBackend( name, rootDirectory, new StubLuceneWorkFactory() );
		}

		throw log.unrecognizedLuceneDirectoryProvider( name, directoryProvider );
	}
}
