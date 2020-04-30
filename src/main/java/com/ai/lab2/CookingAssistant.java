package com.ai.lab2;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ai.lab2.Solution.*;
import static com.ai.utils.Lab_Utils.negationElement;
import static com.ai.utils.Lab_Utils.trimByOperator;
import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class CookingAssistant {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Please select the working mode: ");

        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();

        if (line.equals("interactive")) {
            Scanner interactive = new Scanner(new File(Constant.chicken_alfredo));

            refutationResolution(interactive);
            interactive.close();
        } else if (line.equals("test")) {
            Scanner interactive = new Scanner(new File(Constant.chicken_alfredo));

            refutationResolution(interactive);
            interactive.close();

            sc.close();
        }
    }

    private static void refutationResolution(Scanner sc) {

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

        if (negatedElem.contains(doubleNegationOp)) negatedElem = lastElem.replace(doubleNegationOp, "");
        normalAndCNFResults.set(normalAndCNFResults.size() - 1, negatedElem);

        String firstEl;
        String derivation = null;

        if (lastElem.length() > 2) {
            String[] elements = new String[0];
            if (!lastElem.contains(orOperator) && !lastElem.contains(andOperator)) {
                System.out.println("=============");
                ArrayList<String> initialCNFResults = new ArrayList<>(normalAndCNFResults);

                elementLoopLabel:
                for (String element : normalAndCNFResults) {
//                    normalAndCNFResults.set(initialCNFResults.size() - 1, element);
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
            else {
                if (lastElem.contains(orOperator)) {
                    elements = trimByOperator(lastElem, orOperator);
                }
                else if (lastElem.contains(andOperator)) {
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
    }
}
