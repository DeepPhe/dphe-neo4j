package org.healthnlp.deepphe.neo4j.util;

import org.healthnlp.deepphe.neo4j.node.StructuredPatientData;

import java.util.Iterator;

public class RandomStructuredPatientDataIterator implements Iterator<StructuredPatientData> {

    private final StructuredPatientDataGenerator structuredPatientDataGenerator;

    public RandomStructuredPatientDataIterator(StructuredPatientDataGenerator structuredPatientDataGenerator) {
        this.structuredPatientDataGenerator = structuredPatientDataGenerator;
    }

    ;

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public StructuredPatientData next() {
        return structuredPatientDataGenerator.next();
    }
}
