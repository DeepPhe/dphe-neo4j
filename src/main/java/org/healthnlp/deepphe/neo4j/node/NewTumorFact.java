package org.healthnlp.deepphe.neo4j.node;

public class NewTumorFact extends NewFact {
    public NewCancerFactInfo getTumorFactInfo() {
        return tumorFactInfo;
    }

    public void setTumorFactInfo(NewCancerFactInfo tumorFactInfo) {
        this.tumorFactInfo = tumorFactInfo;
    }

    NewCancerFactInfo tumorFactInfo;
}
