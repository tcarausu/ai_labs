package com.ai.utils;

import com.ai.lab2.LogicalElement;

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


}
