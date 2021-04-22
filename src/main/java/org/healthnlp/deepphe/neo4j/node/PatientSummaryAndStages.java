package org.healthnlp.deepphe.neo4j.node;

import java.util.ArrayList;
import java.util.List;

public class PatientSummaryAndStages {
    private String patientId;
    private String patientName;
    private String birthDate;
    private String lastEncounterAge;
    private String firstEncounterAge;
    private String lastEncounterDate;
    private String getFirstEncounterDate;
    private List<String> stages;

    public List<String> getStages() {
        if (stages == null) {
            stages = new ArrayList<>();
        }
        return stages;
    }

    public void setStages(List<String> stages) {
        this.stages = stages;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getLastEncounterAge() {
        return lastEncounterAge;
    }

    public void setLastEncounterAge(String lastEncounterAge) {
        this.lastEncounterAge = lastEncounterAge;
    }

    public String getFirstEncounterAge() {
        return firstEncounterAge;
    }

    public void setFirstEncounterAge(String firstEncounterAge) {
        this.firstEncounterAge = firstEncounterAge;
    }

    public String getLastEncounterDate() {
        return lastEncounterDate;
    }

    public void setLastEncounterDate(String lastEncounterDate) {
        this.lastEncounterDate = lastEncounterDate;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getGetFirstEncounterDate() {
        return getFirstEncounterDate;
    }

    public void setGetFirstEncounterDate(String getFirstEncounterDate) {
        this.getFirstEncounterDate = getFirstEncounterDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


}
