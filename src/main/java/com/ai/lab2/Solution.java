package com.ai.lab2;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.ai.utils.Lab_Utils.*;
import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class Solution {

    static AtomicBoolean noDerivationPossible = new AtomicBoolean();

    static AtomicReference<String> atomicStringReference = new AtomicReference<>();
    static AtomicInteger count = new AtomicInteger(1);

    public static void main(String[] args) throws FileNotFoundException {
//        Scanner sc = new Scanner(new File(Constant.small_ex_1));
//        Scanner sc = new Scanner(new File(Constant.small_ex_2));
//        Scanner sc = new Scanner(new File(Constant.small_ex_3));
        Scanner sc = new Scanner(new File(Constant.small_ex_4));

        ArrayList<String> listOfPremises = new ArrayList<>();
        LinkedList<String> normalAndCNFResults = new LinkedList<>();

        while (sc.hasNext()) {
            String line = lowerCase(sc.nextLine());
            if (line.startsWith("#")) continue;
            if (line.trim().isEmpty()) continue;
            listOfPremises.add(line);
        }

        System.out.println("=============");
        for (int i = 0; i < listOfPremises.size() - 1; i++) {
            String element = listOfPremises.get(i);
            System.out.println(count + ". " + element);
            count.getAndIncrement();
        }
        
        String lastElem = listOfPremises.get(listOfPremises.size() - 1);
        String initialGoal = lastElem;
        for (String currentPremise : listOfPremises) {
            LinkedList<LogicalElement> elementsToCompare = new LinkedList<>();

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
                        currentPremise = negateTheValue + openParenthesis + getElementPremise(elementsToCompare, element.getOperator()) + closeParenthesis;
                    }
                    currentPremise = cnfConvert(currentPremise, elementsToCompare, element.getOperator());
                }
                if (!normalAndCNFResults.contains(currentPremise)) {
                    normalAndCNFResults.add(currentPremise);
                }
            }

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
            LinkedList<String> initialCNFResults = new LinkedList<>(normalAndCNFResults);
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

    public static String prepareSecondElementForDerivation(LinkedList<String> normalAndCNFResults,
                                                           String firstEl, String derivation, int downTop, String lastElem, int count) {
        String secondEl = null;

        for (int j = normalAndCNFResults.size(); j >= 0; j--) {
            if (downTop > 0) {

                secondEl = normalAndCNFResults.get(downTop - 1);
            } else if (downTop == 0) {
                if (normalAndCNFResults.size() > 1) {
                    if (j != 0) {
                        secondEl = normalAndCNFResults.getLast();
                    } else {
                        System.out.println(lastElem + " is false. As '" + derivation + "' is the last value present\n");
                        noDerivationPossible.getAndSet(true);
                        break;
                    }
                } else {
                    System.out.println("=============");
                    System.out.println(lastElem + " is false. As '" + derivation + "' is the last value present\n");
                    noDerivationPossible.getAndSet(true);
                    break;
                }
            }

            derivation = derivationOfElements(firstEl, secondEl);

            if (derivation != null && !derivation.equals(noMatchNextPairs) && !derivation.equals("")
                    && !derivation.equals(NIL) && !derivation.contains(hasMatchingLiteral_NextPairs)) {

                if (firstEl.contains(orOperator)) {
                    findMatchesForThe2Elements(normalAndCNFResults, firstEl, derivation, downTop, count, secondEl, orOperator, lastElem);
                } else if (firstEl.contains(andOperator)) {
                    findMatchesForThe2Elements(normalAndCNFResults, firstEl, derivation, downTop, count, secondEl, andOperator, lastElem);
                } else {
                    normalAndCNFResults.remove(firstEl);
                    normalAndCNFResults.remove(secondEl);
                    normalAndCNFResults.add(derivation);

                    System.out.println(count + 1 + ". " + derivation + " "
                            + openParenthesis + downTop + "," + (count - 1) + closeParenthesis);
                }

                break;
            } else {
                assert derivation != null;
                if (derivation.contains(hasMatchingLiteral_NextPairs)) {
                    derivation = derivation.replace(hasMatchingLiteral_NextPairs, "");
                    normalAndCNFResults.remove(secondEl);

                    normalAndCNFResults.add(derivation);
                    break;
                } else {
                    if (derivation.equals("")) {
                        normalAndCNFResults.remove(firstEl);
                        normalAndCNFResults.remove(secondEl);
                        if (normalAndCNFResults.size() == 0) {
                            return NIL;
                        }
                    }
                    if (derivation.equals(noMatchNextPairs)) {
                        if (!firstEl.contains(orOperator) && !firstEl.contains(andOperator)) {
                            normalAndCNFResults.remove(firstEl);

                        }

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

    private static void findMatchesForThe2Elements(LinkedList<String> normalAndCNFResults, String firstEl, String derivation,
                                                   int downTop, int count, String secondEl, String operator, String lastElem) {
        String elementToClear = atomicStringReference.get();
        String[] firstElComponents = trimByOperator(firstEl, operator);
        String[] secondElComponents = trimByOperator(secondEl, operator);

        String firstElementAdjust = "";
        String initialFirstEl = firstEl;
        boolean hasMatchOrNot = findIfAnyMatches(firstElComponents, secondElComponents);
        if (firstElComponents.length > 1 && secondElComponents.length > 1) {

            if (hasMatchOrNot) {
                firstElementAdjust = elementToClear + "end";
                firstEl = firstEl + "end";
                if (firstEl.contains(negateTheValue + elementToClear)) {
                    firstEl = firstEl.replace(operator + negateTheValue + firstElementAdjust, "");
                    normalAndCNFResults.set(normalAndCNFResults.indexOf(initialFirstEl), firstEl);
                }

                normalAndCNFResults.set(normalAndCNFResults.indexOf(secondEl), derivation);
                normalAndCNFResults.remove(secondEl);

                System.out.println(count + 1 + ". " + derivation + " "
                        + openParenthesis + downTop + "," + (count - 1) + closeParenthesis);
            }
        } else if (firstElComponents.length == 1 && secondElComponents.length > 1) {

            if (hasMatchOrNot) {
                normalAndCNFResults.set(normalAndCNFResults.indexOf(secondEl), derivation);
                normalAndCNFResults.remove(firstEl);

                if (normalAndCNFResults.size() > 1) {
                    System.out.println(count + 1 + ". " + derivation + " "
                            + openParenthesis + downTop + "," + (count - 1) + closeParenthesis);
                } else {
                    System.out.println("=============");
                    System.out.println(lastElem + " is false. As '" + derivation + "' is the last value present\n");
                    noDerivationPossible.getAndSet(true);
                }
            }
        } else if (firstElComponents.length > 1 && secondElComponents.length == 1) {
            if (hasMatchOrNot) {
                normalAndCNFResults.set(normalAndCNFResults.indexOf(firstEl), derivation);
                normalAndCNFResults.remove(secondEl);

                if (normalAndCNFResults.size() > 1) {
                    System.out.println(count + 1 + ". " + derivation + " "
                            + openParenthesis + downTop + "," + (count - 1) + closeParenthesis);
                } else {
                    System.out.println("=============");
                    System.out.println(lastElem + " is false. As '" + derivation + "' is the last value present\n");
                    noDerivationPossible.getAndSet(true);
                }
            }

        }
    }

    private static boolean findIfAnyMatches(String[] firstElComponents, String[] secondElComponents) {
        boolean hasMatchOrNot = false;
        foundMatch:
        for (String firstElComponent : firstElComponents) {
            if (firstElComponent.contains(negateTheValue))
                firstElComponent = firstElComponent.replace(negateTheValue, "");
            for (String secondElComponent : secondElComponents) {
                if (secondElComponent.contains(negateTheValue))
                    secondElComponent = secondElComponent.replace(negateTheValue, "");
                hasMatchOrNot = firstElComponent.equals(secondElComponent);
                if (hasMatchOrNot) {
                    break foundMatch;
                }
            }
        }
        return hasMatchOrNot;
    }

    private static String derivationOfElements(String firstClause, String secondClause) {
        //In case that both Clause have at least an operator
        if ((firstClause.length() > 2) && (secondClause.length() > 2)) {
            if (firstClause.contains(orOperator)) {
                String result = "";
                String[] elements = trimByOperator(firstClause, orOperator);
                StringBuilder firstClauseBuilder = new StringBuilder(firstClause);
                for (String element : elements) {
                    result = retrieveElements(element, secondClause,
                            firstClauseBuilder.toString().contains(negateTheValue),
                            element.replace(negateTheValue, ""));
                    if (result.equals("")) {
                        firstClauseBuilder.append("end");
                        result = firstClauseBuilder.toString().replace(element, "");
                        elements = trimByOperator(result.replace("end", ""), orOperator);
                        if (elements.length == 1) {
                            String remainingEl = elements[0];
                            if (remainingEl.startsWith("v ")) {
                                result = remainingEl.replace("v ", "");
                            } else if (remainingEl.endsWith(" v")) {
                                result = remainingEl.replace(" v", "");
                            }
                            return result;
                        }
                        result = result.replace(orOperator + "end", "");
                    }
                }
                return result;
            } else if (firstClause.contains(andOperator)) {
                String result = "";
                String[] elements = trimByOperator(firstClause, andOperator);
                StringBuilder firstClauseBuilder = new StringBuilder(firstClause);
                for (String element : elements) {
                    result = retrieveElements(element, secondClause,
                            firstClauseBuilder.toString().contains(negateTheValue),
                            element.replace(negateTheValue, ""));
                    if (result.equals("")) {
                        firstClauseBuilder.append("end");
                        result = firstClauseBuilder.toString().replace(element, "");
                        elements = trimByOperator(result.replace("end", ""), andOperator);
                        if (elements.length == 1) {
                            String remainingEl = elements[0];
                            if (remainingEl.startsWith("v ")) {
                                result = remainingEl.replace("v ", "");
                            } else if (remainingEl.endsWith(" v")) {
                                result = remainingEl.replace(" v", "");
                            }
                            return result;
                        }
                        result = result.replace(andOperator + "end", "");
                    }
                }
                return result;
            } else if (!firstClause.contains(orOperator) && !firstClause.contains(andOperator)) {
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

        ArrayList<LogicalElement> elements = new ArrayList<>();

        LogicalElement firstElement = new LogicalElement(simpleClause);
        elements.add(firstElement);

        if (contains) {
            firstElement.setElementName(replace);
            firstElement.setHasNegation(true);
        }
        if (!firstElement.getElementName().equals(expressionClause)) {
            String[] expresionClauseElements = trimByOperator(expressionClause, operator);
            for (String expression : expresionClauseElements) {
                LogicalElement logicalEl = new LogicalElement(expression);

                if (logicalEl.getElementName().contains(negateTheValue)) {
                    logicalEl.setElementName(expression.replace(negateTheValue, ""));
                    logicalEl.setHasNegation(true);
                }
                elements.add(logicalEl);
                if (expressionClause.contains(simpleClause)) {
                    if ((logicalEl.hasNegation() == firstElement.hasNegation())
                            && (logicalEl.getElementName().equals(firstElement.getElementName()))) {

                        return hasMatchingLiteral_NextPairs + expressionClause;
                    }
                }
//                else if (!expressionClause.contains(simpleClause)) {
//                    if ((logicalEl.hasNegation() == firstElement.hasNegation())
//                            && (logicalEl.getElementName().equals(firstElement.getElementName()))) {
//
//                        return hasMatchingLiteral_NextPairs + expressionClause;
//                    }
//                }
            }
        } else if (firstElement.getElementName().equals(expressionClause)) {
            LogicalElement logicalEl = new LogicalElement(expressionClause);

            if (logicalEl.getElementName().contains(negateTheValue)) {
                logicalEl.setElementName(expressionClause.replace(negateTheValue, ""));
                logicalEl.setHasNegation(true);
            }
            elements.add(logicalEl);
        }

        return compareExpresionToSingleClause(elements, operator, expressionClause);

    }

    private static String compareExpresionToSingleClause(ArrayList<LogicalElement> elements, String operator, String expressionToDerive) {
        String singleWithExpression = null;
        String finalExpression = "";
        String firstNegOrNot;

        if (elements.size() >= 2) {
            for (int i = 0; i < elements.size(); i++) {
                LogicalElement firstEl = elements.get(0);

                if (firstEl.hasNegation()) {
                    firstNegOrNot = negateTheValue;
                } else firstNegOrNot = "";

                if (i < elements.size() - 1) {
                    LogicalElement testingEl = elements.get(i + 1);
                    if (firstEl.getElementName().equals(testingEl.getElementName())) {
                        atomicStringReference = new AtomicReference<>(testingEl.getElementName());
                        singleWithExpression = compareSingleClauseWithElementT(operator, firstNegOrNot, firstEl, testingEl);
                        finalExpression = finalExpression.concat(singleWithExpression);
                        if (singleWithExpression.contains(hasMatchingLiteral_NextPairs)) {
                            return finalExpression;
                        }
                    } else {
                        if (testingEl.hasNegation()) {
                            finalExpression = finalExpression.concat(negateTheValue + testingEl.getElementName() + operator);
                        } else finalExpression = finalExpression.concat(testingEl.getElementName() + operator);

                    }
                }

            }
            if (finalExpression.endsWith(operator)) {
                finalExpression = finalExpression + "end";
                singleWithExpression = finalExpression.replace(operator + "end", "");
            }
            if (Objects.equals(singleWithExpression, expressionToDerive)) {
                return noMatchNextPairs;
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

    public static String cnfConvert(String currentPremise, LinkedList<LogicalElement> premises, String operator) {
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
                    currentPremise = moveNegationOntoAtoms(currentPremise, premises, operator);
                }
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
                        currentPremise = moveNegationOntoAtoms(currentPremise, premises, orOperator);

                    } else if (currentPremise.contains(closeParenthesis + andOperator + openParenthesis)) {
                        currentPremise = moveNegationOntoAtoms(currentPremise, premises, andOperator);

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
                currentPremise = moveNegationOntoAtoms(currentPremise, premises, operator);
            }

        }

        if (currentPremise.contains(negateTheValue + openParenthesis)) {
            currentPremise = moveNegationOntoAtoms(currentPremise, premises, operator);
        }

        return currentPremise;
    }

    private static String moveNegationOntoAtoms(String currentPremise, LinkedList<LogicalElement> premises, String operator) {
        String negatedOp = negateOperator(operator);

        LinkedList<String> listOfElements = new LinkedList<>();
        StringBuilder finalPremise = new StringBuilder();

        if (currentPremise.contains(closeParenthesis)
                && currentPremise.contains(operator)
                && currentPremise.contains(openParenthesis)) {

            for (int i = 0; i < premises.size() - 1; i++) {
                LogicalElement element = premises.get(i);
                LogicalElement followingEl = premises.get(i + 1);

                String elementName = element.getElementName();
                String follElementName = followingEl.getElementName();

                if (!listOfElements.contains(elementName)) {
                    if (!listOfElements.contains(elementName + operator)) {
                        listOfElements.add(elementName + operator);
                        int currentPos = listOfElements.indexOf(elementName + operator);
                        finalPremise.append(negateTheValue).append(listOfElements.get(currentPos));
                    }
                }
                if (!listOfElements.contains(follElementName)) {
                    if (!listOfElements.contains(follElementName + operator)) {
                        listOfElements.add(follElementName + operator);
                        int currentPos = listOfElements.indexOf(follElementName + operator);
                        finalPremise.append(negateTheValue).append(listOfElements.get(currentPos));
                    }
                }
            }

            replaceString(finalPremise, operator, negatedOp);

            if (finalPremise.toString().endsWith(negatedOp)) {
                finalPremise.append("end");
                finalPremise = new StringBuilder(finalPremise.toString().replace(negatedOp + "end", ""));
            }

            if (finalPremise.toString().contains(doubleNegationOp)) {
                replaceString(finalPremise, doubleNegationOp, "");
            }
            currentPremise = String.valueOf(finalPremise);
        }
        return currentPremise;
    }

}
