package org.healthnlp.deepphe.neo4j.constant;

import org.healthnlp.deepphe.neo4j.util.SearchUtil;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author SPF , chip-nlp
 * @since {11/12/2023}
 */
final public class UriConstants2 {

   private UriConstants2() {}

   static public final String PATIENT_CONCEPT_SCHEMA = "DeepPheXn";

   static public final String BREAST_QUADRANT = "BreastQuadrant";
   static public final Collection<String> QUADRANT_URIS = new HashSet<>();
   static public Collection<String> getBreastQuadrants( final GraphDatabaseService graphDb ) {
      initializeUris( graphDb );
      return QUADRANT_URIS;
   }

   static private final Collection<String> LEFT_LUNG_LOBES = new HashSet<>();
   static public Collection<String> getLeftLungLobes( final GraphDatabaseService graphDb ) {
      initializeUris( graphDb );
      return LEFT_LUNG_LOBES;
   }

   static private final Collection<String> RIGHT_LUNG_LOBES = new HashSet<>();
   static public Collection<String> getRightLungLobes(final GraphDatabaseService graphDb ) {
      initializeUris( graphDb );
      return RIGHT_LUNG_LOBES;
   }



   static private final Object URI_LOCK = new Object();
   static private void initializeUris( final GraphDatabaseService graphDb ) {
      synchronized ( URI_LOCK ) {
         if ( !QUADRANT_URIS.isEmpty() ) {
            return;
         }
         QUADRANT_URIS.addAll( SearchUtil.getBranchUris( graphDb, BREAST_QUADRANT ) );
         QUADRANT_URIS.remove( BREAST_QUADRANT );
//         QUADRANT_URIS.addAll( SearchUtil.getBranchUris( graphDb, "Nipple" ) );
//         QUADRANT_URIS.addAll( SearchUtil.getBranchUris( graphDb, "Areola" ) );
//         QUADRANT_URIS.addAll( SearchUtil.getBranchUris( graphDb, "CentralPortionOfTheBreast" ) );
//         QUADRANT_URIS.addAll( SearchUtil.getBranchUris( graphDb, "SubareolarRegion" ) );
//         QUADRANT_URIS.addAll( SearchUtil.getBranchUris( graphDb, "AxillaryTailOfTheBreast" ) );

         final Collection<String> lungLobes = SearchUtil.getBranchUris( graphDb, "LungLobe" );
         lungLobes.stream().filter( u -> u.contains( "Left" ) ).forEach( LEFT_LUNG_LOBES::add );
         lungLobes.stream().filter( u -> u.contains( "Right" ) ).forEach( RIGHT_LUNG_LOBES::add );
      }
   }



}
