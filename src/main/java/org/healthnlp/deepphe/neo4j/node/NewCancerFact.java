package org.healthnlp.deepphe.neo4j.node;

public class NewCancerFact extends NewFact {

    NewCancerFactInfo cancerFactInfo;

    public NewCancerFactInfo getCancerFactInfo() {
        return cancerFactInfo;
    }

    public void setCancerFactInfo(NewCancerFactInfo cancerFactInfo) {
        this.cancerFactInfo = cancerFactInfo;
    }

}
