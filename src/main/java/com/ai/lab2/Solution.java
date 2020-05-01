package com.ai.lab2;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ai.utils.Lab_Utils.*;
import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.*;

public class Solution {

    static LinkedHashMap<String, HashMap<String, ArrayList<LogicalElement>>> mapForCNF = new LinkedHashMap<>();
    static LinkedHashMap<String, ArrayList<LogicalElement>> promiseWithElements = new LinkedHashMap<>();
    static AtomicBoolean noDerivationPossible = new AtomicBoolean();

    public static void main(String[] args) throws FileNotFoundException {
//        Scanner sc = new Scanner(new File(Constant.small_ex_1));
//        Scanner sc = new Scanner(new File(Constant.small_ex_2));
//        Scanner sc = new Scanner(new File(Constant.small_ex_3));
        Scanner sc = new Scanner(new File(Constant.small_ex_4));

        AtomicInteger count = new AtomicInteger();
        ArrayList<String> listOfPremises = new ArrayList<>();
        ArrayList<String> normalAndCNFResults = new ArrayList<>();

        while (sc.hasNext()) {
            String line = lowerCase(sc.nextLine());
            if (line.startsWith("#")) continue;
            if (line.trim().isEmpty()) continue;
            listOfPremises.add(line);
        }

        String lastElem = listOfPremises.get(listOfPremises.size() - 1);
        String initialGoal = lastElem;
        for (String currentPremise : listOfPremises) {
            ArrayList<LogicalElement> elementsToCompare = new ArrayList<>();

            String[] comparableOperators = trimByOperator(currentPremise, orOperator + "|" + andOperator
                    + "|" + andOperator + "|" + implyOperator + "|" + equivalent);
            List<String> compOper = Arrays.asList(comparableOperators);
            ArrayList<String> updatableList = new ArrayList<>(compOper);

            for (String comp : updatableList) {
                elementsToCompare.add(new LogicalElement(comp));
            }

            for (LogicalElement element : elementsToCompare) {
                if (currentPremise.contains(orOperator)) element.setOperator(orOperator);
                else if (currentPremise.contains(andOperator)) element.setOperator(andOperator);
                else if (currentPremise.contains(implyOperator)) element.setOperator(implyOperator);
                else if (currentPremise.contains(equivalent)) element.setOperator(equivalent);

                if (elementsToCompare.size() > 1) {
                    if (currentPremise.equals(lastElem)) {
                        currentPremise = negateTheValue + openParenthesis + elementsToCompare.get(0).getElementName()
                                + element.getOperator() + elementsToCompare.get(1).getElementName() + closeParenthesis;
                    }
                    currentPremise = cnfConvert(currentPremise, elementsToCompare, element.getOperator());
                }
                promiseWithElements.put(element.getOperator(), elementsToCompare);
                mapForCNF.put(currentPremise, promiseWithElements);
                if (!normalAndCNFResults.contains(currentPremise)) {
                    normalAndCNFResults.add(currentPremise);
                }
            }

        }

        System.out.println("=============");
        for (int i = count.get(); i < listOfPremises.size() - 1; i++) {
            count.getAndIncrement();
            String element = listOfPremises.get(i);
            System.out.println(i + ". " + element);
        }

        System.out.println("=============");
        String currentLast = normalAndCNFResults.get(normalAndCNFResults.size() - 1);
        if (currentLast.length() == 2 || currentLast.length() == 1) {
            System.out.println(count + ". " + negationElement(lastElem));
            System.out.println("=============");
        }

        String negatedElem = negationElement(lastElem);

        if (lastElem.length() == 1) {
            normalAndCNFResults.set(normalAndCNFResults.size() - 1, negatedElem);
        }
        if (lastElem.length() == 2) {
            if (negatedElem.contains(doubleNegationOp)) negatedElem = lastElem.replace(doubleNegationOp, "");
            normalAndCNFResults.set(normalAndCNFResults.size() - 1, negatedElem);
        }
        if (lastElem.length() > 2) {
            lastElem = normalAndCNFResults.get(normalAndCNFResults.size() - 1);
            normalAndCNFResults.set(normalAndCNFResults.size() - 1, lastElem);
        }

        String firstEl;
        String derivation = null;

        if (lastElem.length() == 1 || lastElem.length() == 2) {
            for (int i = normalAndCNFResults.size(); i > 0; i--) {
                count.getAndIncrement();
                int downTop = i - 1;
                firstEl = normalAndCNFResults.get(downTop);

                derivation = prepareSecondElementForDerivation(normalAndCNFResults, firstEl,
                        derivation, downTop, lastElem, count.get());
                if (noDerivationPossible.get()) {
                    break;
                }
                if (derivation.equals(NIL)) {
                    System.out.println("=============");
                    System.out.println(initialGoal + " is true");
                    break;
                }
            }
        }

        if (lastElem.length() > 2) {
            String[] elements = new String[0];
            if (lastElem.contains(orOperator)) {
                elements = trimByOperator(lastElem, orOperator);
            } else if (lastElem.contains(andOperator)) {
                elements = trimByOperator(lastElem, andOperator);
            }
            ArrayList<String> initialCNFResults = new ArrayList<>(normalAndCNFResults);
            for (String element : elements) {
                if (element.contains(doubleNegationOp)) {
                    element = element.replace(doubleNegationOp, "");
                }
                System.out.println(count.getAndIncrement() + ". " + element);
            }
            System.out.println("=============");
            elementLoopLabel:
            for (String element : elements) {
                normalAndCNFResults.set(initialCNFResults.size() - 1, element);
                for (int j = normalAndCNFResults.size(); j > 0; j--) {

                    int downTop = j - 1;
                    firstEl = normalAndCNFResults.get(downTop);

                    derivation = prepareSecondElementForDerivation(normalAndCNFResults, firstEl, derivation,
                            downTop, element, count.get());
                    count.getAndIncrement();

                    if (noDerivationPossible.get()) {
                        normalAndCNFResults = initialCNFResults;
                    }
                    if (derivation.equals(NIL)) {
                        System.out.println("=============");
                        System.out.println(initialGoal + " is true");
                        System.out.println("=============");
                        break elementLoopLabel;
                    }

                }
            }
        }
    }

