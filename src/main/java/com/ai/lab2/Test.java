package com.ai.lab2;

import com.ai.utils.Constant;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.ai.utils.Lab_Utils.*;
import static com.ai.utils.RegexOperator.*;
import static org.apache.commons.lang3.StringUtils.*;

public class Test {
    static LinkedHashMap<String, HashMap<String, ArrayList<LogicalElement>>> mapForCNF = new LinkedHashMap<>();
    static LinkedHashMap<String, ArrayList<LogicalElement>> promiseWithElements = new LinkedHashMap<>();
    static AtomicBoolean noDerivationPossible = new AtomicBoolean();

    static AtomicReference<String> atomicStringReference = new AtomicReference<>();

    public static AtomicReference<String> getAtomicStringReference() {
        return atomicStringReference;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(Constant.small_ex_1));
        AtomicInteger count = new AtomicInteger();

        ArrayList<String> listOfPremises = new ArrayList<>();
        LinkedList<String> normalAndCNFResults = new LinkedList<>();

        while (sc.hasNext()) {
            String line = lowerCase(sc.nextLine());
            if (line.startsWith("#")) continue;
            if (line.trim().isEmpty()) continue;
            listOfPremises.add(line);
        }

        String lastElem = listOfPremises.get(listOfPremises.size() - 1);

        // print out all premises except the goal
        System.out.println("=============");
        for (int i = count.get(); i < listOfPremises.size() - 1; i++) {
            count.getAndIncrement();
            String element = listOfPremises.get(i);
            System.out.println(count + ". " + element);
        }

        // print goal
        System.out.println("=============");
        count.getAndIncrement();
        System.out.println(count + ". " + negationElement(lastElem));
        AtomicInteger lastParanthesisCount = new AtomicInteger(count.get() + 1);
        System.out.println("=============");
        ArrayList<String> result = new ArrayList<>();
        String current = listOfPremises.get(listOfPremises.size() - 1);
        // if (lastElem.length() == 1 || lastElem.length() == 2) {

        for (int i = listOfPremises.size(); i > 0; i--) {
            count.getAndIncrement();
            int downTop = i - 1;

            if (downTop != 0) {
                String[] input = current.split("\\s+");
                String[] clauses = listOfPremises.get(downTop - 1).split("\\s+");
                ArrayList<String> newInput = new ArrayList<>(Arrays.asList(input));
                ArrayList<String> newClauses = new ArrayList<>(Arrays.asList(clauses));

                int startingCount = 0;
                boolean removedIt = false;
                for (int x = 0; x < newInput.size(); x++) {
                    // handle only one clause
                    if (clauses.length == 1 && !newClauses.isEmpty()) {
                        if (input[x].contains("v")) {
                            continue;
                        }
                        if (removedIt) {
                            newClauses.add(0, input[x]);
                            continue;
                        }
                        String firstClause = newInput.get(x).replace("~", "");
                        String secondClause = newClauses.get(startingCount).replace("~", "");

                        if (firstClause.equals(secondClause)) {
                            newClauses.remove(newClauses.size() - 1);
                            removedIt = true;
                        } else {
                            newClauses.add(0, newInput.get(x));
                            startingCount++;
                        }
                    }

                    else {
                        for (int j = startingCount; j < newClauses.size() - startingCount; j++) {
                            if (input.length == 1) {
                                if (newClauses.get(j).contains("v")) {
                                    continue;
                                }
                                if (removedIt ) {
                                    newClauses.remove(newClauses.get(j-1));
                                    startingCount++;
                                    continue;
                                }
                            }

                            if (newClauses.get(j).contains("v") || newInput.get(x).contains("v")) {
                                continue;
                            }



                            String firstClause = newInput.get(x).replace("~", "");
                            String secondClause = newClauses.get(j).replace("~", "");

                            if (firstClause.equals(secondClause)) {
                                if (j != 0) {
                                    newClauses.remove(j - 1);
                                    newClauses.remove(j - 1);
                                } else {
                                    newClauses.remove(j);
                                    if (newInput.size() == 1) {
                                        removedIt = true;
                                    }
                                }
                            }
                            //System.out.println(input[x] + "   "   + newClauses.get(j));
                            else if (j == newClauses.size() - 1) {
                                newClauses.add(0, newInput.get(x));
                                startingCount++;
                            }
                        }
                    }
                }
                current = StringUtils.join(newClauses, " v ");
                if (!newClauses.isEmpty()) {
                    System.out.println(lastParanthesisCount.getAndIncrement() + ". " + current + "(" + downTop+ ", "  + ")");
                } else {
                    System.out.println(lastParanthesisCount.getAndIncrement() + ". " + "NIL (" + downTop + ", " + ")");
                }
                /*
                current = StringUtils.join(clauses, "");
                result.clear();
                System.out.println("=============");
                System.out.println(current);
                 */
            }
        }
    }
}
