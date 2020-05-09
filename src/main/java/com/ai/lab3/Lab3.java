package com.ai.lab3;

import com.ai.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

import static com.ai.lab2.CookingAssistant.retrieveKnowledge;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class Lab3 {

    public static void main(String[] args) throws FileNotFoundException {

        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        if(line!=null){
            Scanner interactive = new Scanner(new File(Constant.coffee));
            Scanner inputs = new Scanner(new File(Constant.coffee_input));
            LinkedList<String> inputsToTest = new LinkedList<>();

            while (inputs.hasNext()) {
                String knowledge = lowerCase(inputs.nextLine());
                if (knowledge.startsWith("#")) continue;
                if (knowledge.trim().isEmpty()) continue;
                inputsToTest.add(knowledge);
            }
        }

    }
}