    public static String prepareSecondElementForDerivation(ArrayList<String> normalAndCNFResults,
                                                           String firstEl, String derivation, int downTop, String lastElem, int count) {
        String secondEl = null;
        for (int j = normalAndCNFResults.size(); j >= 0; j--) {
            if (downTop > 0) {

                secondEl = normalAndCNFResults.get(downTop - 1);
            } else if (downTop == 0) {
                System.out.println("=============");
                System.out.println(lastElem + " is false. As '" + derivation + "' is the last value present\n");
                noDerivationPossible.getAndSet(true);
                break;
            }

            derivation = derivationOfElements(firstEl, secondEl);

            if (derivation != null && !derivation.equals(noMatchNextPairs) && !derivation.equals("")
                    && !derivation.equals(NIL) && !derivation.contains(hasMatchingLiteral_NextPairs)) {
                normalAndCNFResults.remove(firstEl);
                normalAndCNFResults.remove(secondEl);

                normalAndCNFResults.add(derivation);

                System.out.println(count + ". " + derivation + " "
                        + openParenthesis + downTop + "," + j + closeParenthesis);
                break;
            } else {
                assert derivation != null;
                if (derivation.contains(hasMatchingLiteral_NextPairs)) {
                    derivation = derivation.replace(hasMatchingLiteral_NextPairs, "");
                    normalAndCNFResults.remove(secondEl);

                    normalAndCNFResults.add(derivation);
                    break;
                } else {
                    if (derivation.equals(noMatchNextPairs)) {
                        normalAndCNFResults.remove(firstEl);

                        downTop--;
                    }

                }
            }
            if (derivation.equals(NIL)) {
                System.out.println(count + ". " + derivation + " " + openParenthesis + downTop + "," + j + closeParenthesis);
                noDerivationPossible.getAndSet(false);
                break;

            }
        }

        return derivation;
    }

