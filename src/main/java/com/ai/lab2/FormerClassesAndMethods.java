package com.ai.lab2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static com.ai.lab2.Solution.extractComparableElements;
import static com.ai.lab2.Solution.twoElementOperatorNegationComparison;
import static com.ai.utils.Lab_Utils.trimByOperator;
import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class FormerClassesAndMethods {


    private static HashMap<String, HashMap<ArrayList<LogicalElement>, Boolean>> logicalElAndOperator = new HashMap<>();
    private static ArrayList<Boolean> operators = new ArrayList<>();

    private static void checkForOperators(String line, String elementOrExpression) {
        if (elementOrExpression.contains(orOperator)) {
            boolean operatorEl = setupElements(elementOrExpression, orOperator, true, false);
            operators.add(operatorEl);
            System.out.println(line + " has as result: " + operatorEl + "\n");
        } else if (elementOrExpression.contains(andOperator)) {
            boolean operatorEl = setupElements(elementOrExpression, andOperator, false, true);
            operators.add(operatorEl);
            System.out.println(line + " has as result: " + operatorEl + "\n");
        } else if (elementOrExpression.contains(implyOperator)) {
            boolean operatorEl = setupElements(elementOrExpression, implyOperator, false, false);
            operators.add(operatorEl);
            System.out.println(line + " has as result: " + operatorEl + "\n");
        } else if (elementOrExpression.contains(equivalent)) {
            boolean operatorEl = setupElements(elementOrExpression, equivalent, false, false);
            operators.add(operatorEl);
            System.out.println(line + " has as result: " + operatorEl + "\n");
        }
    }

    private static boolean setupElements(String element, String logicOperator, boolean orOperatorToTest,
                                         boolean andOperatorToTest) {
        ArrayList<LogicalElement> elements = new ArrayList<>();
        String[] comparableOperators = trimByOperator(element, logicOperator);
        for (String comp : comparableOperators) {
            elements.add(new LogicalElement(comp));
        }

        elements.get(0).setTorF(true);
        elements.get(1).setTorF(false);

        System.out.println("Assuming Element 0 as " + elements.get(0).getTorF());
        System.out.println("Assuming Element 1 as " + elements.get(1).getTorF());
        ComparableElement comp = new ComparableElement(elements, orOperatorToTest, andOperatorToTest);
        boolean checkOperators = comp.checkOperators(element);

        HashMap<ArrayList<LogicalElement>, Boolean> elemOperators = new HashMap<>();
        elemOperators.put(elements, checkOperators);

        logicalElAndOperator.put(element, elemOperators);
        return checkOperators;
    }


    public static boolean functionPlResolution(LinkedList<LogicalElement> premises, String regex, String endGoal) {
//        LinkedList<LogicalElement> clauses = cnfConvert(premises && negationElement(endGoal));
//        LinkedList<LogicalElement> newListOfClauses = new LinkedList<>();
//
//        foreach(c1, c2) in selectClasuses (clauses) {
//                LinkedList < LogicalElement > resolvents = plResolve(c1, c2);
//
//        if (NIL apartine resolvents)return true;
//        newListOfClauses = newListOfClauses.add(resolvents);
//        }
//        if (newListOfClauses is inOrEquals clauses)return false;
//        clauses = clauses.add(newListOfClauses)
        return true;
    }

    public void bla() {
//        k =[] - empty list
//        O/ taiet = empty set;
//if length list/sets is different return null
//if(k1 is variable) - can be replaced with w/ever return substitution where k2 replaces k1
//        if k1 is element of k2  it tries to replace something with itself
//        (replaces k1-variable with a term  k2 that contains k1then return null
//        vice-versa for k2
//if neither k1 nor k2 is variable  return null


    }


    private static String differentOp(String firstClause, String secondClause, String firstOp, String secondOp) {
        String singleWithExpression = null;
        String operator = "";
        if (secondClause.contains(orOperator) || secondClause.contains(lowerCase(orOperator))) {
            operator = lowerCase(orOperator);
        } else if (secondClause.contains(andOperator)) {
            operator = andOperator;
        }

        ArrayList<LogicalElement> elemAndNegation = new ArrayList<>();

        String[] comparableOperatorsFirstClause = trimByOperator(firstClause, firstOp);
        extractComparableElements(elemAndNegation, comparableOperatorsFirstClause);

        String[] comparableOperatorsSecondClause = trimByOperator(secondClause, secondOp);
        extractComparableElements(elemAndNegation, comparableOperatorsSecondClause);

        LogicalElement firstElemN = elemAndNegation.get(0);
        LogicalElement secondElemN = elemAndNegation.get(1);
        LogicalElement thirdElemN = elemAndNegation.get(2);
        LogicalElement forthElemN = elemAndNegation.get(3);

        String firstElementToTest = firstElemN.getElementName();
        String secondElementToTest = secondElemN.getElementName();
        String thirdElementToTest = thirdElemN.getElementName();
        String forthElementToTest = forthElemN.getElementName();

        if (firstElementToTest.equals(thirdElementToTest)) {
            if (firstElemN.hasNegation() != thirdElemN.hasNegation()) {
                singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression,
                        secondElemN, forthElemN);
                String s = "s";
            }
            String s = "s";
        } else if (firstElementToTest.equals(forthElementToTest)) {
            if (firstElemN.hasNegation() != forthElemN.hasNegation()) {
                singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression,
                        secondElemN, thirdElemN);
                String s = "s";
            }
            String s = "s";
        }

        if (secondElementToTest.equals(thirdElementToTest)) {
            if (secondElemN.hasNegation() != thirdElemN.hasNegation()) {
                singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression,
                        firstElemN, forthElemN);
                String s = "s";
            }
            String s = "s";

        } else if (secondElementToTest.equals(forthElementToTest)) {
            if (secondElemN.hasNegation() != forthElemN.hasNegation()) {
                singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression,
                        firstElemN, thirdElemN);
                String s = "s";
            }
            String s = "s";
        }
        return singleWithExpression;

    }

    private static String commonOp(String firstClause, String secondClause, String operator) {
        String singleWithExpression;
        ArrayList<LogicalElement> elemAndNegation = new ArrayList<>();

        String[] comparableOperatorsFirstClause = trimByOperator(firstClause, operator);
        extractComparableElements(elemAndNegation, comparableOperatorsFirstClause);

        String[] comparableOperatorsSecondClause = trimByOperator(secondClause, operator);
        extractComparableElements(elemAndNegation, comparableOperatorsSecondClause);

        LogicalElement firstElemN = elemAndNegation.get(0);
        LogicalElement secondElemN = elemAndNegation.get(1);
        LogicalElement thirdElemN = elemAndNegation.get(2);
        LogicalElement forthElemN = elemAndNegation.get(3);

        if (firstElemN.getElementName().equals(thirdElemN.getElementName())) {
            if (!firstElemN.getOperator().equals(thirdElemN.getOperator())) {
                elemAndNegation.remove(firstElemN);
                elemAndNegation.remove(thirdElemN);
            }
        } else if (firstElemN.getElementName().equals(forthElemN.getElementName())) {
            if (!firstElemN.getOperator().equals(forthElemN.getOperator())) {
                elemAndNegation.remove(firstElemN);
                elemAndNegation.remove(forthElemN);
            }
        } else if (secondElemN.getElementName().equals(thirdElemN.getElementName())) {
            if (!secondElemN.getOperator().equals(thirdElemN.getOperator())) {
                elemAndNegation.remove(secondElemN);
                elemAndNegation.remove(thirdElemN);
            }
        } else if (secondElemN.getElementName().equals(forthElemN.getElementName())) {
            if (!secondElemN.getOperator().equals(forthElemN.getOperator())) {
                elemAndNegation.remove(secondElemN);
                elemAndNegation.remove(forthElemN);
            }
        }

        singleWithExpression = elemAndNegation.get(0).getOperator() + elemAndNegation.get(0).getElementName() + operator +
                elemAndNegation.get(1).getOperator() + elemAndNegation.get(1).getElementName();

        return singleWithExpression;
    }

    private static String compareSingleClauseWithElement(String operator, String firstNegOrNot, LogicalElement firstClause,
                                                         LogicalElement firstElementFromSecondClause, LogicalElement secondElementFromSecondClause, boolean secondElHasNegation) {
        String singleWithExpression;
        if (firstClause.hasNegation() != firstElementFromSecondClause.hasNegation()) {
            if (secondElementFromSecondClause.hasNegation()) {
                singleWithExpression = negateTheValue + secondElementFromSecondClause.getElementName();
            } else {
                singleWithExpression = secondElementFromSecondClause.getElementName();
            }
        } else {
            if (secondElHasNegation) {
                singleWithExpression = hasMatchingLiteral_NextPairs + firstNegOrNot + firstClause.getElementName() + operator
                        + negateTheValue + secondElementFromSecondClause.getElementName();
            } else {
                singleWithExpression = hasMatchingLiteral_NextPairs + firstNegOrNot + firstClause.getElementName() + operator
                        + secondElementFromSecondClause.getElementName();
            }
        }
        return singleWithExpression;
    }


}
