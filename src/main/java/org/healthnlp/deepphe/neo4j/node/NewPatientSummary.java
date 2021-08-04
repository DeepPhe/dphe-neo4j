package org.healthnlp.deepphe.neo4j.node;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class NewPatientSummary {
    PatientInfo patientInfo;
    List<NewReport> reportData;


    public PatientInfo getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
    }

    public List<NewReport> getReportData() {
        return reportData;
    }

    public void setReportData(List<NewReport> reportData) {
        this.reportData = reportData;
    }



  //  public static final String json = "{\"patientInfo\": {\"patientName\": \"John Smith\",\"lastEncounterAge\": \"55\",\"firstEncounterAge\": \"48\",\"lastEncounterDate\": \"2015-01-06\",\"patientId\": \"patient04\",\"firstEncounterDate\": \"2010-01-01\",\"birthDate\": \"1958-05-01\" }, \"reportData\": [{\"id\": \"patient04_report056_RAD\",\"date\": \"2013/03/28\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report055_NOTE\",\"date\": \"2013/03/20\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report054_RAD\",\"date\": \"2013/02/13\",\"type\": \"Radiology report\",\"episode\": \"Follow-up\"},{\"id\": \"patient04_report053_NOTE\",\"date\": \"2013/02/11\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report051_SP\",\"date\": \"2012/12/17\",\"type\": \"Surgical pathology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report052_NOTE\",\"date\": \"2012/12/17\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report050_RAD\",\"date\": \"2012/10/23\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report049_NOTE\",\"date\": \"2012/10/23\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report048_NOTE\",\"date\": \"2012/10/22\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report046_NOTE\",\"date\": \"2012/10/15\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report047_NOTE\",\"date\": \"2012/10/15\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report045_NOTE\",\"date\": \"2012/10/12\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report044_NOTE\",\"date\": \"2012/09/25\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report043_NOTE\",\"date\": \"2012/09/25\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report042_NOTE\",\"date\": \"2012/09/24\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report041_NOTE\",\"date\": \"2012/08/09\",\"type\": \"Clinical note\",\"episode\": \"Medical Decision-making\"},{\"id\": \"patient04_report040_NOTE\",\"date\": \"2012/07/16\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report039_NOTE\",\"date\": \"2012/06/25\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report038_NOTE\",\"date\": \"2012/06/04\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report037_NOTE\",\"date\": \"2012/06/04\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report036_NOTE\",\"date\": \"2012/05/14\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report035_NOTE\",\"date\": \"2012/04/23\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report034_RAD\",\"date\": \"2012/04/02\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report033_NOTE\",\"date\": \"2012/04/02\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report031_NOTE\",\"date\": \"2012/04/02\",\"type\": \"Clinical note\",\"episode\": \"Treatment\"},{\"id\": \"patient04_report032_NOTE\",\"date\": \"2012/04/02\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report030_RAD\",\"date\": \"2012/03/22\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report029_RAD\",\"date\": \"2012/03/22\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report027_NOTE\",\"date\": \"2012/03/21\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report028_NOTE\",\"date\": \"2012/03/21\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report026_NOTE\",\"date\": \"2012/03/12\",\"type\": \"Clinical note\",\"episode\": \"Medical Decision-making\"},{\"id\": \"patient04_report025_NOTE\",\"date\": \"2012/03/05\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report024_PGN\",\"date\": \"2012/03/01\",\"type\": \"Progress note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report023_DS\",\"date\": \"2012/02/29\",\"type\": \"Discharge summary\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report022_PGN\",\"date\": \"2012/02/29\",\"type\": \"Progress note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report020_NOTE\",\"date\": \"2012/02/29\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report021_NOTE\",\"date\": \"2012/02/29\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report019_PGN\",\"date\": \"2012/02/28\",\"type\": \"Progress note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report018_SP\",\"date\": \"2012/02/28\",\"type\": \"Surgical pathology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report015_NOTE\",\"date\": \"2012/02/20\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report017_RAD\",\"date\": \"2012/02/20\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report016_NOTE\",\"date\": \"2012/02/20\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report010_SP\",\"date\": \"2012/02/14\",\"type\": \"Surgical pathology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report011_RAD\",\"date\": \"2012/02/14\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report014_RAD\",\"date\": \"2012/02/14\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report012_RAD\",\"date\": \"2012/02/14\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report013_RAD\",\"date\": \"2012/02/14\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report009_RAD\",\"date\": \"2012/02/03\",\"type\": \"Radiology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report008_NOTE\",\"date\": \"2012/02/02\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report007_NOTE\",\"date\": \"2012/02/02\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report006_NOTE\",\"date\": \"2012/02/02\",\"type\": \"Clinical note\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report003_SP\",\"date\": \"2012/01/24\",\"type\": \"Surgical pathology report\",\"episode\": \"Unknown\"},{\"id\": \"patient04_report002_RAD\",\"date\": \"2012/01/24\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report005_RAD\",\"date\": \"2012/01/24\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report004_RAD\",\"date\": \"2012/01/24\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"},{\"id\": \"patient04_report001_RAD\",\"date\": \"2012/01/11\",\"type\": \"Radiology report\",\"episode\": \"Pre-diagnostic\"} ], \"typeCounts\": {\"Radiology report\": 16,\"Clinical note\": 32,\"Surgical pathology report\": 4,\"Progress note\": 3,\"Discharge summary\": 1 }, \"episodes\": [\"Pre-diagnostic\",\"Medical Decision-making\",\"Treatment\",\"Follow-up\",\"Unknown\" ], \"episodeCounts\": {\"Unknown\": 33,\"Follow-up\": 1,\"Treatment\": 12,\"Medical Decision-making\": 2,\"Pre-diagnostic\": 8 }, \"reportTypes\": [\"Clinical note\",\"Radiology report\",\"Surgical pathology report\",\"Progress note\",\"Discharge summary\" ], \"episodeDates\": {\"Unknown\": [\"2013/03/28\",\"2013/03/20\",\"2012/12/17\",\"2012/12/17\",\"2012/10/23\",\"2012/10/23\",\"2012/10/15\",\"2012/10/12\",\"2012/09/25\",\"2012/09/25\",\"2012/04/02\",\"2012/04/02\",\"2012/03/22\",\"2012/03/22\",\"2012/03/21\",\"2012/03/21\",\"2012/03/05\",\"2012/03/01\",\"2012/02/29\",\"2012/02/29\",\"2012/02/29\",\"2012/02/29\",\"2012/02/28\",\"2012/02/28\",\"2012/02/20\",\"2012/02/20\",\"2012/02/20\",\"2012/02/14\",\"2012/02/03\",\"2012/02/02\",\"2012/02/02\",\"2012/02/02\",\"2012/01/24\"],\"Follow-up\": [\"2013/02/13\"],\"Treatment\": [\"2013/02/11\",\"2012/10/22\",\"2012/10/15\",\"2012/09/24\",\"2012/07/16\",\"2012/06/25\",\"2012/06/04\",\"2012/06/04\",\"2012/05/14\",\"2012/04/23\",\"2012/04/02\",\"2012/04/02\"],\"Medical Decision-making\": [\"2012/08/09\",\"2012/03/12\"],\"Pre-diagnostic\": [\"2012/02/14\",\"2012/02/14\",\"2012/02/14\",\"2012/02/14\",\"2012/01/24\",\"2012/01/24\",\"2012/01/24\",\"2012/01/11\"] }, \"reportsGroupedByDateAndTypeObj\": {\"2013/03/28\": {\"Radiology report\": [   {  \"id\": \"patient04_report056_RAD\",  \"date\": \"2013/03/28\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   }]},\"2013/03/20\": {\"Clinical note\": [   {  \"id\": \"patient04_report055_NOTE\",  \"date\": \"2013/03/20\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2013/02/13\": {\"Radiology report\": [   {  \"id\": \"patient04_report054_RAD\",  \"date\": \"2013/02/13\",  \"type\": \"Radiology report\",  \"episode\": \"Follow-up\"   }]},\"2013/02/11\": {\"Clinical note\": [   {  \"id\": \"patient04_report053_NOTE\",  \"date\": \"2013/02/11\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/12/17\": {\"Surgical pathology report\": [   {  \"id\": \"patient04_report051_SP\",  \"date\": \"2012/12/17\",  \"type\": \"Surgical pathology report\",  \"episode\": \"Unknown\"   }],\"Clinical note\": [   {  \"id\": \"patient04_report052_NOTE\",  \"date\": \"2012/12/17\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/10/23\": {\"Radiology report\": [   {  \"id\": \"patient04_report050_RAD\",  \"date\": \"2012/10/23\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   }],\"Clinical note\": [   {  \"id\": \"patient04_report049_NOTE\",  \"date\": \"2012/10/23\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/10/22\": {\"Clinical note\": [   {  \"id\": \"patient04_report048_NOTE\",  \"date\": \"2012/10/22\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/10/15\": {\"Clinical note\": [   {  \"id\": \"patient04_report046_NOTE\",  \"date\": \"2012/10/15\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   },   {  \"id\": \"patient04_report047_NOTE\",  \"date\": \"2012/10/15\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/10/12\": {\"Clinical note\": [   {  \"id\": \"patient04_report045_NOTE\",  \"date\": \"2012/10/12\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/09/25\": {\"Clinical note\": [   {  \"id\": \"patient04_report044_NOTE\",  \"date\": \"2012/09/25\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report043_NOTE\",  \"date\": \"2012/09/25\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/09/24\": {\"Clinical note\": [   {  \"id\": \"patient04_report042_NOTE\",  \"date\": \"2012/09/24\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/08/09\": {\"Clinical note\": [   {  \"id\": \"patient04_report041_NOTE\",  \"date\": \"2012/08/09\",  \"type\": \"Clinical note\",  \"episode\": \"Medical Decision-making\"   }]},\"2012/07/16\": {\"Clinical note\": [   {  \"id\": \"patient04_report040_NOTE\",  \"date\": \"2012/07/16\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/06/25\": {\"Clinical note\": [   {  \"id\": \"patient04_report039_NOTE\",  \"date\": \"2012/06/25\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/06/04\": {\"Clinical note\": [   {  \"id\": \"patient04_report038_NOTE\",  \"date\": \"2012/06/04\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   },   {  \"id\": \"patient04_report037_NOTE\",  \"date\": \"2012/06/04\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/05/14\": {\"Clinical note\": [   {  \"id\": \"patient04_report036_NOTE\",  \"date\": \"2012/05/14\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/04/23\": {\"Clinical note\": [   {  \"id\": \"patient04_report035_NOTE\",  \"date\": \"2012/04/23\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   }]},\"2012/04/02\": {\"Radiology report\": [   {  \"id\": \"patient04_report034_RAD\",  \"date\": \"2012/04/02\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   }],\"Clinical note\": [   {  \"id\": \"patient04_report033_NOTE\",  \"date\": \"2012/04/02\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   },   {  \"id\": \"patient04_report031_NOTE\",  \"date\": \"2012/04/02\",  \"type\": \"Clinical note\",  \"episode\": \"Treatment\"   },   {  \"id\": \"patient04_report032_NOTE\",  \"date\": \"2012/04/02\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/03/22\": {\"Radiology report\": [   {  \"id\": \"patient04_report030_RAD\",  \"date\": \"2012/03/22\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report029_RAD\",  \"date\": \"2012/03/22\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   }]},\"2012/03/21\": {\"Clinical note\": [   {  \"id\": \"patient04_report027_NOTE\",  \"date\": \"2012/03/21\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report028_NOTE\",  \"date\": \"2012/03/21\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/03/12\": {\"Clinical note\": [   {  \"id\": \"patient04_report026_NOTE\",  \"date\": \"2012/03/12\",  \"type\": \"Clinical note\",  \"episode\": \"Medical Decision-making\"   }]},\"2012/03/05\": {\"Clinical note\": [   {  \"id\": \"patient04_report025_NOTE\",  \"date\": \"2012/03/05\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/03/01\": {\"Progress note\": [   {  \"id\": \"patient04_report024_PGN\",  \"date\": \"2012/03/01\",  \"type\": \"Progress note\",  \"episode\": \"Unknown\"   }]},\"2012/02/29\": {\"Discharge summary\": [   {  \"id\": \"patient04_report023_DS\",  \"date\": \"2012/02/29\",  \"type\": \"Discharge summary\",  \"episode\": \"Unknown\"   }],\"Progress note\": [   {  \"id\": \"patient04_report022_PGN\",  \"date\": \"2012/02/29\",  \"type\": \"Progress note\",  \"episode\": \"Unknown\"   }],\"Clinical note\": [   {  \"id\": \"patient04_report020_NOTE\",  \"date\": \"2012/02/29\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report021_NOTE\",  \"date\": \"2012/02/29\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/02/28\": {\"Progress note\": [   {  \"id\": \"patient04_report019_PGN\",  \"date\": \"2012/02/28\",  \"type\": \"Progress note\",  \"episode\": \"Unknown\"   }],\"Surgical pathology report\": [   {  \"id\": \"patient04_report018_SP\",  \"date\": \"2012/02/28\",  \"type\": \"Surgical pathology report\",  \"episode\": \"Unknown\"   }]},\"2012/02/20\": {\"Clinical note\": [   {  \"id\": \"patient04_report015_NOTE\",  \"date\": \"2012/02/20\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report016_NOTE\",  \"date\": \"2012/02/20\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }],\"Radiology report\": [   {  \"id\": \"patient04_report017_RAD\",  \"date\": \"2012/02/20\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   }]},\"2012/02/14\": {\"Surgical pathology report\": [   {  \"id\": \"patient04_report010_SP\",  \"date\": \"2012/02/14\",  \"type\": \"Surgical pathology report\",  \"episode\": \"Unknown\"   }],\"Radiology report\": [   {  \"id\": \"patient04_report011_RAD\",  \"date\": \"2012/02/14\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   },   {  \"id\": \"patient04_report014_RAD\",  \"date\": \"2012/02/14\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   },   {  \"id\": \"patient04_report012_RAD\",  \"date\": \"2012/02/14\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   },   {  \"id\": \"patient04_report013_RAD\",  \"date\": \"2012/02/14\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   }]},\"2012/02/03\": {\"Radiology report\": [   {  \"id\": \"patient04_report009_RAD\",  \"date\": \"2012/02/03\",  \"type\": \"Radiology report\",  \"episode\": \"Unknown\"   }]},\"2012/02/02\": {\"Clinical note\": [   {  \"id\": \"patient04_report008_NOTE\",  \"date\": \"2012/02/02\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report007_NOTE\",  \"date\": \"2012/02/02\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   },   {  \"id\": \"patient04_report006_NOTE\",  \"date\": \"2012/02/02\",  \"type\": \"Clinical note\",  \"episode\": \"Unknown\"   }]},\"2012/01/24\": {\"Surgical pathology report\": [   {  \"id\": \"patient04_report003_SP\",  \"date\": \"2012/01/24\",  \"type\": \"Surgical pathology report\",  \"episode\": \"Unknown\"   }],\"Radiology report\": [   {  \"id\": \"patient04_report002_RAD\",  \"date\": \"2012/01/24\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   },   {  \"id\": \"patient04_report005_RAD\",  \"date\": \"2012/01/24\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   },   {  \"id\": \"patient04_report004_RAD\",  \"date\": \"2012/01/24\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   }]},\"2012/01/11\": {\"Radiology report\": [   {  \"id\": \"patient04_report001_RAD\",  \"date\": \"2012/01/11\",  \"type\": \"Radiology report\",  \"episode\": \"Pre-diagnostic\"   }]} }, \"maxVerticalCountsPerType\": {\"Radiology report\": 4,\"Clinical note\": 3,\"Surgical pathology report\": 1,\"Progress note\": 1,\"Discharge summary\": 1 } }";

    public static void main(String[] args) {
//        Gson gson = new Gson();
//        NewPatientSummary newPatientSummary = gson.fromJson(json, NewPatientSummary.class);
//        System.out.println(newPatientSummary.patientInfo.getPatientName());
    }

}