    private static String derivationOfElements(String firstClause, String secondClause) {
        //In case that both Clause have at least an operator
        if ((firstClause.length() > 2) && (secondClause.length() > 2)) {
            if (firstClause.contains(orOperator) && secondClause.contains(orOperator)) {
                return commonOp(firstClause, secondClause, orOperator);

            } else if (firstClause.contains(andOperator) && secondClause.contains(orOperator)) {
                return differentOp(firstClause, secondClause, andOperator, orOperator);

            } else if (firstClause.contains(orOperator) && secondClause.contains(andOperator)) {
                return differentOp(firstClause, secondClause, orOperator, andOperator);

            } else if (firstClause.contains(andOperator) && secondClause.contains(andOperator)) {
                return commonOp(firstClause, secondClause, andOperator);
            } else if (!firstClause.contains(andOperator) && !firstClause.contains(orOperator)) {
                return retrieveElements(firstClause, secondClause,
                        firstClause.contains(negateTheValue), firstClause.replace(negateTheValue, ""));
            }
        }

        //In case that the first Clause is negative and 2nd one has single element
        else if (secondClause.length() == 1) {
            if (firstClause.length() == 2) {
                LogicalElement elem1 = new LogicalElement(firstClause);
                LogicalElement elem2 = new LogicalElement(secondClause);

                if (firstClause.contains(negateTheValue)) {
                    elem1.setHasNegation(true);
                    elem1.setElementName(firstClause.replace(negateTheValue, ""));
                }

                if (elem1.getElementName().equals(elem2.getElementName())) {
                    if (elem1.hasNegation() != elem2.hasNegation()) {
                        return NIL;
                    }
                } else if (!elem1.equals(elem2)) {
                    return noMatchNextPairs;
                }
            } else if (firstClause.length() > 2) {
                return retrieveElements(secondClause, firstClause,
                        secondClause.contains(negateTheValue), firstClause.replace(negateTheValue, ""));
            }
        }

        //In case that the second Clause is negative and 1st one has single element
        else if (firstClause.length() == 1) {
            if (secondClause.length() == 2) {
                LogicalElement elem1 = new LogicalElement(firstClause);
                LogicalElement elem2 = new LogicalElement(secondClause);

                if (secondClause.contains(negateTheValue)) {
                    elem2.setHasNegation(true);
                    elem2.setElementName(secondClause.replace(negateTheValue, ""));
                }
                if (elem1.getElementName().equals(elem2.getElementName())) {
                    if (elem1.hasNegation() != elem2.hasNegation()) {
                        return NIL;
                    }
                } else if (!elem1.equals(elem2)) {
                    return noMatchNextPairs;
                }

            } else if (secondClause.length() > 2) {
                return retrieveElements(firstClause, secondClause,
                        firstClause.contains(negateTheValue), firstClause.replace(negateTheValue, ""));
            }
        }

        //In case that the first Clause is negated but the 2nd clause is an expression
        else if (firstClause.length() == 2) {
            if (secondClause.length() > 2) {
                return retrieveElements(firstClause, secondClause,
                        firstClause.contains(negateTheValue), firstClause.replace(negateTheValue, ""));
            }
        }

        //In case that the first Clause is negated but the 2nd clause is an expression
        else if (secondClause.length() == 2) {
            if (firstClause.length() > 2) {
                return retrieveElements(secondClause, firstClause,
                        secondClause.contains(negateTheValue), secondClause.replace(negateTheValue, ""));
            }
        }

        return null;
    }

