package com.ai.lab2;

import java.util.ArrayList;

import static com.ai.utils.RegexOperator.*;

public class ComparableElement {
    private ArrayList<LogicalElement> logicalElements;
    private boolean logicalOperator;
    private boolean finalOperator;
    private boolean orOp;
    private boolean andOp;
    private boolean isNegatedOrNot;

    public ComparableElement() {
    }

    public ComparableElement(ArrayList<LogicalElement> logicalElements, boolean orOp, boolean andOp) {
        this.logicalElements = logicalElements;
        this.orOp = orOp;
        this.andOp = andOp;
    }

    public boolean isNegatedOrNot() {
        return isNegatedOrNot;
    }

    public void setNegatedOrNot(boolean negatedOrNot) {
        isNegatedOrNot = negatedOrNot;
    }

    public boolean isFinalOperator() {
        return finalOperator;
    }

    public void setFinalOperator(boolean finalOperator) {
        this.finalOperator = finalOperator;
    }

    public boolean isOrOp() {
        return orOp;
    }

    public void setOrOp(boolean orOp) {
        this.orOp = orOp;
    }

    public boolean isAndOp() {
        return andOp;
    }

    public void setAndOp(boolean andOp) {
        this.andOp = andOp;
    }

    public ArrayList<LogicalElement> getLogicalElements() {
        return logicalElements;
    }

    public void setLogicalElements(ArrayList<LogicalElement> logicalElements) {
        this.logicalElements = logicalElements;
    }

