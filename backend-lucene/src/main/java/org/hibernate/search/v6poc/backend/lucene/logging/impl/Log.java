/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.logging.impl;

import java.nio.file.Path;
import java.util.List;

import org.hibernate.search.v6poc.util.SearchException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "HSEARCH-LUCENE")
public interface Log extends BasicLogger {

	@Message(id = 4, value = "Unknown field '%1$s' in indexes %2$s." )
	SearchException unknownFieldForSearch(String absoluteFieldPath, List<String> indexNames);

	@Message(id = 5, value = "Root directory '%2$s' of backend '%1$s' exists but is not a writable directory.")
	SearchException localDirectoryBackendRootDirectoryNotWritableDirectory(String backendName, Path rootDirectory);

	@Message(id = 6, value = "Unable to create root directory '%2$s' for backend '%1$s'.")
	SearchException unableToCreateRootDirectoryForLocalDirectoryBackend(String backendName, Path rootDirectory, @Cause Exception e);

	@Message(id = 7, value = "Undefined Lucene backend type for backend '%1$s'.")
	SearchException undefinedLuceneBackendType(String backendName);

	@Message(id = 8, value = "Unrecognized Lucene backend type '%2$s' for backend '%1$s'.")
	SearchException unrecognizedLuceneBackendType(String backendName, String backendType);

	@Message(id = 12, value = "An analyzer was set on field '%1$s', but fields of this type cannot be analyzed." )
	SearchException cannotUseAnalyzerOnFieldType(String fieldName);

	@Message(id = 13, value = "A normalizer was set on field '%1$s', but fields of this type cannot be analyzed." )
	SearchException cannotUseNormalizerOnFieldType(String fieldName);

	@Message(id = 14, value = "Cannot use an analyzer on field '%1$s' because it is sortable." )
	SearchException cannotUseAnalyzerOnSortableField(String fieldName);

	@Message(id = 15, value = "Could not analyze sortable field '%1$s'.")
	SearchException couldNotAnalyzeSortableField(String fieldName, @Cause Exception cause);

	@LogMessage(level = Level.WARN)
	@Message(id = 16, value = "The analysis of field '%1$s' produced multiple tokens. Tokenization or term generation"
			+ " (synonyms) should not be used on sortable fields. Only the first token will be indexed.")
	void multipleTermsInAnalyzedSortableField(String fieldName);

	@Message(id = 22, value = "Unable to create the IndexWriter for backend '%1$s', index '%2$s' and path '%3$s'." )
	SearchException unableToCreateIndexWriter(String backendName, String indexName, Path directoryPath, @Cause Exception e);

	@Message(id = 23, value = "Unable to index entry '%2$s' for index '%1$s'." )
	SearchException unableToIndexEntry(String indexName, String id);

	@Message(id = 24, value = "Unable to delete entry '%2$s' from index '%1$s'." )
	SearchException unableToDeleteEntryFromIndex(String indexName, String id);

	@Message(id = 25, value = "Unable to flush index '%1$s'." )
	SearchException unableToFlushIndex(String indexName);

	@Message(id = 26, value = "Unable to commit index '%1$s'." )
	SearchException unableToCommitIndex(String indexName);

	@Message(id = 27, value = "Index directory '%2$s' of backend '%1$s' exists but is not a writable directory.")
	SearchException localDirectoryIndexRootDirectoryNotWritableDirectory(String backendName, Path indexDirectory);

	@Message(id = 28, value = "Unable to create index root directory '%2$s' for backend '%1$s'.")
	SearchException unableToCreateIndexRootDirectoryForLocalDirectoryBackend(String backendName, Path indexDirectory, @Cause Exception e);
}
