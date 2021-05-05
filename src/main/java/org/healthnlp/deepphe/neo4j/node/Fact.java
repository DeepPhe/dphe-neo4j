package org.healthnlp.deepphe.neo4j.node;

import java.util.List;
import java.util.Map;

/**
 * @author SPF , chip-nlp
 * @since {4/30/2021}
 */
public class Fact {

   private String id;
   private String classUri;

   private String name;
   private String value;

   private Map<String,List<Fact>> relatedFacts;

   private List<Mention> directEvidence;

   private List<Integer> confidenceFeatures;
   private Integer confidence;

   public String getId() {
      return id;
   }

   public void setId( final String id ) {
      this.id = id;
   }
   public String getName() {
      return name;
   }

   public String getClassUri() {
      return classUri;
   }

   public void setClassUri( final String classUri ) {
      this.classUri = classUri;
   }

   public void setName( final String name ) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue( final String value ) {
      this.value = value;
   }

   public Map<String,List<Fact>> getRelatedFacts() {
      return relatedFacts;
   }

   public void setRelatedFacts( final Map<String,List<Fact>> relatedFacts ) {
      this.relatedFacts = relatedFacts;
   }

   public List<Mention> getDirectEvidence() {
      return directEvidence;
   }

   public void setDirectEvidence( final List<Mention> directEvidence ) {
      this.directEvidence = directEvidence;
   }

   public List<Integer> getConfidenceFeatures() {
      return confidenceFeatures;
   }

   public void setConfidenceFeatures( final List<Integer> confidenceFeatures ) {
      this.confidenceFeatures = confidenceFeatures;
   }

   public Integer getConfidence() {
      return confidence;
   }

   public void setConfidence( final Integer confidence ) {
      this.confidence = confidence;
   }

}
