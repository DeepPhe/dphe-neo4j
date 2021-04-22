package org.healthnlp.deepphe.neo4j.reader;


import org.healthnlp.deepphe.neo4j.node.*;
import org.healthnlp.deepphe.neo4j.util.DataUtil;
import org.healthnlp.deepphe.neo4j.util.SearchUtil;
import org.neo4j.graphdb.*;
import org.neo4j.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static org.healthnlp.deepphe.neo4j.constant.Neo4jConstants.*;
import static org.healthnlp.deepphe.neo4j.constant.RelationConstants.HAS_STAGE;
import static org.healthnlp.deepphe.neo4j.constant.UriConstants.STAGE;
import static org.healthnlp.deepphe.neo4j.util.DataUtil.adjustPropertyName;
import static org.healthnlp.deepphe.neo4j.util.DataUtil.safeGetProperty;

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


    public Patient getPatient(final GraphDatabaseService graphDb,
                              final Log log,
                              final String patientId) {
        final Patient patient = new Patient();
        try (Transaction tx = graphDb.beginTx()) {
            final Node patientNode = SearchUtil.getLabeledNode(graphDb, PATIENT_LABEL, patientId);
            if (patientNode == null) {
                log.error("No patient node for " + patientId);
                tx.success();
                return null;
            }

            patient.setId(patientId);


            String nameValue = DataUtil.safeGetProperty(patientNode, PATIENT_NAME, "Joan Q. Public");

            String genderValue = DataUtil.safeGetProperty(patientNode, PATIENT_GENDER, "F");


            String birthValue = DataUtil.safeGetProperty(patientNode, PATIENT_BIRTH_DATE, "2021-09-13");


            String deathValue = DataUtil.safeGetProperty(patientNode, PATIENT_DEATH_DATE, "2025-09-3");


            patient.setName(nameValue);
            patient.setGender(genderValue);
            patient.setBirth(birthValue);
            patient.setDeath(deathValue);


            final List<Note> notes = getNotes(graphDb, log, patientNode);
            patient.setNotes(notes);

            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get patient " + patientId + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return patient;
    }


    public PatientSummary getPatientSummary(final GraphDatabaseService graphDb,
                                            final Log log,
                                            final String patientId) {
        final PatientSummary patientSummary = new PatientSummary();
        patientSummary.setId(patientId);
        final Patient patient = getPatient(graphDb, log, patientId);
        if (patient == null) {
            return null;
        }
        patientSummary.setPatient(patient);

        try (Transaction tx = graphDb.beginTx()) {
            final Node patientNode = SearchUtil.getLabeledNode(graphDb, PATIENT_LABEL, patientId);
            if (patientNode == null) {
                log.error("No patient node for " + patientId);
                tx.success();
                return null;
            }
            // get cancer summaries
            final List<NeoplasmSummary> cancers = getCancers(graphDb, log, patientNode);
            patientSummary.setNeoplasms(cancers);
            // Neoplasm is id, class uri and attributes.
            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get patient " + patientId + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return patientSummary;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    //
    //                            NEOPLASM DATA
    //
    /////////////////////////////////////////////////////////////////////////////////////////


    private List<NeoplasmSummary> getCancers(final GraphDatabaseService graphDb,
                                             final Log log,
                                             final Node patientNode) {
        final List<NeoplasmSummary> cancers = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            SearchUtil.getOutRelatedNodes(graphDb, patientNode, SUBJECT_HAS_CANCER_RELATION)
                    .stream()
                    .map(n -> createCancer(graphDb, log, n))
                    .filter(Objects::nonNull)
                    .forEach(cancers::add);
            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get cancers for " + patientNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
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

    private NeoplasmSummary populateNeoplasm(final GraphDatabaseService graphDb,
                                             final Log log,
                                             final NeoplasmSummary neoplasm,
                                             final Node neoplasmNode) {
        try (Transaction tx = graphDb.beginTx()) {
            neoplasm.setId(DataUtil.objectToString(neoplasmNode.getProperty(NAME_KEY)));
            neoplasm.setClassUri(DataUtil.getUri(graphDb, neoplasmNode));
            final List<NeoplasmAttribute> attributes = getAttributes(graphDb, log, neoplasmNode);
            neoplasm.setAttributes(attributes);
            tx.success();
            return neoplasm;
        } catch (TransactionFailureException txE) {
            log.error("Cannot get neoplasm " + neoplasmNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return null;
    }

    public List<NeoplasmAttribute> getAttributes(final GraphDatabaseService graphDb,
                                                 final Log log,
                                                 final Node neoplasmNode) {
        final List<NeoplasmAttribute> attributes = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            SearchUtil.getOutRelatedNodes(graphDb, neoplasmNode, NEOPLASM_HAS_ATTRIBUTE_RELATION)
                    .stream()
                    .map(a -> createAttribute(graphDb, log, a))
                    .filter(Objects::nonNull)
                    .forEach(attributes::add);
            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get attributes for " + neoplasmNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return attributes;
    }

    private NeoplasmAttribute createAttribute(final GraphDatabaseService graphDb,
                                              final Log log,
                                              final Node attributeNode) {
        try (Transaction tx = graphDb.beginTx()) {
            final NeoplasmAttribute attribute = new NeoplasmAttribute();
            attribute.setId(DataUtil.objectToString(attributeNode.getProperty(NAME_KEY)));
            attribute.setClassUri(DataUtil.objectToString(attributeNode.getProperty(ATTRIBUTE_URI)));
            attribute.setName(DataUtil.objectToString(attributeNode.getProperty(ATTRIBUTE_NAME)));
            attribute.setValue(DataUtil.objectToString(attributeNode.getProperty(ATTRIBUTE_VALUE)));
            final List<Mention> directEvidence = new ArrayList<>();
            SearchUtil.getOutRelatedNodes(graphDb, attributeNode, ATTRIBUTE_DIRECT_MENTION_RELATION)
                    .stream()
                    .map(m -> createMention(graphDb, log, m))
                    .filter(Objects::nonNull)
                    .forEach(directEvidence::add);
            attribute.setDirectEvidence(directEvidence);
            final List<Mention> indirectEvidence = new ArrayList<>();
            SearchUtil.getOutRelatedNodes(graphDb, attributeNode, ATTRIBUTE_INDIRECT_MENTION_RELATION)
                    .stream()
                    .map(m -> createMention(graphDb, log, m))
                    .filter(Objects::nonNull)
                    .forEach(indirectEvidence::add);
            attribute.setIndirectEvidence(indirectEvidence);
            final List<Mention> notEvidence = new ArrayList<>();
            SearchUtil.getOutRelatedNodes(graphDb, attributeNode, ATTRIBUTE_NOT_MENTION_RELATION)
                    .stream()
                    .map(m -> createMention(graphDb, log, m))
                    .filter(Objects::nonNull)
                    .forEach(notEvidence::add);
            attribute.setNotEvidence(notEvidence);
            tx.success();
            return attribute;
        } catch (TransactionFailureException txE) {
            log.error("Cannot get attribute " + attributeNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return null;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    //
    //                            NOTE DATA
    //
    /////////////////////////////////////////////////////////////////////////////////////////


    public Note getNote(final GraphDatabaseService graphDb,
                        final Log log,
                        final String noteId) {
        try (Transaction tx = graphDb.beginTx()) {
            final Node noteNode = SearchUtil.getLabeledNode(graphDb, TEXT_DOCUMENT_LABEL, noteId);
            if (noteNode == null) {
                log.error("No note node for " + noteId);
                tx.success();
                return null;
            }
            final Note note = createNote(graphDb, log, noteNode);
            tx.success();
            return note;
        } catch (TransactionFailureException txE) {
            log.error("Cannot get note " + noteId + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        final Note badNote = new Note();
        badNote.setId(noteId);
        return badNote;
    }


   private List<Note> getNotes( final GraphDatabaseService graphDb,
                                final Log log,
                                final Node patientNode) {
        final List<Note> notes = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            SearchUtil.getOutRelatedNodes(graphDb, patientNode, SUBJECT_HAS_NOTE_RELATION)
                    .stream()
                    .map(n -> createNote(graphDb, log, n))
                    .forEach(notes::add);
            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get notes for " + patientNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return notes;
    }


    private Note createNote(final GraphDatabaseService graphDb,
                            final Log log,
                            final Node noteNode) {
        final Note note = new Note();
        try (Transaction tx = graphDb.beginTx()) {
            note.setId(DataUtil.objectToString(noteNode.getProperty(NAME_KEY)));
            note.setType(DataUtil.objectToString(noteNode.getProperty(NOTE_TYPE)));
            note.setDate(DataUtil.objectToString(noteNode.getProperty(NOTE_DATE)));
            note.setEpisode(DataUtil.objectToString(noteNode.getProperty(NOTE_EPISODE)));
            note.setText(DataUtil.objectToString(noteNode.getProperty(NOTE_TEXT)));

            note.setSections(getSections(graphDb, log, noteNode));
            final Collection<FullMention> fullMentions = getFullMentions(graphDb, log, noteNode);
            note.setMentions(getMentions(fullMentions));
            note.setRelations(getRelations(fullMentions));
            note.setCorefs(getCorefs(fullMentions));

            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get Note " + noteNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return note;
    }


   /////////////////////////////////////////////////////////////////////////////////////////
   //
   //                            SECTION DATA
   //
   /////////////////////////////////////////////////////////////////////////////////////////


    public List<Section> getSections(final GraphDatabaseService graphDb,
                                     final Log log,
                                     final Node noteNode) {
        final List<Section> sections = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            SearchUtil.getOutRelatedNodes(graphDb, noteNode, NOTE_HAS_SECTION_RELATION)
                    .stream()
                    .map(s -> createSection(graphDb, log, s))
                    .filter(Objects::nonNull)
                    .forEach(sections::add);
            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get sections for " + noteNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return sections;
    }


    private Section createSection(final GraphDatabaseService graphDb,
                                  final Log log,
                                  final Node sectionNode) {
        try (Transaction tx = graphDb.beginTx()) {
            final Section section = new Section();
            section.setId(DataUtil.objectToString(sectionNode.getProperty(NAME_KEY)));
            section.setType(DataUtil.objectToString(sectionNode.getProperty(SECTION_TYPE)));
            section.setBegin(DataUtil.objectToInt(sectionNode.getProperty(TEXT_SPAN_BEGIN)));
            section.setEnd(DataUtil.objectToInt(sectionNode.getProperty(TEXT_SPAN_END)));
            tx.success();
            return section;
        } catch (TransactionFailureException txE) {
            log.error("Cannot get Section " + sectionNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return null;
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    //
    //                            MENTION RELATION AND COREF DATA
    //
    /////////////////////////////////////////////////////////////////////////////////////////


    public Collection<FullMention> getFullMentions(final GraphDatabaseService graphDb,
                                                   final Log log,
                                                   final Node noteNode) {
        final Collection<FullMention> mentions = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            SearchUtil.getOutRelatedNodes(graphDb, noteNode, NOTE_HAS_TEXT_MENTION_RELATION)
                    .stream()
                    .map(m -> createFullMention(graphDb, log, m))
                    .filter(Objects::nonNull)
                    .forEach(mentions::add);
            tx.success();
        } catch (TransactionFailureException txE) {
            log.error("Cannot get mentions for " + noteNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return mentions;
    }

    private FullMention createFullMention(final GraphDatabaseService graphDb,
                                          final Log log,
                                          final Node mentionNode) {
        try (Transaction tx = graphDb.beginTx()) {
            final Mention mention = createMention(graphDb, log, mentionNode);
            if (mention == null) {
                tx.success();
                return null;
            }
            final FullMention fullMention = new FullMention(mention);
            for (Relationship relation : mentionNode.getRelationships(Direction.OUTGOING)) {
                final String relationName = relation.getType().name();
                if (relationName.equals(INSTANCE_OF)) {
                    continue;
                }
                if (relationName.equals(MENTION_COREF)) {
                    fullMention.addCoref(DataUtil.objectToString(relation.getProperty(COREF_ID)));
                    continue;
                }
                final Node targetNode = relation.getOtherNode(mentionNode);
                fullMention.addRelation(DataUtil.objectToString(targetNode.getProperty(NAME_KEY)), relationName);
            }

            tx.success();
            return fullMention;
        } catch (TransactionFailureException txE) {
            log.error("Cannot get Mention " + mentionNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return null;
    }

    private Mention createMention(final GraphDatabaseService graphDb,
                                  final Log log,
                                  final Node mentionNode) {
        try (Transaction tx = graphDb.beginTx()) {
            final Mention mention = new Mention();
            mention.setId(DataUtil.objectToString(mentionNode.getProperty(NAME_KEY)));
            mention.setClassUri(DataUtil.getUri(graphDb, mentionNode));
            mention.setBegin(DataUtil.objectToInt(mentionNode.getProperty(TEXT_SPAN_BEGIN)));
            mention.setEnd(DataUtil.objectToInt(mentionNode.getProperty(TEXT_SPAN_END)));
            mention.setNegated(DataUtil.objectToBoolean(mentionNode.getProperty(INSTANCE_NEGATED)));
            mention.setUncertain(DataUtil.objectToBoolean(mentionNode.getProperty(INSTANCE_UNCERTAIN)));
            mention.setGeneric(DataUtil.objectToBoolean(mentionNode.getProperty(INSTANCE_GENERIC)));
            mention.setConditional(DataUtil.objectToBoolean(mentionNode.getProperty(INSTANCE_CONDITIONAL)));
            mention.setHistoric(DataUtil.objectToBoolean(mentionNode.getProperty(INSTANCE_HISTORIC)));
            mention.setTemporality(DataUtil.objectToString(mentionNode.getProperty(INSTANCE_TEMPORALITY)));
            tx.success();
            return mention;
        } catch (TransactionFailureException txE) {
            log.error("Cannot get Mention " + mentionNode.getId() + " from graph.");
            log.error(txE.getMessage());
        } catch (Exception e) {
            // While it is bad practice to catch pure Exception, neo4j throws undeclared exceptions of all types.
            log.error("Ignoring Exception " + e.getMessage());
            // Attempt to continue.
        }
        return null;
    }

    private List<Mention> getMentions(final Collection<FullMention> fullMentions) {
        return fullMentions.stream()
                .map(FullMention::getMention)
                .collect(Collectors.toList());
    }


    private List<MentionRelation> getRelations(final Collection<FullMention> fullMentions) {
        return fullMentions.stream()
                .map(FullMention::getRelations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    private List<MentionCoref> getCorefs(final Collection<FullMention> fullMentions) {
        final Map<String, Collection<String>> corefMap = new HashMap<>();
        for (FullMention mention : fullMentions) {
            mention.getCorefs().forEach(c -> corefMap.computeIfAbsent(c, d -> new HashSet<>()).add(mention.getId()));
        }
        final List<MentionCoref> corefs = new ArrayList<>(corefMap.size());
        for (Map.Entry<String, Collection<String>> entry : corefMap.entrySet()) {
            final MentionCoref coref = new MentionCoref();
            coref.setId(entry.getKey());
            coref.setIdChain(entry.getValue().toArray(new String[0]));
            corefs.add(coref);
        }
        return corefs;
    }

    static private PatientSummaryAndStages createSharedPatientProperties( final Node patientNode ) {



        String nameValue = safeGetProperty(patientNode, PATIENT_NAME, "John Smith");
        String genderValue = safeGetProperty(patientNode, PATIENT_GENDER, "M");

        String birthValue = safeGetProperty(patientNode, PATIENT_BIRTH_DATE, "2010-01-01");
        String deathValue = safeGetProperty(patientNode, PATIENT_DEATH_DATE, "2012-01-01");
        String stageValue = safeGetProperty(patientNode, "stages", "Stage IA");

        String firstEncounterDate = "2010-01-01";
        String lastEncounterDate = "2015-01-06";
        String firstEncounterAge = "15";
        String lastEncounterAge = "37";

        PatientSummaryAndStages patientSummaryAndStages = new PatientSummaryAndStages();
        //patientSummaryAndStages.setPatientId(); how?
        patientSummaryAndStages.setPatientName(nameValue);
        patientSummaryAndStages.setBirthDate(birthValue);
        patientSummaryAndStages.setFirstEncounterAge(firstEncounterAge);
        patientSummaryAndStages.setGetFirstEncounterDate(firstEncounterDate);
        patientSummaryAndStages.setLastEncounterAge(lastEncounterAge);
        patientSummaryAndStages.setLastEncounterDate(lastEncounterDate);
        patientSummaryAndStages.getStages().add("Stage IA");

        final Map<String, Object> sharedPatientProperties = new HashMap<>();
        // Currently the NAME_KEY works as patient ID - Joe
        sharedPatientProperties.put( "patientId",
                DataUtil.objectToString( patientNode.getProperty( NAME_KEY ) ) );
        sharedPatientProperties.put( "patientName",nameValue);
        sharedPatientProperties.put( "birthDate", birthValue);
        // DataUtil.objectToString( birthValue );
        sharedPatientProperties.put( "firstEncounterDate",firstEncounterDate);
        //DataUtil.objectToString( patientNode.getProperty( PATIENT_FIRST_ENCOUNTER ) ) );
        sharedPatientProperties.put( "lastEncounterDate", lastEncounterDate);
        //  DataUtil.objectToString( patientNode.getProperty( PATIENT_LAST_ENCOUNTER ) ) );
        sharedPatientProperties.put( "firstEncounterAge", firstEncounterAge);
//              TextFormatter.getPatientEncounterAge(
//                      DataUtil.objectToString( patientNode.getProperty( PATIENT_BIRTH_DATE ) ),
//                      DataUtil.objectToString(
//                              patientNode.getProperty( PATIENT_FIRST_ENCOUNTER ) ) ) );
        sharedPatientProperties.put( "lastEncounterAge", lastEncounterAge);
//              TextFormatter.getPatientEncounterAge(
//                      DataUtil.objectToString( patientNode.getProperty( PATIENT_BIRTH_DATE ) ),
//                      DataUtil.objectToString(
//                              patientNode.getProperty( PATIENT_LAST_ENCOUNTER ) ) ) );

        return patientSummaryAndStages;
    }

    public PatientSummaryAndStagesList patientSummaryAndStagesList(GraphDatabaseService graphDb, Log log, boolean includeStages) {
        PatientSummaryAndStagesList patientSummaryAndStagesList = new PatientSummaryAndStagesList();
        try ( Transaction tx = graphDb.beginTx() ) {
            // DataUtil.getAllPatientNodes() is supposed to return all unique patients
            final Collection<Node> patientNodes = DataUtil.getAllPatientNodes( graphDb );
            for ( Node patientNode : patientNodes ) {
                PatientSummaryAndStages patientSummaryAndStages = createSharedPatientProperties( patientNode );


          if ( includeStages ) {
                    // get the major stage values for the patient
                    final Set<String> stages = new HashSet<>();
                    final Collection<Node> cancerNodes = SearchUtil.getOutRelatedNodes( graphDb, patientNode,
                            SUBJECT_HAS_CANCER_RELATION );
                    for ( Node cancerNode : cancerNodes ) {
                        // TODO : We are no longer using specific relation names for the various attributes.
                        //  Using a unique relation name per attribute type makes the graph too 'ugly', especially since we
                        //  are working up to 50+ attribute types.
                        // We are now using the Neo4jConstants NEOPLASM_HAS_ATTRIBUTE / NEOPLASM_HAS_ATTRIBUTE_RELATION to
                        // specify normalized NeoplasmAttribute for any attribute type.
                        // The attribute has a normalized ontology uri, "human readable" name and value.
                        // TODO once this is moved to ReadFunctions it should be more like:*/

                            SearchUtil.getOutRelatedNodes( graphDb, cancerNode, NEOPLASM_HAS_ATTRIBUTE_RELATION )
                            .stream()
                            .filter( n -> DataUtil.objectToString( n.getProperty( ATTRIBUTE_NAME ) ).equals( "stage" ) )
                            .map( n -> DataUtil.objectToString( n.getProperty( ATTRIBUTE_URI ) ) )
                            //.map( TextFormatter::toPrettyStage )
                            .forEach( stages::add );
                        // TODO - note that we should probably make an AttributeConstants class or something like that.
//                    //  SearchUtil.getOutRelatedNodes( graphDb, cancerNode, HAS_STAGE )
//                                .stream()
//                                .map( n -> DataUtil.getUri( graphDb, n ) )
//                                .map( TextFormatter::toPrettyStage )
//                                .forEach( stages::add );
                    }
                    // Also add stages for cohort
                    //patientProperties.put( "stages", stages );
              patientSummaryAndStages.getStages().addAll(stages);
                }


                // Add to the set, this doesn't allow duplicates
                patientSummaryAndStagesList.getPatientSummaryAndStages().add( patientSummaryAndStages );
            }
            tx.success();
        } catch ( RuntimeException e ) {
            throw new RuntimeException( "Failed to call getCohortData()" );
        }
        return patientSummaryAndStagesList;
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

        private FullMention(final Mention mention) {
            _mention = mention;
        }

        private void addRelation(final String targetId, final String relationType) {
            if (_relations == null) {
                _relations = new HashSet<>();
            }
            _relations.add(new HalfRelation(targetId, relationType));
        }

        private void addCoref(final String chainId) {
            if (_corefs == null) {
                _corefs = new HashSet<>();
            }
            _corefs.add(chainId);
        }

        public String getId() {
            return _mention.getId();
        }

        public Mention getMention() {
            return _mention;
        }

        public Collection<MentionRelation> getRelations() {
            if (_relations == null) {
                return Collections.emptyList();
            }
            return _relations.stream().map(this::createRelation).collect(Collectors.toList());
        }

        private MentionRelation createRelation(final HalfRelation vector) {
            final MentionRelation relation = new MentionRelation();
            relation.setSourceId(getId());
            relation.setTargetId(vector.getTargetId());
            relation.setType(vector.getRelationType());
            return relation;
        }

        public Collection<String> getCorefs() {
            if (_corefs == null) {
                return Collections.emptyList();
            }
            return _corefs;
        }

        static private class HalfRelation {
            final private String _targetId;
            final private String _relationType;

            public HalfRelation(final String targetId, final String relationType) {
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
