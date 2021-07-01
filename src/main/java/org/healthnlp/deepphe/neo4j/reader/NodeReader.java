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
   //                            COHORT DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   public List<PatientSummary> getPatientSummaries( final GraphDatabaseService graphDb,
                                                    final Log log ) {
      return DataUtil.getAllPatientNodes( graphDb )
                     .stream()
                     .map( n -> getPatientSummary( graphDb, log, n ) )
                     .filter( Objects::nonNull )
                     .collect( Collectors.toList() );
   }

   public List<PatientDiagnosis> getPatientDiagnoses( final GraphDatabaseService graphDb,
                                                      final Log log ) {
      return DataUtil.getAllPatientNodes( graphDb )
                     .stream()
                     .map( n -> createPatientDiagnoses( graphDb, log, n ) )
                     .filter( Objects::nonNull )
                     .flatMap( Collection::stream )
                     .collect( Collectors.toList() );
   }

   private List<PatientDiagnosis> createPatientDiagnoses( final GraphDatabaseService graphDb,
                                         final Log log,
                                         final Node patientNode ) {
      final String patientId = DataUtil.getNodeName( graphDb, patientNode );
      return getCancers( graphDb, log, patientNode ).stream()
                                             .map( c -> createPatientDiagnosis( patientId, c ) )
                                             .collect( Collectors.toList() );
   }

   static private PatientDiagnosis createPatientDiagnosis( final String patientId, final NeoplasmSummary cancer ) {
      final PatientDiagnosis diagnosis = new PatientDiagnosis();
      diagnosis.setPatientId( patientId );
      diagnosis.setClassUri( cancer.getClassUri() );
      return diagnosis;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            PATIENT DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   public Patient getPatient( final GraphDatabaseService graphDb,
                              final Log log,
                              final String patientId ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Node patientNode = SearchUtil.getLabeledNode( graphDb, PATIENT_LABEL, patientId );
         Patient patient = getPatient( graphDb, log, patientNode, patientId );
         tx.success();
         return patient;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get patient " + patientId + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return null;
   }


   public Patient getPatient( final GraphDatabaseService graphDb,
                              final Log log,
                              final Node patientNode,
                              final String patientId ) {
      if ( patientNode == null ) {
         log.error( "No patient node for " + patientId );
         return null;
      }
      final Patient patient = new Patient();
      patient.setId( patientId );
      patient.setBirth( "" );
      patient.setDeath( "" );
      patient.setGender( "" );
      patient.setName( patientId );
      final List<Note> notes = getNotes( graphDb, log, patientNode );
      patient.setNotes( notes );
      return patient;
   }


   public PatientSummary getPatientSummary( final GraphDatabaseService graphDb,
                              final Log log,
                              final String patientId ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Node patientNode = SearchUtil.getLabeledNode( graphDb, PATIENT_LABEL, patientId );
         if ( patientNode == null ) {
            log.error( "No patient node for " + patientId );
            tx.success();
            return null;
         }
         PatientSummary patientSummary = getPatientSummary( graphDb, log, patientNode, patientId );
         tx.success();
         return patientSummary;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get patient " + patientId + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return null;
   }


   public PatientSummary getPatientSummary( final GraphDatabaseService graphDb,
                                            final Log log,
                                            final Node patientNode ) {
      if ( patientNode == null ) {
         log.error( "Null Patient Node to getPatientSummary" );
         return null;
      }
      final String patientId = DataUtil.getNodeName( graphDb, patientNode );
      if ( patientId.equals( MISSING_NODE_NAME ) ) {
         log.error( "No patient Id for " + patientNode.getId() );
         return null;
      }
      return getPatientSummary( graphDb, log, patientNode, patientId );
   }


   public PatientSummary getPatientSummary( final GraphDatabaseService graphDb,
                                            final Log log,
                                            final Node patientNode,
                                            final String patientId ) {
      if ( patientNode == null ) {
         log.error( "No patient node for " + patientId );
         return null;
      }
      final PatientSummary patientSummary = new PatientSummary();
      patientSummary.setId( patientId );
      final Patient patient = getPatient( graphDb, log, patientNode, patientId );
      if ( patient == null ) {
         return null;
      }
      patientSummary.setPatient( patient );
      // get cancer summaries
      final List<NeoplasmSummary> cancers = getCancers( graphDb, log, patientNode );
      patientSummary.setNeoplasms( cancers );
      return patientSummary;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            NEOPLASM DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


   private List<NeoplasmSummary> getCancers( final GraphDatabaseService graphDb,
                                final Log log,
                                final Node patientNode ) {
      final List<NeoplasmSummary> cancers = new ArrayList<>();
      try ( Transaction tx = graphDb.beginTx() ) {
         SearchUtil.getOutRelatedNodes( graphDb, patientNode, SUBJECT_HAS_CANCER_RELATION )
                   .stream()
                   .map( n -> createCancer( graphDb, log, n ) )
                   .filter( Objects::nonNull )
                   .forEach( cancers::add );
         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get cancers for " + patientNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return cancers;
   }

   private NeoplasmSummary createCancer( final GraphDatabaseService graphDb,
                            final Log log,
                            final Node cancerNode ) {
      final NeoplasmSummary cancer = new NeoplasmSummary();
      try ( Transaction tx = graphDb.beginTx() ) {
         populateNeoplasm( graphDb, log, cancer, cancerNode );
         final List<NeoplasmSummary> tumors = new ArrayList<>();
         SearchUtil.getOutRelatedNodes( graphDb, cancerNode, CANCER_HAS_TUMOR_RELATION )
                   .stream()
                   .map( t -> populateNeoplasm( graphDb, log, new NeoplasmSummary(), t ) )
                   .filter( Objects::nonNull )
                   .forEach( tumors::add );
         cancer.setSubSummaries( tumors );
         tx.success();
         return cancer;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get cancer " + cancerNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return null;
   }

   private NeoplasmSummary populateNeoplasm( final GraphDatabaseService graphDb,
                                       final Log log,
                                       final NeoplasmSummary neoplasm,
                                       final Node neoplasmNode ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         neoplasm.setId( DataUtil.objectToString( neoplasmNode.getProperty( NAME_KEY ) ) );
         neoplasm.setClassUri( DataUtil.getUri( graphDb, neoplasmNode ) );
         final List<NeoplasmAttribute> attributes = getAttributes( graphDb, log, neoplasmNode );
         neoplasm.setAttributes( attributes );
         tx.success();
         return neoplasm;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get neoplasm " + neoplasmNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return null;
   }

   public List<NeoplasmAttribute> getAttributes( final GraphDatabaseService graphDb,
                                                   final Log log,
                                                   final Node neoplasmNode ) {
      final List<NeoplasmAttribute> attributes = new ArrayList<>();
      try ( Transaction tx = graphDb.beginTx() ) {
         SearchUtil.getOutRelatedNodes( graphDb, neoplasmNode, NEOPLASM_HAS_ATTRIBUTE_RELATION )
                   .stream()
                   .map( a -> createAttribute( graphDb, log, a ) )
                   .filter( Objects::nonNull )
                   .forEach( attributes::add );
         tx.success();
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get attributes for " + neoplasmNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return attributes;
   }

   private NeoplasmAttribute createAttribute( final GraphDatabaseService graphDb,
                                          final Log log,
                                          final Node attributeNode ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final NeoplasmAttribute attribute = new NeoplasmAttribute();
         attribute.setId( DataUtil.objectToString( attributeNode.getProperty( NAME_KEY ) ) );
         attribute.setClassUri( DataUtil.objectToString( attributeNode.getProperty( ATTRIBUTE_URI ) ) );
         attribute.setName( DataUtil.objectToString( attributeNode.getProperty( ATTRIBUTE_NAME ) ) );
         attribute.setValue( DataUtil.objectToString( attributeNode.getProperty( ATTRIBUTE_VALUE ) ) );
         final List<Mention> directEvidence = new ArrayList<>();
         SearchUtil.getOutRelatedNodes( graphDb, attributeNode, ATTRIBUTE_DIRECT_MENTION_RELATION )
                   .stream()
                   .map( m -> createMention( graphDb, log, m ) )
                   .filter( Objects::nonNull )
                   .forEach( directEvidence::add );
         attribute.setDirectEvidence( directEvidence );
         final List<Mention> indirectEvidence = new ArrayList<>();
         SearchUtil.getOutRelatedNodes( graphDb, attributeNode, ATTRIBUTE_INDIRECT_MENTION_RELATION )
                   .stream()
                   .map( m -> createMention( graphDb, log, m ) )
                   .filter( Objects::nonNull )
                   .forEach( indirectEvidence::add );
         attribute.setIndirectEvidence( indirectEvidence );
         final List<Mention> notEvidence = new ArrayList<>();
         SearchUtil.getOutRelatedNodes( graphDb, attributeNode, ATTRIBUTE_NOT_MENTION_RELATION )
                   .stream()
                   .map( m -> createMention( graphDb, log, m ) )
                   .filter( Objects::nonNull )
                   .forEach( notEvidence::add );
         attribute.setNotEvidence( notEvidence );
         tx.success();
         return attribute;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get attribute " + attributeNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return null;
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return mentions;
   }

   private FullMention createFullMention( final GraphDatabaseService graphDb,
                                          final Log log,
                                          final Node mentionNode ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Mention mention = createMention( graphDb, log, mentionNode );
         if ( mention == null ) {
            tx.success();
            return null;
         }
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
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
      }
      return null;
   }

   private Mention createMention( final GraphDatabaseService graphDb,
                                 final Log log,
                                 final Node mentionNode ) {
      try ( Transaction tx = graphDb.beginTx() ) {
         final Mention mention = new Mention();
         mention.setId( DataUtil.objectToString( mentionNode.getProperty( NAME_KEY ) ) );
         mention.setClassUri( DataUtil.getUri( graphDb, mentionNode ) );
         for ( Relationship relation : mentionNode.getRelationships( Direction.INCOMING,
                                                                     NOTE_HAS_TEXT_MENTION_RELATION ) ) {
            final Node noteNode = relation.getOtherNode( mentionNode );
            mention.setNoteId( DataUtil.objectToString( noteNode.getProperty( NAME_KEY ) ) );
            mention.setNoteType( DataUtil.objectToString( noteNode.getProperty( NOTE_TYPE ) ) );
         }
         mention.setBegin( DataUtil.objectToInt( mentionNode.getProperty( TEXT_SPAN_BEGIN ) ) );
         mention.setEnd( DataUtil.objectToInt( mentionNode.getProperty( TEXT_SPAN_END ) ) );
         mention.setNegated( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_NEGATED ) ) );
         mention.setUncertain( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_UNCERTAIN ) ) );
         mention.setGeneric( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_GENERIC ) ) );
         mention.setConditional( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_CONDITIONAL ) ) );
         mention.setHistoric( DataUtil.objectToBoolean( mentionNode.getProperty( INSTANCE_HISTORIC ) ) );
         mention.setTemporality( DataUtil.objectToString( mentionNode.getProperty( INSTANCE_TEMPORALITY ) ) );
         tx.success();
         return mention;
      } catch ( TransactionFailureException txE ) {
         log.error( "Cannot get Mention " + mentionNode.getId() + " from graph." );
         log.error( txE.getMessage() );
      } catch ( Exception e ) {
         // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
         log.error( "Ignoring Exception " + e.getMessage() );
         // Attempt to continue.
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
