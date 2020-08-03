package org.healthnlp.deepphe.neo4j.embedded;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.impl.store.kvstore.RotationTimeoutException;
import org.neo4j.kernel.impl.transaction.log.files.LogFiles;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.kernel.lifecycle.LifecycleException;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A shutdown hook for the Neo4j instance so that it
 * shuts down nicely when the VM exits (even if you "Ctrl-C" the
 * running application).
 * @author SPF , chip-nlp
 * @version %I%
 * @since 8/3/2020
 */
public class ShutdownHook extends Thread {

   final GraphDatabaseService _graphDb;
   final String _directory;

   public ShutdownHook( final GraphDatabaseService graphDb, final String directory ) {
      _graphDb = graphDb;
      _directory = directory;
   }

   public void run() {
      try {
         final LogFiles logFiles = ((GraphDatabaseAPI)_graphDb).getDependencyResolver()
                                                               .resolveDependency( LogFiles.class );
         final File[] txLogFiles = logFiles.logFiles();
         _graphDb.shutdown();
         // Delete the transaction logs.  This should actually help restarts.
         for ( File txLogFile : txLogFiles ) {
            FileUtils.deleteFile( txLogFile );
         }
         // Delete the counts logs.  This is necessary when different hosts restart a graph.
         final File graphDir = new File( _directory );
         if ( graphDir.isDirectory() ) {
            final File[] files = graphDir.listFiles();
            if ( files != null ) {
               Arrays.stream( files )
                     .filter( f -> f.getAbsolutePath().contains( ".counts.db" ) )
                     .forEach( FileUtils::deleteFile );
            }
         }
      } catch ( LifecycleException | RotationTimeoutException |
            ClassCastException multE ) {
         System.err.println( multE.getMessage() );
         multE.printStackTrace();
         // ignore
      }
   }


}
