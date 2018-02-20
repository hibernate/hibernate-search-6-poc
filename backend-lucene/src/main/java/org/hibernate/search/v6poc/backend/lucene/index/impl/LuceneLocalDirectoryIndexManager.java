/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.v6poc.backend.lucene.index.impl;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.hibernate.search.v6poc.backend.index.spi.ChangesetIndexWorker;
import org.hibernate.search.v6poc.backend.index.spi.IndexSearchTargetBuilder;
import org.hibernate.search.v6poc.backend.index.spi.StreamIndexWorker;
import org.hibernate.search.v6poc.backend.lucene.document.impl.LuceneRootDocumentBuilder;
import org.hibernate.search.v6poc.backend.lucene.document.model.impl.LuceneIndexModel;
import org.hibernate.search.v6poc.backend.lucene.impl.LuceneBackend;
import org.hibernate.search.v6poc.backend.lucene.orchestration.impl.LuceneWorkOrchestrator;
import org.hibernate.search.v6poc.backend.lucene.orchestration.impl.StubLuceneWorkOrchestrator;
import org.hibernate.search.v6poc.backend.lucene.work.impl.LuceneWorkFactory;
import org.hibernate.search.v6poc.engine.spi.SessionContext;
import org.hibernate.search.v6poc.util.SearchException;
import org.hibernate.search.v6poc.util.spi.Closer;


/**
 * @author Guillaume Smet
 */
public class LuceneLocalDirectoryIndexManager implements LuceneIndexManager {

	private final LuceneBackend backend;
	private final String name;
	private final LuceneIndexModel model;
	private final LuceneWorkFactory workFactory;
	private final LuceneWorkOrchestrator changesetOrchestrator;
	private final LuceneWorkOrchestrator streamOrchestrator;
	private final IndexWriter indexWriter;

	public LuceneLocalDirectoryIndexManager(LuceneBackend backend, String name, LuceneIndexModel model, IndexWriter indexWriter) {
		this.backend = backend;
		this.name = name;
		this.model = model;
		this.workFactory = backend.getWorkFactory();
		this.changesetOrchestrator = new StubLuceneWorkOrchestrator( indexWriter );
		this.streamOrchestrator = new StubLuceneWorkOrchestrator( indexWriter );
		this.indexWriter = indexWriter;
	}

	public String getName() {
		return name;
	}

	public LuceneIndexModel getModel() {
		return model;
	}

	@Override
	public ChangesetIndexWorker<LuceneRootDocumentBuilder> createWorker(SessionContext context) {
		return new LuceneChangesetIndexWorker( workFactory, changesetOrchestrator, name, context );
	}

	@Override
	public StreamIndexWorker<LuceneRootDocumentBuilder> createStreamWorker(SessionContext context) {
		return new LuceneStreamIndexWorker( workFactory, streamOrchestrator, name, context );
	}

	@Override
	public IndexSearchTargetBuilder createSearchTarget() {
		// XXX GSM: implement Search
		throw new UnsupportedOperationException( "Search is not implemented yet" );
	}

	@Override
	public void addToSearchTarget(IndexSearchTargetBuilder searchTargetBuilder) {
		// XXX GSM: implement Search
		throw new UnsupportedOperationException( "Search is not implemented yet" );
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "name=" ).append( name )
				.append( "]")
				.toString();
	}

	@Override
	public void close() {
		try ( Closer<IOException> closer = new Closer<>() ) {
			closer.push( indexWriter::close );
			closer.push( changesetOrchestrator::close );
			closer.push( streamOrchestrator::close );
		}
		catch (IOException | RuntimeException e) {
			throw new SearchException( "Failed to shut down the Lucene index manager", e );
		}
	}
}
