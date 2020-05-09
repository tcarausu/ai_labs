package com.ai.lab2;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.ai.lab2.Solution.*;
import static com.ai.utils.Lab_Utils.*;
import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class CookingAssistant {

    static AtomicReference<String> valueToTest = new AtomicReference<>("coffee");
    static AtomicInteger count = new AtomicInteger(1);

    public static AtomicInteger getCount() {
        return count;
    }

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Please select the working mode: ");

        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();

        if (line.equals("int")) {//interactive
            Scanner interactive = new Scanner(new File(Constant.coffee));
            Scanner inputs = new Scanner(new File(Constant.coffee_input));
            LinkedList<String> listOfPremises = retrieveKnowledge(interactive);
            LinkedList<String> inputsToTest = new LinkedList<>();

            while (inputs.hasNext()) {
                String knowledge = lowerCase(inputs.nextLine());
                if (knowledge.startsWith("#")) continue;
                if (knowledge.trim().isEmpty()) continue;
                inputsToTest.add(knowledge);
            }

            for (String input : inputsToTest) {
                count.set(1);
                System.out.println("\nPlease enter your query: ");
                System.out.println("\n" + startClauseTest + input);

                if (input.contains(clauseValidity)) {
                    valueToTest = new AtomicReference<>(lowerCase(input.replace(clauseValidity, "")));
                    int index = inputsToTest.indexOf(input);
                    if (index != 0) {
                        listOfPremises.removeLast();
                    }
                    refutationResolution(listOfPremises);
                } else if (input.contains(clauseRemoval)) {
                    valueToTest = new AtomicReference<>(lowerCase(input.replace(clauseRemoval, "")));
                    listOfPremises.remove(valueToTest.toString());
                    System.out.println("removed " + valueToTest);
                } else if (input.contains(clauseAddition)) {
                    valueToTest = new AtomicReference<>(lowerCase(input.replace(clauseAddition, "")));
                    listOfPremises.add(valueToTest.toString());
                    System.out.println("added " + valueToTest);
                }

            }

            interactive.close();
        } else if (line.equals("test")) {
//            Scanner interactive = new Scanner(new File(Constant.chicken_alfredo));
            Scanner interactive = new Scanner(new File(Constant.coffee));

            LinkedList<String> listOfPremises = retrieveKnowledge(interactive);

            System.out.println("Please enter your query: ");
            String testCommandQuery = sc.nextLine();

            if (testCommandQuery.endsWith(clauseValidity)) {
                valueToTest = new AtomicReference<>(lowerCase(testCommandQuery.replace(clauseValidity, "")));
                System.out.println("\n" + startClauseTest + testCommandQuery);

                refutationResolution(listOfPremises);
                interactive.close();

            }
            if (testCommandQuery.endsWith(clauseAddition)) {
                valueToTest = new AtomicReference<>(lowerCase(testCommandQuery.replace(clauseAddition, "")));

                System.out.println("\n" + startClauseTest + testCommandQuery);
                listOfPremises.add(testCommandQuery);
                System.out.println("added " + testCommandQuery);
            }
            if (testCommandQuery.endsWith(clauseRemoval)) {
                valueToTest = new AtomicReference<>(lowerCase(testCommandQuery.replace(clauseRemoval, "")));

                System.out.println("\n" + startClauseTest + testCommandQuery);
                listOfPremises.remove(testCommandQuery);
                System.out.println("removed " + testCommandQuery);

            }
            if (testCommandQuery.endsWith(clauseExit) || testCommandQuery.contains(clauseExit)) {
                sc.close();
            }

        }
    }


    private static void refutationResolution(LinkedList<String> listOfPremises) {
        LinkedList<String> normalAndCNFResults = new LinkedList<>();
        if (valueToTest.get() != null && !Objects.requireNonNull(valueToTest).get().equals("")) {
            listOfPremises.add(String.valueOf(valueToTest));
            System.out.println("=============");
        } else {
            System.out.println("No input");

            System.exit(0);
        }

        for (int i = 0; i < listOfPremises.size() - 1; i++) {
            String element = listOfPremises.get(i);
            System.out.println(count.getAndIncrement() + ". " + element);
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

        String currentLast = normalAndCNFResults.get(normalAndCNFResults.size() - 1);
        if (currentLast.length() == 2 || currentLast.length() == 1) {
            System.out.println(count.getAndIncrement() + ". " + negationElement(lastElem));
            System.out.println("=============");
        }

        System.out.println("=============");
        String[] lastElements = new String[0];

        LinkedList<String> lastElementsList;
        String negatedElem = "";
        if (lastElem.contains(orOperator)) {
            lastElements = trimByOperator(lastElem, orOperator);
            lastElementsList = new LinkedList<>(Arrays.asList(lastElements));
            lastElementsList.add(orOperator);
        } else if (lastElem.contains(andOperator)) {
            lastElements = trimByOperator(lastElem, andOperator);
            lastElementsList = new LinkedList<>(Arrays.asList(lastElements));
            lastElementsList.add(andOperator);
        }
        String[] elements = new String[0];
        if (lastElements.length == 0) {
            negatedElem = negationElement(lastElem);
            if (negatedElem.contains(doubleNegationOp)) negatedElem = lastElem.replace(doubleNegationOp, "");
            normalAndCNFResults.set(normalAndCNFResults.size() - 1, negatedElem);

            System.out.println(count + ". " + negationElement(lastElem));
            System.out.println("=============");
        } else if (lastElements.length > 1) {
            if (lastElem.contains(orOperator)) {
                elements = trimByOperator(lastElem, orOperator);
            } else if (lastElem.contains(andOperator)) {
                elements = trimByOperator(lastElem, andOperator);
            }
            for (String element : elements) {
                System.out.println(count.getAndIncrement() + ". " + element);
            }
            System.out.println("=============");
        }


        String firstEl;
        String derivation = null;
        LinkedList<String> initialCNFResults = new LinkedList<>(normalAndCNFResults);

        if (lastElem.length() > 2) {
            if (elements.length == 0) {
                for (int j = normalAndCNFResults.size(); j > 0; j--) {
                    int downTop = j - 1;
                    firstEl = normalAndCNFResults.get(downTop);

                    derivation = prepareSecondElementForDerivation(normalAndCNFResults, firstEl, derivation,
                            downTop, lastElem, count.get());
                    count.getAndIncrement();

                    if (derivation.equals(NIL)) {
                        System.out.println("=============");
                        System.out.println(initialGoal + " is true");
                        System.out.println("=============");
                        break;
                    }
                }
                if (noDerivationPossible.get()) {
                    normalAndCNFResults = initialCNFResults;

                }

            }

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


    public static LinkedList<String> retrieveKnowledge(Scanner interactive) {
        System.out.println("\nResolution system constructed with knowledge:\n");

        LinkedList<String> listOfPremises = new LinkedList<>();

        while (interactive.hasNext()) {
            String knowledge = lowerCase(interactive.nextLine());
            if (knowledge.startsWith("#")) continue;
            if (knowledge.contains("#")) continue;
            if (knowledge.trim().isEmpty()) continue;
            listOfPremises.add(knowledge);
        }

        System.out.println("=============");
        for (int i = 0; i < listOfPremises.size() - 1; i++) {
            String element = listOfPremises.get(i);
            System.out.println(currentKnowledge + element);
        }
        System.out.println("=============\n");
        return listOfPremises;
    }

}
