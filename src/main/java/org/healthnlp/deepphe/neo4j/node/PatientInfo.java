package org.healthnlp.deepphe.neo4j.node;

public class PatientInfo {
    private String patientId;
    private String patientName;
    private String birthDate;
    private String lastEncounterAge;
    private String firstEncounterAge;
    private String lastEncounterDate;
    private String getFirstEncounterDate;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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

    public String getGetFirstEncounterDate() {
        return getFirstEncounterDate;
    }

    public void setGetFirstEncounterDate(String getFirstEncounterDate) {
        this.getFirstEncounterDate = getFirstEncounterDate;
    }
}
