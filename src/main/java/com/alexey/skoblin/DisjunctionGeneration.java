package com.alexey.skoblin;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    /*
        Использование дизъюнкции, разбор случаев (du - disjunction usage):
        du[i](f[0],f[1]):(G->D) <- f[j]:(G B[j] -> D),j:{0,1},G[i]==(B[0]vB[1]),
    */
public class DisjunctionGeneration {

    // Константы для идентификаторов полей и шаблонов
    private static final String ID = "id";
    private static final String OUTPUT_FIRST_FUNCTION = "f1st";
    private static final String OUTPUT_SECOND_FUNCTION = "f2st";
    private static final String INPUT_STRING = "input";
    private static final String OUTPUT_STRING = "output";

    // Регулярные выражения для обработки входных данных
    private static final String EXP_WITHOUT_CAPTURE = "(?:(?:[A-Z]\\([a-z]\\))|(?:[a-z])|(?:A[a-z])|(?:E[a-z]))";
    private static final String EXP = "((?:(?:[A-Z]\\([a-z]\\))|(?:[a-z])|(?:A[a-z])|(?:E[a-z])|(?:[A-Z]\\([a-z],[a-z]\\)))+)";

    // Методы для формирования частей регулярного выражения
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
        return MessageFormat.format("(?<{3}>(?:(?:{0}{2}\\|{2}{1})|(?:{0}{2}{1})|(?:{0}{2}=>{2}{1}))+)", bracketIn, bracketOut, EXP_WITHOUT_CAPTURE, INPUT_STRING);
    }

    private String getInputStringWithoutCapture() {
        String bracketIn = "(?:\\(){0,1}+";
        String bracketOut = "(?:\\)){0,1}+";
        return MessageFormat.format("(?:(?:{0}{2}(\\|){2}{1})|(?:{0}{2}(=>){2}{1})|(?:{0}{2}{1}))", bracketIn, bracketOut, EXP);
    }

    private String getOutputString() {
        String bracketIn = "(?:\\(){0,1}+";
        String bracketOut = "(?:\\)){0,1}+";
        return MessageFormat.format("(?<{2}>(?:{0}{3}{1})+)", bracketIn, bracketOut, OUTPUT_STRING, EXP);
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

    // Метод для разбора строки в массив
    public List<List<String>> getInputStringInArray(String input) {
        Pattern pattern = Pattern.compile(getInputStringWithoutCapture());
        Matcher matcher = pattern.matcher(input);
        List<List<String>> list = new ArrayList<>();
        while (matcher.find()) {
            List<String> innerList = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) == null) {
                    continue;
                }
                innerList.add(matcher.group(i));
            }
            list.add(innerList);
        }
        return list;
    }

    // Метод для выполнения программы
    public void run() {
        Scanner in = new Scanner(System.in);
        System.out.println("Input: ");
        String input = in.nextLine();
        in.close();
        Pattern pattern = Pattern.compile(getRegEx());
        Matcher matcher = pattern.matcher(input);
        StringBuilder builder = new StringBuilder();
        if (matcher.find()) {
            try {
                // Извлечение данных из строки
                Integer id = Integer.parseInt(matcher.group(ID));
                String outputFirstFunction = matcher.group(OUTPUT_FIRST_FUNCTION);
                String outputSecondFunction = matcher.group(OUTPUT_SECOND_FUNCTION);
                String inputString = matcher.group(INPUT_STRING);
                String outputString = matcher.group(OUTPUT_STRING);
                List<List<String>> list = getInputStringInArray(inputString);

                Collections.reverse(list);
                // Проверка условий и формирование вывода
                if (list.get(id).stream().count() > 2) {
                    if (list.get(id).get(1).equals("|")) {
                        builder.append(outputFirstFunction).append(":").append(inputString).append(list.get(id).get(0)).append("->").append(outputString);
                        builder.append("\n");
                        builder.append(outputSecondFunction).append(":").append(inputString).append(list.get(id).get(2)).append("->").append(outputString);
                    } else {
                        builder.append("Invalid id.");
                    }
                } else {
                    builder.append("Invalid id.");
                }
            } catch (Exception e) {
                builder.append("Unknown exceptions");
            }
        } else {
            builder.append("Invalid string.");
        }
        System.out.println("Output:");
        System.out.println(builder.toString());
    }

        public static void main(String[] args) {
            new DisjunctionGeneration().run();
        }


}
