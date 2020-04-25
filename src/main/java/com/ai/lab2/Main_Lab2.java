package com.ai.lab2;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.substring;

public class Main_Lab2 {

    static LinkedHashMap<String, HashMap<String, ArrayList<LogicalElement>>> mapForCNF = new LinkedHashMap<>();
    static LinkedHashMap<String, ArrayList<LogicalElement>> promiseWithElements = new LinkedHashMap<>();

    private static HashMap<String, HashMap<ArrayList<LogicalElement>, Boolean>> logicalElAndOperator = new HashMap<>();
    private static ArrayList<Boolean> operators = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(Constant.small_ex_1));
//        Scanner sc = new Scanner(new File(Constant.small_example_for_operators));

        int counter = 0;
        int derivedCounter = 0;

        ArrayList<String> listOfPremises = new ArrayList<>();
        ArrayList<String> normalAndCNFResults = new ArrayList<>();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.startsWith("#")) continue;
            if (line.trim().isEmpty()) continue;
            listOfPremises.add(line);
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
                    currentPremise = cnfConvert(currentPremise, elementsToCompare, element.getOperator());
                }
                promiseWithElements.put(element.getOperator(), elementsToCompare);
                mapForCNF.put(currentPremise, promiseWithElements);
                if (!normalAndCNFResults.contains(currentPremise)) normalAndCNFResults.add(currentPremise);
            }

        }

        System.out.println("=============");
        for (int i = counter; i < listOfPremises.size() - 1; i++) {
            String element = listOfPremises.get(i);
            System.out.println(i + ". " + element);
        }

        System.out.println("=============");
        System.out.println(listOfPremises.size() + ". " + negationElement(lastElem));
        System.out.println("=============");

        normalAndCNFResults.set(normalAndCNFResults.size() - 1, negationElement(lastElem));
        derivedCounter = listOfPremises.size();

        for (int i = counter; i <= normalAndCNFResults.size(); i++) {
            String firstEl = normalAndCNFResults.get(counter);
            String secondEl = normalAndCNFResults.get(counter + 1);

            String derivation = derivation(firstEl, secondEl);
            System.out.println((derivedCounter += 1) + ". " +derivation);
            if (derivation != null && !derivation.equals(nextPairs) && !derivation.equals(NIL)) {
                normalAndCNFResults.remove(firstEl);
                normalAndCNFResults.remove(secondEl);

                ArrayList<String> derivedResults = new ArrayList<>();
                derivedResults.add(0, derivation);
                for (int j = 0; j < normalAndCNFResults.size(); j++) {
                    String element = normalAndCNFResults.get(j);
                    derivedResults.add(j + 1, element);
                }
                normalAndCNFResults = new ArrayList<>(derivedResults);

            } else if (derivation.equals(NIL)) {
                System.out.println("=============");
                System.out.println(lastElem + " is true");
                String s = "s";

            } else if (derivation.equals(nextPairs)) {
                String s = "s";


            }
        }

    }

    private static String derivation(String firstClause, String secondClause) {
        if ((firstClause.length() > 1) && (secondClause.length() > 1)) {
            if (firstClause.contains(orOperator) && secondClause.contains(orOperator)) {
                return commonOp(firstClause, secondClause, orOperator);

            } else if (firstClause.contains(andOperator) && secondClause.contains(orOperator)) {
                return differentOp(firstClause, secondClause, andOperator, orOperator);

            } else if (firstClause.contains(orOperator) && secondClause.contains(andOperator)) {
                return differentOp(firstClause, secondClause, orOperator, andOperator);

            } else if (firstClause.contains(andOperator) && secondClause.contains(andOperator)) {
                return commonOp(firstClause, secondClause, andOperator);
            }
        } else if ((firstClause.length() > 1) && (secondClause.length() == 1)) {
            if (firstClause.length() == 2) {
                LogicalElement elem1 = new LogicalElement(firstClause);
                LogicalElement elem2 = new LogicalElement(secondClause);

                if (firstClause.contains(negateTheValue)) {
                    elem2.setElementName(secondClause);
                    elem1.setHasNegation(true);
                }
                if (secondClause.contains(negateTheValue)) {
                    elem2.setHasNegation(false);
                }
                if (elem1.getElementName().equals(elem2.getElementName())) {
                    if (elem1.hasNegation() != elem2.hasNegation()) {
                        return NIL;
                    }
                } else if (!elem1.equals(elem2)) {
                    return nextPairs;
                }
            }

            if (firstClause.contains(orOperator)) {
                return compareExpresionToSingleClause(firstClause, secondClause, orOperator);
            } else if (firstClause.contains(andOperator)) {
                return compareExpresionToSingleClause(firstClause, secondClause, andOperator);
            }
        } else if ((firstClause.length() == 1) && (secondClause.length() > 1)) {
            if (secondClause.length() == 2) {
                LogicalElement elem1 = new LogicalElement(firstClause);
                LogicalElement elem2 = new LogicalElement(secondClause);

                if (firstClause.contains(negateTheValue)) {
                    elem1.setHasNegation(false);
                }
                if (secondClause.contains(negateTheValue)) {
                    elem2.setElementName(firstClause);
                    elem2.setHasNegation(true);
                }
                if (elem1.getElementName().equals(elem2.getElementName())) {
                    if (elem1.hasNegation() != elem2.hasNegation()) {
                        return NIL;
                    }
                } else if (!elem1.equals(elem2)) {
                    return nextPairs;
                }
            }

            if (secondClause.contains(orOperator)) {
                return compareExpresionToSingleClause(firstClause, secondClause, orOperator);
            } else if (secondClause.contains(andOperator)) {
                return compareExpresionToSingleClause(firstClause, secondClause, andOperator);
            }
        }

        return null;
    }

    private static String differentOp(String firstClause, String secondClause, String firstOp, String secondOp) {

        ArrayList<LogicalElement> elemAndNegation = new ArrayList<>();

        String[] comparableOperatorsFirstClause = trimByOperator(firstClause, firstOp);
        extractComparableElements(elemAndNegation, comparableOperatorsFirstClause);

        String[] comparableOperatorsSecondClause = trimByOperator(secondClause, secondOp);
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
        ArrayList<LogicalElement> finalEl = elemAndNegation;

        return finalEl.get(0).getOperator() + finalEl.get(0).getElementName() +
//                + operator +
                finalEl.get(1).getOperator() + finalEl.get(1).getElementName();

    }

    private static String commonOp(String firstClause, String secondClause, String operator) {
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
        return elemAndNegation.get(0).getOperator() + elemAndNegation.get(0).getElementName() + operator +
                elemAndNegation.get(1).getOperator() + elemAndNegation.get(1).getElementName();
    }

    private static String compareExpresionToSingleClause(String firstClause, String secondClause, String operator) {
        ArrayList<LogicalElement> elemAndNegation = new ArrayList<>();
        if (secondClause.length() == 1) {
            String[] comparableOperatorsFirstClause = trimByOperator(firstClause, operator);
            extractComparableElements(elemAndNegation, comparableOperatorsFirstClause);

            LogicalElement logicalCause = new LogicalElement(secondClause);
            elemAndNegation.add(logicalCause);
        } else if (firstClause.length() == 1) {
            String[] comparableOperatorsFirstClause = trimByOperator(secondClause, operator);
            extractComparableElements(elemAndNegation, comparableOperatorsFirstClause);

            LogicalElement logicalCause = new LogicalElement(firstClause);
            elemAndNegation.add(logicalCause);
        }

        LogicalElement firstElemN = elemAndNegation.get(0);
        LogicalElement secondElemN = elemAndNegation.get(1);
        LogicalElement thirdElemN = elemAndNegation.get(2);

        if (firstElemN.getElementName().equals(thirdElemN.getElementName())) {
            if (!firstElemN.getOperator().equals(thirdElemN.getOperator())) {
                elemAndNegation.remove(firstElemN);
                elemAndNegation.remove(thirdElemN);
            }
            return secondElemN.getOperator() + secondElemN.getElementName();
        } else if (secondElemN.getElementName().equals(thirdElemN.getElementName())) {
            if (!secondElemN.getOperator().equals(thirdElemN.getOperator())) {
                elemAndNegation.remove(secondElemN);
                elemAndNegation.remove(thirdElemN);
            }
            return firstElemN.getOperator() + firstElemN.getElementName();
        }

        return null;
    }

    private static void extractComparableElements(ArrayList<LogicalElement> elemAndNegation, String[] comparableOperatorsFirstClause) {
        String firstClauseElem1 = comparableOperatorsFirstClause[0];
        String firstClauseElem2 = comparableOperatorsFirstClause[1];

        if (firstClauseElem1.contains(negateTheValue)) {
            LogicalElement logicalElement = new LogicalElement(firstClauseElem1.replace(negateTheValue, ""));
            logicalElement.setOperator(negateTheValue);
            elemAndNegation.add(logicalElement);
        } else {
            LogicalElement logicalElement = new LogicalElement(firstClauseElem1.replace(negateTheValue, ""));
            logicalElement.setOperator("");
            elemAndNegation.add(logicalElement);
        }
        if (firstClauseElem2.contains(negateTheValue)) {
            LogicalElement logicalElement = new LogicalElement(firstClauseElem2.replace(negateTheValue, ""));
            logicalElement.setOperator(negateTheValue);
            elemAndNegation.add(logicalElement);
        } else {
            LogicalElement logicalElement = new LogicalElement(firstClauseElem2.replace(negateTheValue, ""));
            logicalElement.setOperator("");
            elemAndNegation.add(logicalElement);
        }
    }

    private static String[] trimByOperator(String currentPremise, String s) {
        return currentPremise.trim()
                .split(s);
    }

    private static String removeDoubleNegation(String premise) {
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

    private static String negationElement(String elem) {
        return negateTheValue + elem;
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

    private static String makeEquivalent(LogicalElement firstElement, LogicalElement secondElement) {
        return openParenthesis + negationElement(firstElement.getElementName())
                + orOperator + secondElement.getElementName() + closeParenthesis
                //conjuncts the 2 parenthesis
                + andOperator +
                //negates the second element has and or but also keeps the first
                openParenthesis + negationElement(secondElement.getElementName())
                + orOperator + firstElement.getElementName() + closeParenthesis;
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

    private static String negateOperator(String operator) {
        if (operator.equals(orOperator)) {
            operator = andOperator;
        } else if (operator.equals(andOperator)) operator = orOperator;
        return operator;
    }

//    public static boolean functionPlResolution(LinkedList<LogicalElement> premises, String regex, String endGoal) {
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
}