    public boolean isLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(boolean logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    private void checkNegation(LogicalElement first, LogicalElement second) {
//
//        if (!first.hasTripleNegation() && !second.hasTripleNegation()) {
//            first.setHasNegation(false);
//            second.setHasNegation(false);
//        } else if (first.hasTripleNegation() && !second.hasTripleNegation()) {
//            first.setHasNegation(true);
//            second.setHasNegation(false);
////            first.setTorF(!first.getTorF());
//
//        } else if (!first.hasTripleNegation() && second.hasTripleNegation()) {
//            first.setHasNegation(false);
//            second.setHasNegation(true);
////            second.setTorF(!second.getTorF());
//        }

        if (!first.hasDoubleNegation() && !second.hasDoubleNegation()) {
            noDoubleNegationPresent(first, second);
        } else if (first.hasDoubleNegation() && !second.hasDoubleNegation()) {
            firstDoubleNegation(first, second);

        } else if (!first.hasDoubleNegation() && second.hasDoubleNegation()) {
            secondDoubleNegation(first, second);
        }
    }

    private void noDoubleNegationPresent(LogicalElement first, LogicalElement second) {
        //No Triple Negation or Normal Negation
        if ((!first.getElementName().contains(negateTheValue) && !second.getElementName().contains(negateTheValue))
                && (!first.hasTripleNegation() && !second.hasTripleNegation())) {
            first.setHasNegation(false);
            second.setHasNegation(false);
        }
        //No Triple Negation but both have Normal Negation
        else if ((first.getElementName().contains(negateTheValue) && second.getElementName().contains(negateTheValue))
                && (!first.hasTripleNegation() && !second.hasTripleNegation())) {
            first.setHasNegation(true);
            second.setHasNegation(true);
            first.setTorF(!first.getTorF());
            second.setTorF(!second.getTorF());
        }
        //Triple Negation on both but No Normal Negation
        else if ((!first.getElementName().contains(negateTheValue) && !second.getElementName().contains(negateTheValue))
                && (first.hasTripleNegation() && second.hasTripleNegation())) {
            first.setHasNegation(true);
            second.setHasNegation(true);
            first.setTorF(!first.getTorF());
            second.setTorF(!second.getTorF());
        }
        //No Triple Negation but first has Normal Negation
        else if (first.getElementName().contains(negateTheValue)
                && !first.hasTripleNegation() && !second.hasTripleNegation()) {
            first.setHasNegation(true);
            second.setHasNegation(false);
            first.setTorF(!first.getTorF());
        }
        //No Triple Negation but second has Normal Negation
        else if (second.getElementName().contains(negateTheValue)
                && !first.hasTripleNegation() && !second.hasTripleNegation()) {
            first.setHasNegation(false);
            second.setHasNegation(true);
            second.setTorF(!second.getTorF());
        }
        //First Triple Negation,second is Not; but second has Normal Negation
        else if (first.hasTripleNegation() && !second.hasTripleNegation()) {
            first.setHasNegation(true);
            second.setHasNegation(true);
            first.setTorF(!first.getTorF());
            second.setTorF(!second.getTorF());
        }
        //Second Triple Negation,first is Not; but first has Normal Negation
        else if (!first.hasTripleNegation() && second.hasTripleNegation()) {
            first.setHasNegation(true);
            second.setHasNegation(true);
            first.setTorF(!first.getTorF());
            second.setTorF(!second.getTorF());
        }

        //First Triple Negation,second is Not; also neither has Normal Negation
        else if (first.hasTripleNegation() && !second.hasTripleNegation()) {
            first.setHasNegation(true);
            second.setHasNegation(true);
            first.setTorF(!first.getTorF());
            second.setTorF(!second.getTorF());
        }
        //Second Triple Negation,first is Not; also neither has Normal Negation
        else if (!first.hasTripleNegation() && second.hasTripleNegation()) {
            first.setHasNegation(true);
            second.setHasNegation(true);
            first.setTorF(!first.getTorF());
            second.setTorF(!second.getTorF());
        }

    }

    private void firstDoubleNegation(LogicalElement first, LogicalElement second) {
        if (!second.getElementName().contains(negateTheValue) && !second.hasTripleNegation()) {
            first.setHasNegation(false);
            second.setHasNegation(false);
        } else if (second.getElementName().contains(negateTheValue) || second.hasTripleNegation()) {
            first.setHasNegation(false);
            second.setHasNegation(true);
            second.setTorF(!second.getTorF());
        }
    }

    private void secondDoubleNegation(LogicalElement first, LogicalElement second) {
        if (!first.getElementName().contains(negateTheValue) && !first.hasTripleNegation()) {
            first.setHasNegation(false);
            second.setHasNegation(false);
        } else if (first.getElementName().contains(negateTheValue) || first.hasTripleNegation()) {
            first.setHasNegation(true);
            second.setHasNegation(false);
            first.setTorF(!first.getTorF());
        }
    }


    private void checkDoubleNegation(LogicalElement first, LogicalElement second) {
        if (!first.hasTripleNegation() && !second.hasTripleNegation()) {
            if (first.getElementName().contains(doubleNegationOp)
                    && second.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(true);
                second.setHasDoubleNegation(true);
                checkNegation(first, second);
            } else if (first.getElementName().contains(doubleNegationOp)
                    && !second.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(true);
                second.setHasDoubleNegation(false);
                checkNegation(first, second);
            } else if (!first.getElementName().contains(doubleNegationOp)
                    && second.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(false);
                second.setHasDoubleNegation(true);
                checkNegation(first, second);
            } else if (!first.getElementName().contains(doubleNegationOp)
                    && !second.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(false);
                second.setHasDoubleNegation(false);
                checkNegation(first, second);
            }
        } else if (first.hasTripleNegation() && !second.hasTripleNegation()) {
            if (second.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(false);
                second.setHasDoubleNegation(true);
                checkNegation(first, second);
            } else if (!second.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(false);
                second.setHasDoubleNegation(false);
                checkNegation(first, second);
            }

        } else if (!first.hasTripleNegation() && second.hasTripleNegation()) {
            if (first.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(true);
                second.setHasDoubleNegation(false);
                checkNegation(first, second);
            } else if (!first.getElementName().contains(doubleNegationOp)) {
                first.setHasDoubleNegation(false);
                second.setHasDoubleNegation(false);
                checkNegation(first, second);
            }

        }
    }

    private void checkTripleNegation(LogicalElement first, LogicalElement second) {
        if (first.getElementName().contains(tripleNegationOp)
                && second.getElementName().contains(tripleNegationOp)) {
            first.setHasTripleNegation(true);
            second.setHasTripleNegation(true);
            checkDoubleNegation(first, second); //~~ = double negation of elem.
        } else if (first.getElementName().contains(tripleNegationOp)
                && !second.getElementName().contains(tripleNegationOp)) {
            first.setHasTripleNegation(true);
            second.setHasTripleNegation(false);
            checkDoubleNegation(first, second); //~~ = double negation of elem.
        } else if (!first.getElementName().contains(tripleNegationOp)
                && second.getElementName().contains(tripleNegationOp)) {
            first.setHasTripleNegation(false);
            second.setHasTripleNegation(true);
            checkDoubleNegation(first, second); //~~ = double negation of elem.
        } else if (!first.getElementName().contains(tripleNegationOp)
                && !second.getElementName().contains(tripleNegationOp)) {
            first.setHasTripleNegation(false);
            second.setHasTripleNegation(false);
            checkDoubleNegation(first, second); //~~ = double negation of elem.
        }
    }

    public boolean checkOperators(String regex) {
        LogicalElement firstElement = logicalElements.get(0);
        LogicalElement secondElement = logicalElements.get(1);

        checkTripleNegation(firstElement, secondElement); //~~~ = triple negation of elem. <=> Simple negation
        if (this.isOrOp()) {
            return firstElement.getTorF() || secondElement.getTorF(); // or = v
        } else if (this.isAndOp()) {
            return firstElement.getTorF() && secondElement.getTorF(); // and = ^
        } else if (regex.contains(implyOperator)) {
            return !firstElement.getTorF() || secondElement.getTorF(); //~ = negation of first elem = implication elim.
        } else if (regex.contains(equivalent)) {
            return (firstElement.getTorF() && secondElement.getTorF())
                    || (!firstElement.getTorF() && !secondElement.getTorF()); //"=" <=> equivalent.
        }

        return false;
    }

}
