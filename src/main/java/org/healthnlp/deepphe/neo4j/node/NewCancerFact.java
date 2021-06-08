package org.healthnlp.deepphe.neo4j.node;

public class NewCancerFact extends NewFact {

    NewFactInfo cancerFactInfo;

    public NewFactInfo getCancerFactInfo() {
        return cancerFactInfo;
    }

    public void setCancerFactInfo(NewFactInfo cancerFactInfo) {
        this.cancerFactInfo = cancerFactInfo;
    }
}
