package org.healthnlp.deepphe.neo4j.node;

import java.util.List;

public class FactInfoAndGroupedTextProvenances {
    public FactInfo getSourceFact() {
        return sourceFact;
    }

    public void setSourceFact(FactInfo sourceFact) {
        this.sourceFact = sourceFact;
    }

    FactInfo sourceFact;
    List<MentionedTerm> mentionedTerms;

    public List<MentionedTerm> getMentionedTerms() {
        return mentionedTerms;
    }

    public void setMentionedTerms(List<MentionedTerm> mentionedTerms) {
        this.mentionedTerms = mentionedTerms;
    }
}
