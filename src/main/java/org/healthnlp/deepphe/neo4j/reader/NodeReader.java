package org.healthnlp.deepphe.neo4j.reader;


import org.healthnlp.deepphe.neo4j.node.*;
import org.healthnlp.deepphe.neo4j.util.DataUtil;
import org.healthnlp.deepphe.neo4j.util.SearchUtil;
import org.neo4j.graphdb.*;
import org.neo4j.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static org.healthnlp.deepphe.neo4j.constant.Neo4jConstants.*;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 7/27/2020
 */
public enum NodeReader {
   INSTANCE;

   static public NodeReader getInstance() {
      return INSTANCE;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            PATIENT DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   public Patient getPatient( final GraphDatabaseService graphDb,
                              final Log log,
                              final String patientId ) {
      final Patient patient = new Patient();
      try ( Transaction tx = graphDb.beginTx() ) {
         final Node patientNode = SearchUtil.getLabeledNode( graphDb, PATIENT_LABEL, patientId );
         if ( patientNode == null ) {
            log.error( "No patient node for " + patientId );
            tx.success();
            return null;
         }

         patient.setId( patientId );
//         patient.setName( DataUtil.objectToString( patientNode.getProperty( PATIENT_NAME ) ) );
//         patient.setGender( DataUtil.objectToString( patientNode.getProperty( PATIENT_GENDER ) ) );
//         patient.setBirth( DataUtil.objectToString( patientNode.getProperty( PATIENT_BIRTH_DATE ) ) );
//         patient.setDeath( DataUtil.objectToString( patientNode.getProperty( PATIENT_DEATH_DATE ) ) );

         final List<Note> notes = getNotes( graphDb, log, patientNode );
         patient.setNotes( notes );

         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get patient " + patientId + " from graph." );
         log.error( txE.getMessage() );
      }
      return patient;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            NOTE DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   public Note getNote( final GraphDatabaseService graphDb,
                 final Log log,
                 final String noteId ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Node noteNode = SearchUtil.getLabeledNode( graphDb, TEXT_DOCUMENT_LABEL, noteId );
         if ( noteNode == null ) {
            log.error( "No note node for " + noteId );
            tx.success();
            return null;
         }
         final Note note = createNote( graphDb, log, noteNode );
         tx.success();
         return note;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get note " + noteId + " from graph." );
         log.error( txE.getMessage() );
      }
      final Note badNote = new Note();
      badNote.setId( noteId );
      return badNote;
   }


   private List<Note> getNotes( final GraphDatabaseService graphDb,
                                final Log log,
                                final Node patientNode ) {
      final List<Note> notes = new ArrayList<>();
      try ( Transaction tx = graphDb.beginTx() ) {
         SearchUtil.getOutRelatedNodes( graphDb, patientNode, SUBJECT_HAS_NOTE_RELATION )
                   .stream()
                   .map( n -> createNote( graphDb, log, n ) )
                   .forEach( notes::add );
         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get notes for " + patientNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      }
      return notes;
   }


   private Note createNote( final GraphDatabaseService graphDb,
                            final Log log,
                            final Node noteNode ) {
      final Note note = new Note();
      try ( Transaction tx = graphDb.beginTx() ) {
         note.setId( DataUtil.objectToString( noteNode.getProperty( NAME_KEY ) ) );
         note.setType( DataUtil.objectToString( noteNode.getProperty( NOTE_TYPE ) ) );
         note.setDate( DataUtil.objectToString( noteNode.getProperty( NOTE_DATE ) ) );
         note.setEpisode( DataUtil.objectToString( noteNode.getProperty( NOTE_EPISODE ) ) );
         note.setText( DataUtil.objectToString( noteNode.getProperty( NOTE_TEXT ) ) );

         note.setSections( getSections( graphDb, log, noteNode ) );
         final Collection<FullMention> fullMentions = getFullMentions( graphDb, log, noteNode );
         note.setMentions( getMentions( fullMentions ) );
         note.setRelations( getRelations( fullMentions ) );
         note.setCorefs( getCorefs( fullMentions ) );

         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get Note " + noteNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      }
      return note;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            SECTION DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   public List<Section> getSections( final GraphDatabaseService graphDb,
                                      final Log log,
                                      final Node noteNode ) {
      final List<Section> sections = new ArrayList<>();
      try ( Transaction tx = graphDb.beginTx() ) {
         SearchUtil.getOutRelatedNodes( graphDb, noteNode, NOTE_HAS_SECTION_RELATION )
                   .stream()
                   .map( s -> createSection( graphDb, log, s ) )
                   .filter( Objects::nonNull )
                   .forEach( sections::add );
         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get sections for " + noteNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      }
      return sections;
   }


   private Section createSection( final GraphDatabaseService graphDb,
                                  final Log log,
                                  final Node sectionNode ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Section section = new Section();
         section.setId( DataUtil.objectToString( sectionNode.getProperty( NAME_KEY ) ) );
         section.setType( DataUtil.objectToString( sectionNode.getProperty( SECTION_TYPE ) ) );
         section.setBegin( DataUtil.objectToInt( sectionNode.getProperty( TEXT_SPAN_BEGIN ) ) );
         section.setEnd( DataUtil.objectToInt( sectionNode.getProperty( TEXT_SPAN_END ) ) );
         tx.success();
         return section;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get Section " + sectionNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      }
      return null;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            MENTION RELATION AND COREF DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   public Collection<FullMention> getFullMentions( final GraphDatabaseService graphDb,
                                                    final Log log,
                                                    final Node noteNode ) {
      final Collection<FullMention> mentions = new ArrayList<>();
      try ( Transaction tx = graphDb.beginTx() ) {
         SearchUtil.getOutRelatedNodes( graphDb, noteNode, NOTE_HAS_TEXT_MENTION_RELATION )
                   .stream()
                   .map( m -> createFullMention( graphDb, log, m ) )
                   .filter( Objects::nonNull )
                   .forEach( mentions::add );
         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get mentions for " + noteNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      }
      return mentions;
   }

   private FullMention createFullMention( final GraphDatabaseService graphDb,
                                          final Log log,
                                          final Node mentionNode ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Mention mention = new Mention();
         mention.setId( DataUtil.objectToString( mentionNode.getProperty( NAME_KEY ) ) );
         mention.setClassUri( DataUtil.getUri( graphDb, mentionNode ) );
         mention.setBegin( DataUtil.objectToInt( mentionNode.getProperty( TEXT_SPAN_BEGIN ) ) );
         mention.setEnd( DataUtil.objectToInt( mentionNode.getProperty( TEXT_SPAN_END ) ) );
         mention.setNegated( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_NEGATED ) ) );
         mention.setUncertain( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_UNCERTAIN ) ) );
         mention.setGeneric( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_GENERIC ) ) );
         mention.setConditional( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_CONDITIONAL ) ) );
         mention.setHistoric( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_HISTORIC ) ) );
         mention.setTemporality( DataUtil.objectToString( mentionNode.getProperty( INSTANCE_TEMPORALITY ) ) );

         final FullMention fullMention = new FullMention( mention );
         for ( Relationship relation : mentionNode.getRelationships( Direction.OUTGOING ) ) {
            final String relationName = relation.getType().name();
            if ( relationName.equals( INSTANCE_OF ) ) {
               continue;
            }
            if ( relationName.equals( MENTION_COREF ) ) {
               fullMention.addCoref( DataUtil.objectToString( relation.getProperty( COREF_ID ) ) );
               continue;
            }
            final Node targetNode = relation.getOtherNode( mentionNode );
            fullMention.addRelation( DataUtil.objectToString( targetNode.getProperty( NAME_KEY ) ), relationName );
         }

         tx.success();
         return fullMention;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get Mention " + mentionNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      }
      return null;
   }


   private List<Mention> getMentions( final Collection<FullMention> fullMentions ) {
      return fullMentions.stream()
                         .map( FullMention::getMention )
                         .collect( Collectors.toList() );
   }


   private List<MentionRelation> getRelations( final Collection<FullMention> fullMentions ) {
      return fullMentions.stream()
                         .map( FullMention::getRelations )
                         .flatMap( Collection::stream )
                         .collect( Collectors.toList() );
   }


   private List<MentionCoref> getCorefs( final Collection<FullMention> fullMentions ) {
      final Map<String, Collection<String>> corefMap = new HashMap<>();
      for ( FullMention mention : fullMentions ) {
         mention.getCorefs().forEach( c -> corefMap.computeIfAbsent( c, d -> new HashSet<>() ).add( mention.getId() ) );
      }
      final List<MentionCoref> corefs = new ArrayList<>( corefMap.size() );
      for ( Map.Entry<String, Collection<String>> entry : corefMap.entrySet() ) {
         final MentionCoref coref = new MentionCoref();
         coref.setId( entry.getKey() );
         coref.setIdChain( entry.getValue().toArray( new String[ 0 ] ) );
         corefs.add( coref );
      }
      return corefs;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            MENTION RELATION AND COREF HELPER CLASS
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   static private class FullMention {
      private final Mention _mention;
      private Collection<HalfRelation> _relations;
      private Collection<String> _corefs;

      private FullMention( final Mention mention ) {
         _mention = mention;
      }

      private void addRelation( final String targetId, final String relationType ) {
         if ( _relations == null ) {
            _relations = new HashSet<>();
         }
         _relations.add( new HalfRelation( targetId, relationType ) );
      }

      private void addCoref( final String chainId ) {
         if ( _corefs == null ) {
            _corefs = new HashSet<>();
         }
         _corefs.add( chainId );
      }

      public String getId() {
         return _mention.getId();
      }

      public Mention getMention() {
         return _mention;
      }

      public Collection<MentionRelation> getRelations() {
         if ( _relations == null ) {
            return Collections.emptyList();
         }
         return _relations.stream().map( this::createRelation ).collect( Collectors.toList() );
      }

      private MentionRelation createRelation( final HalfRelation vector ) {
         final MentionRelation relation = new MentionRelation();
         relation.setSourceId( getId() );
         relation.setTargetId( vector.getTargetId() );
         relation.setType( vector.getRelationType() );
         return relation;
      }

      public Collection<String> getCorefs() {
         if ( _corefs == null ) {
            return Collections.emptyList();
         }
         return _corefs;
      }

      static private class HalfRelation {
         final private String _targetId;
         final private String _relationType;

         public HalfRelation( final String targetId, final String relationType ) {
            _targetId = targetId;
            _relationType = relationType;
         }

         public String getTargetId() {
            return _targetId;
         }

         public String getRelationType() {
            return _relationType;
         }
      }
   }


}
