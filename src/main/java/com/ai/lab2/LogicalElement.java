package com.ai.lab2;

public class LogicalElement {
    private String elementName;
    private String operator;
    private boolean TorF;
    private boolean hasNegation;
    private boolean hasDoubleNegation;
    private boolean hasTripleNegation;

    public LogicalElement(String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public boolean getTorF() {
        return TorF;
    }

    public void setTorF(boolean torF) {
        TorF = torF;
    }

    public boolean isTorF() {
        return TorF;
    }

    public boolean hasNegation() {
        return hasNegation;
    }

    public void setHasNegation(boolean hasNegation) {
        this.hasNegation = hasNegation;
    }

    public boolean hasDoubleNegation() {
        return hasDoubleNegation;
    }

    public void setHasDoubleNegation(boolean hasDoubleNegation) {
        this.hasDoubleNegation = hasDoubleNegation;
    }

    public boolean hasTripleNegation() {
        return hasTripleNegation;
    }

    public void setHasTripleNegation(boolean hasTripleNegation) {
        this.hasTripleNegation = hasTripleNegation;
    }
}
