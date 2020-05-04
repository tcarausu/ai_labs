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

    public static void main(String[] args) throws FileNotFoundException {
//        System.out.println("Please select the working mode: ");
//
//        Scanner sc = new Scanner(System.in);
//        String line = sc.nextLine();
//
//        if (line.equals("interactive")) {
//            Scanner interactive = new Scanner(new File(Constant.chicken_alfredo));
//
//            refutationResolution(interactive);
//            interactive.close();
//        } else if (line.equals("test")) {

//            Scanner interactive = new Scanner(new File(Constant.chicken_alfredo));
        Scanner interactive = new Scanner(new File(Constant.coffee));

//            System.out.println("Please write down the command: ");
//            String testCommand = sc.nextLine();
//            if (testCommand.endsWith(clauseValidity)) {
//                valueToTest = new AtomicReference<>(lowerCase(testCommand.replace(clauseValidity, "")));
//
//                refutationResolution(interactive);
//                interactive.close();
//            } else if (testCommand.endsWith(clauseAddition)) {
//                valueToTest = new AtomicReference<>(lowerCase(testCommand.replace(clauseAddition, "")));
//
//                refutationResolution(interactive);
//                interactive.close();
//            } else if (testCommand.endsWith(clauseRemoval)) {
//                valueToTest = new AtomicReference<>(lowerCase(testCommand.replace(clauseRemoval, "")));
//
        refutationResolution(interactive);
        interactive.close();
//            }
//            sc.close();
//
//        }
    }

    private static void refutationResolution(Scanner sc) {
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
        if (valueToTest != null) {
            listOfPremises.add(String.valueOf(valueToTest));
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
        for (int i = 0; i < listOfPremises.size() - 1; i++) {
            String element = listOfPremises.get(i);
            System.out.println(count + ". " + element);
            count.getAndIncrement();
        }

        String currentLast = normalAndCNFResults.get(normalAndCNFResults.size() - 1);
        if (currentLast.length() == 2 || currentLast.length() == 1) {
            System.out.println(count + ". " + negationElement(lastElem));
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

                    if (noDerivationPossible.get()) {
                        normalAndCNFResults = initialCNFResults;

                    }
                    if (derivation.equals(NIL)) {
                        System.out.println("=============");
                        System.out.println(initialGoal + " is true");
                        System.out.println("=============");
                        break;
                    }
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
//    private static boolean deriveIfPossible(AtomicInteger count, LinkedList<String> normalAndCNFResults,
//                                            String lastElem, String initialGoal,
//                                            String derivation, String valueToTest, LinkedList<String> initialCNFResults) {
//        if (valueToTest != null) {
//            normalAndCNFResults.add(negateTheValue + valueToTest);
//        }
//        String firstEl;
//        for (int j = normalAndCNFResults.size(); j > 0; j--) {
//            int downTop = j - 1;
//            firstEl = normalAndCNFResults.get(downTop);
//
//            derivation = prepareSecondElementForDerivation(normalAndCNFResults, firstEl, derivation,
//                    downTop, lastElem, count.get());
//            count.getAndIncrement();
//
//            if (noDerivationPossible.get()) {
//                return false;
//            }
//            if (derivation.equals(NIL)) {
//                System.out.println("=============");
//                System.out.println(initialGoal + " is true");
//                System.out.println("=============");
//                return true;
//            }
//        }
//        return false;
//    }
}
