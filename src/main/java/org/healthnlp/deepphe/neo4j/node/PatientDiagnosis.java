package org.healthnlp.deepphe.neo4j.node;

import java.util.List;

/**
 * @author SPF , chip-nlp
 * @since {4/27/2021}
 */
public class PatientDiagnosis {

   List<String> diagnosisGroups;
   List<String> diagnosis;
   String patientId;

   public List<String> getDiagnosisGroups() {
      return diagnosisGroups;
   }

   public void setDiagnosisGroups(List<String> diagnosisGroups) {
      this.diagnosisGroups = diagnosisGroups;
   }

   public List<String> getDiagnosis() {
      return diagnosis;
   }

   public void setDiagnosis(List<String> diagnosis) {
      this.diagnosis = diagnosis;
   }

   public String getPatientId() {
      return patientId;
   }

   public void setPatientId(String patientId) {
      this.patientId = patientId;
   }

}
