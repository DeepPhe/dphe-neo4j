package org.healthnlp.deepphe.neo4j.node;

import java.util.List;

/**
 * @author SPF , chip-nlp
 * @since {3/8/2021}
 */
public class CancerSummary extends NeoplasmSummary {

    String cancerId;
    List<CancerFact> cancerFacts;
    List<TumorSummary> tumors;


    public String getCancerId() {
        return cancerId;
    }

    public void setCancerId(String cancerId) {
        this.cancerId = cancerId;
    }

    public List<CancerFact> getCancerFacts() {
        return cancerFacts;
    }

    public void setCancerFacts(List<CancerFact> cancerFacts) {
        this.cancerFacts = cancerFacts;
    }

    public List<TumorSummary> getTumors() {
        return tumors;
    }

    public void setTumors(List<TumorSummary> tumors) {
        this.tumors = tumors;
    }

}