    private static String retrieveElements(String simpleClause, String expressionClause, boolean contains, String replace) {
        String operator = "";
        if (expressionClause.contains(orOperator) || expressionClause.contains(lowerCase(orOperator))) {
            operator = orOperator;
        } else if (expressionClause.contains(andOperator)) {
            operator = andOperator;
        }

        String[] expresionClauseElements = trimByOperator(expressionClause, operator);
        ArrayList<LogicalElement> elements = new ArrayList<>();
        LogicalElement firstElement = new LogicalElement(simpleClause);
        elements.add(firstElement);

        if (contains) {
            firstElement.setElementName(replace);
            firstElement.setHasNegation(true);
        }

        for (String expression : expresionClauseElements) {
            LogicalElement logicalEl = new LogicalElement(expression);

            if (logicalEl.getElementName().contains(negateTheValue)) {
                logicalEl.setElementName(expression.replace(negateTheValue, ""));
                logicalEl.setHasNegation(true);
            }
            elements.add(logicalEl);

        }

        return compareExpresionToSingleClause(elements, operator);
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

    private static String compareExpresionToSingleClause(ArrayList<LogicalElement> elements, String operator) {
        String singleWithExpression = null;
        String finalExpression = "";
        String firstNegOrNot;
//        LogicalElement firstClause = elements.get(0);
//        LogicalElement firstElementFromSecondClause = elements.get(1);
//        LogicalElement secondElementFromSecondClause = elements.get(2);
//
//        if (firstClause.hasNegation()) {
//            firstNegOrNot = negateTheValue;
//        } else firstNegOrNot = "";

        if (elements.size() > 2) {
            for (int i = 0; i < elements.size(); i++) {
                LogicalElement firstEl = elements.get(0);

                if (firstEl.hasNegation()) {
                    firstNegOrNot = negateTheValue;
                } else firstNegOrNot = "";

                if (i < elements.size() - 1) {
                    LogicalElement testingEl = elements.get(i + 1);
                    if (firstEl.getElementName().equals(testingEl.getElementName())) {
                        singleWithExpression = compareSingleClauseWithElementT(operator, firstNegOrNot, firstEl, testingEl);
                        finalExpression=        finalExpression.concat(singleWithExpression);
                    } else {
                        if (testingEl.hasNegation()) {
                            finalExpression = negateTheValue + testingEl.getElementName();
                        } else finalExpression = testingEl.getElementName();

                        singleWithExpression = noMatchNextPairs;
                    }
                }

            }
            singleWithExpression = finalExpression;
//            for (LogicalElement element : elements) {
            //first with first element of clause
//                if (firstClause != element) {

//                }


//                //first with second element of clause
//                else if (firstClause.getElementName().equals(secondElementFromSecondClause.getElementName())) {
//                    //a
//                    singleWithExpression = compareSingleClauseWithElement(operator, firstNegOrNot, firstClause, secondElementFromSecondClause, firstElementFromSecondClause, secondElementFromSecondClause.hasNegation());
//                }
        }

//        return singleWithExpression;
//        }

//        if (elements.size() == 3) {
//
//
//            //first with first element of clause
//            if (firstClause.getElementName().equals(firstElementFromSecondClause.getElementName())) {
//                singleWithExpression = compareSingleClauseWithElement(operator, firstNegOrNot, firstClause, firstElementFromSecondClause, secondElementFromSecondClause, secondElementFromSecondClause.hasNegation());
//            }
//            //first with second element of clause
//            else if (firstClause.getElementName().equals(secondElementFromSecondClause.getElementName())) {
//                //a
//                singleWithExpression = compareSingleClauseWithElement(operator, firstNegOrNot, firstClause, secondElementFromSecondClause, firstElementFromSecondClause, secondElementFromSecondClause.hasNegation());
//            } else {
//                singleWithExpression = noMatchNextPairs;
//            }
//        }
        //you
//TODO

//        else if (elements.size() == 4) {
//            LogicalElement thirdElementFromSecondClause = elements.get(3);
//
//            //first with first element of clause
//            if (firstClause.getElementName().equals(firstElementFromSecondClause.getElementName())) {
//                if (firstClause.hasNegation() != firstElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, secondElementFromSecondClause);
//                } else if (firstClause.hasNegation() == firstElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, secondElementFromSecondClause);
//                }
//
//            }
//
//            //first with 2nd element of clause
//            else if (firstClause.getElementName().equals(secondElementFromSecondClause.getElementName())) {
//                if (firstClause.hasNegation() != secondElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, firstElementFromSecondClause);
//                } else if (firstClause.hasNegation() == secondElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, firstElementFromSecondClause);
//                }
//
//            }
//
//            //first with last element
//            else if (firstClause.getElementName().equals(thirdElementFromSecondClause.getElementName())) {
//                if (firstClause.hasNegation() != thirdElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, firstElementFromSecondClause, secondElementFromSecondClause);
//                }
//                if (firstClause.hasNegation() == thirdElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, firstElementFromSecondClause, secondElementFromSecondClause);
//                }
//
//            }
//
//        }
//TODO
//       else if (elements.size() == 6) {
//            LogicalElement thirdElementFromSecondClause = elements.get(3);
//            LogicalElement forthElementFromSecondClause = elements.get(4);
//            LogicalElement fifthElementFromSecondClause = elements.get(5);
//
//            //first with first element of clause
//            if (firstClause.getElementName().equals(firstElementFromSecondClause.getElementName())) {
//                if (firstClause.hasNegation() != firstElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, secondElementFromSecondClause);
//                } else if (firstClause.hasNegation() == firstElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, secondElementFromSecondClause);
//                }
//
//            }
//
//            //first with 2nd element of clause
//            else if (firstClause.getElementName().equals(secondElementFromSecondClause.getElementName())) {
//                if (firstClause.hasNegation() != secondElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, firstElementFromSecondClause);
//                } else if (firstClause.hasNegation() == secondElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, thirdElementFromSecondClause, firstElementFromSecondClause);
//                }
//
//            }
//
//            //first with last element
//            else if (firstClause.getElementName().equals(thirdElementFromSecondClause.getElementName())) {
//                if (firstClause.hasNegation() != thirdElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, firstElementFromSecondClause, secondElementFromSecondClause);
//                }
//                if (firstClause.hasNegation() == thirdElementFromSecondClause.hasNegation()) {
//                    singleWithExpression = twoElementOperatorNegationComparison(operator, singleWithExpression, firstElementFromSecondClause, secondElementFromSecondClause);
//                }
//
//            }
//
//        }

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

    private static String compareSingleClauseWithElementT(String operator, String firstNegOrNot, LogicalElement firstClause,
                                                          LogicalElement element) {
        String singleWithExpression;
        if (firstClause.hasNegation() != element.hasNegation()) {
            singleWithExpression = "";
        } else {
            if (element.hasNegation()) {
                singleWithExpression = hasMatchingLiteral_NextPairs + firstNegOrNot + firstClause.getElementName() + operator
                        + negateTheValue + element.getElementName();
            } else {
                singleWithExpression = hasMatchingLiteral_NextPairs + firstNegOrNot + firstClause.getElementName() + operator
                        + element.getElementName();
            }
        }
        return singleWithExpression;
    }

    private static String twoElementOperatorNegationComparison(String operator, String singleWithExpression, LogicalElement firstElementFromSecondClause, LogicalElement secondElementFromSecondClause) {
        if (secondElementFromSecondClause.hasNegation()) {
            singleWithExpression = negateTheValue + secondElementFromSecondClause.getElementName();
        } else if (!secondElementFromSecondClause.hasNegation()) {
            singleWithExpression = secondElementFromSecondClause.getElementName();
        }
        if (firstElementFromSecondClause.hasNegation()) {
            singleWithExpression = singleWithExpression + operator + negateTheValue + firstElementFromSecondClause.getElementName();
        } else if (!firstElementFromSecondClause.hasNegation()) {
            singleWithExpression = singleWithExpression + operator + firstElementFromSecondClause.getElementName();
        }
        return singleWithExpression;
    }

    private static void extractComparableElements(ArrayList<LogicalElement> elemAndNegation, String[]
            comparableOperatorsFirstClause) {
        String firstClauseElem1 = comparableOperatorsFirstClause[0];
        String firstClauseElem2 = comparableOperatorsFirstClause[1];

        set_Element_Op(elemAndNegation, firstClauseElem1);
        set_Element_Op(elemAndNegation, firstClauseElem2);
    }

    private static void set_Element_Op(ArrayList<LogicalElement> elemAndNegation, String firstClauseElem2) {
        LogicalElement logicalElement = new LogicalElement(firstClauseElem2.replace(negateTheValue, ""));
        if (firstClauseElem2.contains(negateTheValue)) {
            logicalElement.setOperator(negateTheValue);
            logicalElement.setHasNegation(true);
        } else {
            logicalElement.setOperator("");
        }
        elemAndNegation.add(logicalElement);
    }

    public static String cnfConvert(String currentPremise, ArrayList<LogicalElement> premises, String operator) {
        LogicalElement firstElement = premises.get(0);
        LogicalElement secondElement = premises.get(1);

        if (operator.contains(equivalent)) {
            //~ = negation of first elem = implication elim.
            if (!firstElement.getElementName().contains(negateTheValue + openParenthesis)) {
                currentPremise = makeEquivalent(firstElement, secondElement);
                if (currentPremise.contains(doubleNegationOp)) {
                    currentPremise = removeDoubleNegation(currentPremise);
                }
                if (currentPremise.contains(negateTheValue + openParenthesis)) {
                    currentPremise = moveNegationOntoAtoms(currentPremise, operator);
                }
                int i = 0;
            } else {
                //~ = negation of first elem = implication elim.
                int indexOfP = indexOf(currentPremise, negateTheValue + openParenthesis);
                firstElement.setElementName(firstElement.getElementName().replace(negateTheValue + openParenthesis, ""));
                secondElement.setElementName(secondElement.getElementName().replace(closeParenthesis, ""));
                currentPremise = negateTheValue + openParenthesis + makeEquivalent(firstElement, secondElement) + closeParenthesis;
                if (currentPremise.contains(doubleNegationOp)) {
                    currentPremise = removeDoubleNegation(currentPremise);
                }
                if (currentPremise.contains(negateTheValue + openParenthesis)) {
                    if (currentPremise.contains(closeParenthesis + orOperator + openParenthesis)) {
                        currentPremise = moveNegationOntoAtoms(currentPremise, orOperator);

                    } else if (currentPremise.contains(closeParenthesis + andOperator + openParenthesis)) {
                        currentPremise = moveNegationOntoAtoms(currentPremise, andOperator);

                    }
                }
            }

        }
        if (operator.contains(implyOperator)) {
            //~ = negation of first elem = implication elim.
            currentPremise = negationElement(firstElement.getElementName())
                    + orOperator + secondElement.getElementName();
            if (currentPremise.contains(doubleNegationOp)) {
                currentPremise = removeDoubleNegation(currentPremise);
            }

            if (currentPremise.contains(negateTheValue + openParenthesis)) {
                currentPremise = moveNegationOntoAtoms(currentPremise, operator);
            }

        }
        if (currentPremise.contains(negateTheValue + openParenthesis)) {
            currentPremise = moveNegationOntoAtoms(currentPremise, operator);
        }

        return currentPremise;
    }

    private static String moveNegationOntoAtoms(String currentPremise, String operator) {
        int indexOfP = indexOf(currentPremise, operator);
        String firstSubstring = substring(currentPremise, 0, indexOfP);
        String secondSubstring = substring(currentPremise, indexOfP);
        String negatedOp = negateOperator(operator);
        String firstString;
        String secondString;
        if (currentPremise.contains(closeParenthesis + orOperator + openParenthesis)) {
            firstString = currentPremise.substring(currentPremise.indexOf(negateTheValue) + 2, currentPremise.indexOf(orOperator));
            secondString = currentPremise.substring(currentPremise.indexOf(orOperator))
                    .replace(orOperator, "").replace(closeParenthesis + closeParenthesis, closeParenthesis);

            String s = "s";
        } else if (currentPremise.contains(closeParenthesis + andOperator + openParenthesis)) {
            firstString = currentPremise.substring(currentPremise.indexOf(negateTheValue) + 2, currentPremise.indexOf(andOperator));
            secondString = currentPremise.substring(currentPremise.indexOf(andOperator))
                    .replace(andOperator, "").replace(closeParenthesis + closeParenthesis, closeParenthesis);
            String[] firstStringElements = firstString.split(orOperator);
            String firstStringElement1 = negateTheValue + firstStringElements[0].replace(openParenthesis, "");
            String firstStringElement2 = negateTheValue + firstStringElements[1].replace(closeParenthesis, "");

            String[] secondStringElements = secondString.split(orOperator);
            String secondStringElement1 = negateTheValue + secondStringElements[0].replace(openParenthesis, "");
            String secondStringElement2 = negateTheValue + secondStringElements[1].replace(closeParenthesis, "");

            String firstNegatedAtom = firstStringElement1 + andOperator + firstStringElement2;
            String secondNegatedAtom = secondStringElement1 + andOperator + secondStringElement2;

            currentPremise = openParenthesis + firstNegatedAtom + closeParenthesis + negatedOp + openParenthesis + secondNegatedAtom + closeParenthesis;

            if (currentPremise.contains(doubleNegationOp)) {
                currentPremise = removeDoubleNegation(currentPremise);
            }

            String s = "s";
        } else {
            String firstNegatedAtom = firstSubstring.replace(openParenthesis, "");
            String secondNegatedAtom = negateTheValue + secondSubstring.replace(closeParenthesis, "").replace(operator, "");

            currentPremise = firstNegatedAtom + negatedOp + secondNegatedAtom;
            int i = 9;
        }
        return currentPremise;
    }
}
