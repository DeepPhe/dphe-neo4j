package org.healthnlp.deepphe.neo4j.embedded;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.impl.store.kvstore.RotationTimeoutException;
import org.neo4j.kernel.lifecycle.LifecycleException;

import java.io.File;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 7/23/2020
 */
final public class ServiceFactory {

   static private final Logger LOGGER = Logger.getLogger( "ServiceFactory" );

   private ServiceFactory() {}

   static public GraphDatabaseService createService( final String graphDbPath ) {
      final File graphDbFile = new File( graphDbPath );
      if ( !graphDbFile.isDirectory() ) {
         LOGGER.error( "No Database exists at: " + graphDbPath );
         System.exit( -1 );
      }
      final GraphDatabaseService graphDb = new GraphDatabaseFactory()
            .newEmbeddedDatabaseBuilder( graphDbFile )
            .setConfig( GraphDatabaseSettings.read_only, "true" )
            .newGraphDatabase();
      if ( !graphDb.isAvailable( 500 ) ) {
         LOGGER.error( "Could not initialize neo4j connection for: " + graphDbPath );
         System.exit( -1 );
      }
      registerShutdownHook( graphDb );
      return graphDb;
   }

   static private void registerShutdownHook( final GraphDatabaseService graphDb ) {
      // Registers a shutdown hook for the Neo4j instance so that it
      // shuts down nicely when the VM exits (even if you "Ctrl-C" the
      // running application).
      Runtime.getRuntime().addShutdownHook( new Thread( () -> {
         try {
            graphDb.shutdown();
         } catch ( LifecycleException | RotationTimeoutException multE ) {
            // ignore
         }
      } ) );
   }


}
