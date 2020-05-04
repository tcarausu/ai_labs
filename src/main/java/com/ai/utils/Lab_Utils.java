package com.ai.utils;

import com.ai.lab2.LogicalElement;

import java.util.LinkedList;

import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.substring;

public class Lab_Utils {

    public static String[] trimByOperator(String currentPremise, String s) {
        return currentPremise.trim().split(s);
    }

    public static String negateOperator(String operator) {
        if (operator.equals(orOperator)) {
            operator = andOperator;
        } else if (operator.equals(andOperator)) operator = orOperator;
        return operator;
    }

    public static String negationElement(String elem) {
        return negateTheValue + elem;
    }

    public static String makeEquivalent(LogicalElement firstElement, LogicalElement secondElement) {
        return openParenthesis + negationElement(firstElement.getElementName())
                + orOperator + secondElement.getElementName() + closeParenthesis
                //conjuncts the 2 parenthesis
                + andOperator +
                //negates the second element has and or but also keeps the first
                openParenthesis + negationElement(secondElement.getElementName())
                + orOperator + firstElement.getElementName() + closeParenthesis;
    }

    public static String removeDoubleNegation(String premise) {
        if (premise.contains(openParenthesis) && premise.contains(closeParenthesis)) {
            int indexOfP = indexOf(premise, closeParenthesis);
            String firstSubstring = substring(premise, 0, indexOfP + 1).replace(doubleNegationOp, "");
            String secondSubstring = substring(premise, indexOfP + 1).replace(doubleNegationOp, "");

            premise = firstSubstring + secondSubstring;
        } else if (!premise.contains(openParenthesis)) {
            premise = checkOperator(premise, orOperator);
            premise = checkOperator(premise, andOperator);
        }
        return premise;
    }


    private static String checkOperator(String premise, String andOperator) {
        if (premise.contains(andOperator)) {
            String[] comparableOperators = trimByOperator(premise, andOperator);
            String firstSubstring = comparableOperators[0].replace(doubleNegationOp, "");
            String secondSubstring = comparableOperators[1].replace(doubleNegationOp, "");
            premise = firstSubstring + andOperator + secondSubstring;
            String s = "s";
        }
        return premise;
    }


    public static void replaceString(StringBuilder sb, String toReplace, String replacement) {
        int index;
        while ((index = sb.lastIndexOf(toReplace)) != -1) {
            sb.replace(index, index + toReplace.length(), replacement);
        }
    }

    public static String getElementPremise(LinkedList<LogicalElement> elementsToCompare, String operator) {
        LinkedList<String> listOfElements = new LinkedList<>();

        StringBuilder currentPremise = new StringBuilder();

        for (int i = 0; i < elementsToCompare.size() - 1; i++) {
            LogicalElement element = elementsToCompare.get(i);
            LogicalElement followingEl = elementsToCompare.get(i + 1);

            String elementName = element.getElementName();
            String follElementName = followingEl.getElementName();

            if (!listOfElements.contains(elementName)) {
                if (!listOfElements.contains(elementName + operator)) {
                    listOfElements.add(elementName + operator);
                    int currentPos = listOfElements.indexOf(elementName + operator);
                    currentPremise.append(listOfElements.get(currentPos));
                }
            }
            if (!listOfElements.contains(follElementName)) {
                if (!listOfElements.contains(follElementName + operator)) {
                    listOfElements.add(follElementName + operator);
                    int currentPos = listOfElements.indexOf(follElementName + operator);
                    currentPremise.append(listOfElements.get(currentPos));
                }
            }
        }

        if (currentPremise.toString().endsWith(orOperator)) {
            currentPremise.append("end");
            currentPremise = new StringBuilder(currentPremise.toString().replace(orOperator + "end", ""));
        } else if (currentPremise.toString().endsWith(andOperator)) {
            currentPremise.append("end");
            currentPremise = new StringBuilder(currentPremise.toString().replace(andOperator + "end", ""));
        }
        return currentPremise.toString();

    }

}
