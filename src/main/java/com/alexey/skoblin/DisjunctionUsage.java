package com.alexey.skoblin;

import java.text.MessageFormat;
import java.util.Scanner;

public class DisjunctionUsage {
    private static final String ID = "Id";
    private static final String OUTPUT_FIRST_FUNCTION = "f1st";
    private static final String OUTPUT_SECOND_FUNCTION = "f2st";

    private static final String EXP_WITHOUT_CAPTURE = "(?:(?:[A-Z]\\([a-z]\\))|(?:[a-z])|(?:A[a-z])|(?:E[a-z]))";
    private static final String EXP = "((?:(?:[A-Z]\\([a-z]\\))|(?:[a-z])|(?:A[a-z])|(?:E[a-z])|(?:[A-Z]\\([a-z],[a-z]\\)))+)";

    private String getName() {
        return "du";
    }

    private String getId() {
        return MessageFormat.format("\\[(?<{0}>\\d+)\\]", ID);
    }

    private String getOutputFunction(String name) {
        return MessageFormat.format("(?<{0}>[a-z])", name);

    }

    private String getOutputAllFunctions() {
        return MessageFormat.format("\\({0},{1}\\)", getOutputFunction(OUTPUT_FIRST_FUNCTION), getOutputFunction(OUTPUT_SECOND_FUNCTION));
    }

    private String getInputString() {
        String bracketIn = "(?:\\(){0,1}+";
        String bracketOut = "(?:\\)){0,1}+";
        return MessageFormat.format("((?:(?:{0}{2}\\|{2}{1})|(?:{0}{2}{1}))+)", bracketIn, bracketOut, EXP_WITHOUT_CAPTURE);
    }

    private String getOutputString() {
        return EXP;
    }

    private String getRegEx() {
        return getName()
                + getId()
                + getOutputAllFunctions()
                + ":"
                + getInputString() + "+"
                + "->"
                + getOutputString();
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        System.out.println("Input: ");
        String input = in.nextLine();
        in.close();
    }


    public static void main(String[] args) {
        DisjunctionUsage disjunctionUsage = new DisjunctionUsage();
        System.out.println(disjunctionUsage.getRegEx());
    }
}