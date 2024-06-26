package org.healthnlp.deepphe.neo4j.node;


import java.util.List;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 7/15/2020
 */
public class PatientSummary {

   private String id;
   private Patient patient;
   private List<NeoplasmSummary> neoplasms;

   public String getId() {
      return id;
   }

   public void setId( final String id ) {
      this.id = id;
   }

   public Patient getPatient() {
      return patient;
   }

   public void setPatient( final Patient patient ) {
      this.patient = patient;
   }

   public List<NeoplasmSummary> getNeoplasms() {
      return neoplasms;
   }

   public void setNeoplasms( final List<NeoplasmSummary> neoplasms ) {
      this.neoplasms = neoplasms;
   }


}
