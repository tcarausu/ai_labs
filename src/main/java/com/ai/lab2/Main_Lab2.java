package com.ai.lab2;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.*;

public class Main_Lab2 {

    static LinkedHashMap<String, HashMap<String, ArrayList<LogicalElement>>> mapForCNF = new LinkedHashMap<>();
    static LinkedHashMap<String, ArrayList<LogicalElement>> promiseWithElements = new LinkedHashMap<>();

    private static HashMap<String, HashMap<ArrayList<LogicalElement>, Boolean>> logicalElAndOperator = new HashMap<>();
    private static ArrayList<Boolean> operators = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
//        Scanner sc = new Scanner(new File(Constant.sample_output_min_par));
        Scanner sc = new Scanner(new File(Constant.small_example_for_operators));

        ArrayList<String> listOfPremises = new ArrayList<>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.startsWith("#")) continue;
            listOfPremises.add(line);
//            splitLine = line.split(" {2}");

//            if (line.contains("(") && line.contains(")")) {
//                String[] splitMe = line.split("[\\\\(|)]");
//
//                String[] textInsideParenthesisT = substringsBetween(line, "(", ")");
//                if (textInsideParenthesisT.length > 1) {
//                    String textInsideParenthesis = substringBetween(line, ")", "(");
//                    for (String elementOrExpression : textInsideParenthesisT) {
//                        checkForOperators(line, elementOrExpression);
//                    }
//
////                    checkForOperators(line, textInsideParenthesis);
//                    logicalElAndOperator.entrySet().size();
//                    operators.clear();
//                    logicalElAndOperator.clear();
//                }
//
//            } else {
//                String element = splitLine[0];
//
//                checkForOperators(line, element);
//                operators.clear();
//                logicalElAndOperator.clear();
//            }
        }

        String lastElem = listOfPremises.get(listOfPremises.size() - 1);


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
                if (elementsToCompare.size() == 2) {
                    currentPremise = cnfConvert(currentPremise, lastElem, elementsToCompare, element.getOperator());
                    if (currentPremise.contains(doubleNegationOp)) {
                        workWithDoubleNegation(currentPremise, element.getOperator());
                    }

                }
                promiseWithElements.put(element.getOperator(), elementsToCompare);
                mapForCNF.put(currentPremise, promiseWithElements);
            }
        }

        for (int i = 0; i < listOfPremises.size() - 1; i++) {
            String element = listOfPremises.get(i);
            System.out.println(element);
        }

        System.out.println("=============");
        lastElem = negationElement(lastElem);
        System.out.println(lastElem);
        System.out.println("=============");


    }

    private static String[] trimByOperator(String currentPremise, String s) {
        return currentPremise.trim()
                .split(s);
    }

    private static String workWithDoubleNegation(String premise, String operator) {
        if (premise.contains(openParenthesis) && premise.contains(closeParenthesis)) {
            String[] textInsideParenthesisT = substringsBetween(premise, openParenthesis, closeParenthesis);
            int indexOfP = indexOf(premise, closeParenthesis);
            String firstSubstring = substring(premise, 0, indexOfP + 1);
            String secondSubstring = substring(premise, indexOfP + 1);

            List<String> compOper = Arrays.asList(textInsideParenthesisT);
            ArrayList<String> updatableList = new ArrayList<>(compOper);
            String firstElemFromInitialPremise = updatableList.get(0);
            String secondElemFromInitialPremise = updatableList.get(1);

            if (firstElemFromInitialPremise.contains(doubleNegationOp)) {
                String[] elements = trimByOperator(firstElemFromInitialPremise, orOperator);
                List<String> elemList = Arrays.asList(elements);
                ArrayList<String> elemToAdjust = new ArrayList<>(elemList);
                String firstElem = elemToAdjust.get(0);
                String secondElem = elemToAdjust.get(1);
                if (firstElem.contains(doubleNegationOp)) {
                    firstElem.split(firstElem);
                }
            }
            if (secondElemFromInitialPremise.contains(doubleNegationOp)) {
                String[] elements = substringsBetween(secondElemFromInitialPremise, openParenthesis, closeParenthesis);

            }

//            switch (operator) {
//                case orOperator:
////                    code;
//                case andOperator:
////                    code;
//                case equivalent:
//                    if (secondSubstring.contains(andOperator)) {
//                        updatableList.add(andOperator);
//                        int indexOfOP = indexOf(secondSubstring, openParenthesis);
//                        String withoutOperator = substring(secondSubstring, indexOfOP);
//
//                        premise = firstSubstring + updatableList.get(updatableList.size() - 1) + withoutOperator;
//                    }
//                case implyOperator:
////                    code;
//            }
        }

        return premise;
    }

    private static String negationElement(String elem) {
        return negateTheValue + elem;
    }

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

    private static boolean setupElements(String element, String logicOperator, boolean orOperatorToTest, boolean andOperatorToTest) {
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

    public static String cnfConvert(String currentPremise, String endGoal
            , ArrayList<LogicalElement> premises, String operator
    ) {
        LogicalElement firstElement = premises.get(0);
        LogicalElement secondElement = premises.get(1);

        if (operator.contains(equivalent)) {
            //~ = negation of first elem = implication elim.
            currentPremise = openParenthesis + negationElement(firstElement.getElementName())
                    + orOperator + secondElement.getElementName() + closeParenthesis
                    //conjuncts the 2 parenthesis
                    + andOperator +
                    //negates the second element has and or but also keeps the first
                    openParenthesis + negationElement(secondElement.getElementName())
                    + orOperator + firstElement.getElementName() + closeParenthesis;
        }
        if (operator.contains(implyOperator)) {
            //~ = negation of first elem = implication elim.
            currentPremise = negationElement(firstElement.getElementName())
                    + orOperator + secondElement.getElementName();
        }

        return currentPremise;
    }


//    public static boolean functionPlResolution(LinkedList<Formula> premises, String regex, String endGoal) {
//        LinkedList<Formula> clauses = cnfConvert(premises && negationElement(endGoal));
//        LinkedList<Formula> newListOfClauses = new LinkedList<>();
//
//        if (regex.contains(implyOperator)) {
//            return !firstElement.getTorF() || secondElement.getTorF(); //~ = negation of first elem = implication elim.
//        } else if (regex.contains(equivalent)) {
//            return (firstElement.getTorF() && secondElement.getTorF())
//                    || (!firstElement.getTorF() && !secondElement.getTorF()); //"=" <=> equivalent.
//        }
//
//
////        foreach(c1, c2) in selectClasuses (clauses) {
////                LinkedList < Formula > resolvents = plResolve(c1, c2);
////
////        if (NIL apartine resolvents)return true;
////        newListOfClauses = newListOfClauses.add(resolvents);
////        }
////        if (newListOfClauses is inOrEquals clauses)return false;
////        clauses = clauses.add(newListOfClauses)
//        return true;
//    }

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
}
