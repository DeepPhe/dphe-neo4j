package org.healthnlp.deepphe.neo4j.node;

import java.util.List;

public class GuiPatientSummary {

        PatientInfo patientInfo;
        List<Report> reportData;


        public PatientInfo getPatientInfo() {
            return patientInfo;
        }

        public void setPatientInfo(PatientInfo patientInfo) {
            this.patientInfo = patientInfo;
        }

        public List<Report> getReportData() {
            return reportData;
        }

        public void setReportData(List<Report> reportData) {
            this.reportData = reportData;
        }



}

