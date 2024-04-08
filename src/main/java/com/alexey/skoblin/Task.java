package com.alexey.skoblin;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {

    // Перечисление для типов элементов регулярного выражения
    enum Type {
        LeftPart, Ag, Bracket, BracketClosing, Ig, Cg, ExpE, ExpF1E, ExpF1EF, ExpF1E1P, ExpF2E, ExpF2EF, ExpF2E1P, ExpF2E2P
    }

    // Шаблоны регулярных выражений для каждого типа элементов
    private static final String REGEX_LEFT_PART = MessageFormat.format("(?:(?<{0}>.*)->)", Type.LeftPart.name());
    private static final String REGEX_AG = MessageFormat.format("(?:A(?<{0}>[a-z]))", Type.Ag.name());
    private static final String REGEX_BRACKET = MessageFormat.format("(?<{0}>\\()", Type.Bracket.name());
    private static final String REGEX_BRACKETS_OUT = MessageFormat.format("(?<{0}>\\))", Type.BracketClosing.name());
    private static final String REGEX_IG = MessageFormat.format("(?<{0}>=>)", Type.Ig.name());
    private static final String REGEX_CG = MessageFormat.format("(?<{0}>&)", Type.Cg.name());
    private static final String REGEX_EXP_1_ELEMENT = MessageFormat.format("(?<{0}>[a-z])", Type.ExpE.name());
    private static final String REGEX_EXP_FUNCTION_1_ELEMENT = MessageFormat.format("(?<{0}>(?<{1}>[A-Z])\\((?<{2}>[a-z])\\))", Type.ExpF1E.name(), Type.ExpF1EF.name(), Type.ExpF1E1P.name());
    private static final String REGEX_EXP_FUNCTION_2_ELEMENT = MessageFormat.format("(?<{0}>(?<{1}>[A-Z])\\((?<{2}>[a-z]),(?<{3}>[a-z])\\))", Type.ExpF2E.name(), Type.ExpF2EF.name(), Type.ExpF2E1P.name(), Type.ExpF2E2P.name());
    private static final String EMPTY = "";

    // Метод для получения общего регулярного выражения
    public String getRegEx() {
        return REGEX_LEFT_PART
                + "|" + REGEX_AG
                + "|" + REGEX_BRACKET
                + "|" + REGEX_BRACKETS_OUT
                + "|" + REGEX_IG
                + "|" + REGEX_CG
                + "|" + REGEX_EXP_FUNCTION_1_ELEMENT
                + "|" + REGEX_EXP_FUNCTION_2_ELEMENT;
    }

    // Метод выполнения программы
    public void run() {
        System.out.println(getRegEx()); // Вывод регулярного выражения для отладки
        Scanner in = new Scanner(System.in);
        System.out.println("Input: ");
        String input = in.nextLine(); // Получение входной строки
        in.close();

        // Компиляция регулярного выражения
        Pattern pattern = Pattern.compile(getRegEx());
        // Создание объекта Matcher для поиска соответствий во входной строке
        Matcher matcher = pattern.matcher(input);

        // Инициализация переменных
        StringBuilder leftPart = new StringBuilder(EMPTY);
        StringBuilder rightPart = new StringBuilder(EMPTY);
        List<String[]> list = new ArrayList<>();
        StringBuilder output = new StringBuilder(EMPTY);
        Map<String, String> map = new HashMap<>();
        Type lastType = null;

        // Поиск соответствий во входной строке
        while (matcher.find()) {
            // Обработка каждого типа соответствия
            if (matcher.group(Type.LeftPart.name()) != null) {
                leftPart = new StringBuilder(matcher.group(Type.LeftPart.name()));
                lastType = Type.LeftPart;
            } else if (matcher.group(Type.Ag.name()) != null) {
                // Обработка Ag
                String symbol = matcher.group(Type.Ag.name());
                String name = generateMissingLetter(input.replace(symbol, EMPTY));
                list.add(new String[]{symbol, name});
                map.put(symbol, name);
                output.append("ag(");
                lastType = Type.Ag;
            } else if (matcher.group(Type.Bracket.name()) != null) {
                // Обработка Bracket
                if (output.lastIndexOf("(") != output.length() - 1) {
                    output.append("(");
                }
                removeLast(list, leftPart);
            } else if (matcher.group(Type.BracketClosing.name()) != null) {
                // Обработка BracketClosing
                lastType = goBracketClosing(lastType, list, pattern, leftPart, matcher, output, rightPart);
            } else if (matcher.group(Type.Ig.name()) != null) {
                // Обработка Ig
                removeLast(list, leftPart);
                output.append("ig(");
                lastType = Type.Ig;
            } else if (matcher.group(Type.Cg.name()) != null) {
                // Обработка Cg
                output.append("cg(");
                lastType = Type.Cg;
            } else if (matcher.group(Type.ExpF1E.name()) != null) {
                // Обработка ExpF1E
                if (lastType == Type.Ag) {
                    removeLast(list, leftPart);
                }
                String function = matcher.group(Type.ExpF1EF.name());
                String parameter = matcher.group(Type.ExpF1E1P.name());
                String noEditFunction = matcher.group(Type.ExpF1E.name());
                if (map.get(parameter) != null) {
                    parameter = map.get(parameter);
                }
                list.add(new String[]{noEditFunction, function + "(" + parameter + ")"});
            } else if (matcher.group(Type.ExpF2E.name()) != null) {
                // Обработка ExpF2E
                if (lastType == Type.Ag) {
                    removeLast(list, leftPart);
                }
                String function = matcher.group(Type.ExpF2EF.name());
                String parameter1 = matcher.group(Type.ExpF2E1P.name());
                String parameter2 = matcher.group(Type.ExpF2E2P.name());
                String noEditFunction = matcher.group(Type.ExpF2E.name());
                if (map.get(parameter1) != null) {
                    parameter1 = map.get(parameter1);
                }
                if (map.get(parameter2) != null) {
                    parameter2 = map.get(parameter2);
                }
                list.add(new String[]{noEditFunction, function + "(" + parameter1 + "," + parameter2 + ")"});
            }
        }
        // Обработка завершающей скобки и вывод результата
        if (output.lastIndexOf("(") == output.length() - 1) {
            goBracketClosing(lastType, list, pattern, leftPart, matcher, output, rightPart);
        }
        System.out.println("Output: ");
        System.out.println(output + ":" + leftPart + "->" + rightPart);
    }

    // Метод для удаления последнего элемента из списка и добавления его к leftPart
    private static void removeLast(List<String[]> list, StringBuilder leftPart) {
        if (!list.isEmpty()) {
            leftPart.append(list.get(list.size() - 1)[1]);
            list.remove(list.size() - 1);
        }
    }

    // Метод для обработки завершающей скобки
    private static Type goBracketClosing(Type lastType, List<String[]> list, Pattern pattern, StringBuilder leftPart, Matcher matcher, StringBuilder output, StringBuilder rightPart) {
        if (lastType != Type.BracketClosing) {
            if (lastType != Type.Cg) {
                int id = -1;
                String lastElement = list.get(list.size() - 1)[0];
                List<String[]> listElements = new ArrayList<>();
                temporaryListVariables(leftPart, listElements);
                for (String[] element : listElements) {
                    if (element[0].equals(lastElement)) {
                        id = listElements.indexOf(element);
                        break;
                    }
                }
                if (id == -1) {
                    leftPart.append(list.get(list.size() - 1)[1]);
                    id = 0;
                }
                output.append("pr[").append(id).append("]");
                rightPart.append("(").append(list.get(list.size() - 1)[1]).append(")");
                list.remove(list.size() - 1);
            } else {
                int id1 = -1;
                int id2 = -1;
                String last1Element = list.get(list.size() - 2)[0];
                String last2Element = list.get(list.size() - 1)[0];
                List<String[]> listElements = new ArrayList<>();
                temporaryListVariables(leftPart, listElements);
                for (String[] element : listElements) {
                    if (element[0].equals(last1Element)) {
                        id1 = listElements.indexOf(element);
                        break;
                    }
                }
                for (String[] element : listElements) {
                    if (element[0].equals(last2Element)) {
                        id2 = listElements.indexOf(element);
                        break;
                    }
                }
                if (id1 == -1) {
                    leftPart.append(list.get(list.size() - 2)[1]);
                    id1 = 0;
                    if (id2 == -1) {
                        leftPart.append(list.get(list.size() - 1)[1]);
                        id1 = 1;
                        id2 = 0;
                    }
                }
                if (id2 == -1) {
                    leftPart.append(list.get(list.size() - 1)[1]);
                    id1++;
                    id2 = 0;
                }
                output.append("pr[").append(id1).append("]");
                output.append(",");
                output.append("pr[").append(id2).append("]");
                rightPart.append("(")
                        .append(list.get(list.size() - 2)[1])
                        .append("&")
                        .append(list.get(list.size() - 1)[1])
                        .append(")");
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
            }
        }
        output.append(")");
        lastType = Type.BracketClosing;
        return lastType;
    }

    // Метод для временного списка переменных
    private static void temporaryListVariables(StringBuilder leftPart, List<String[]> listElements) {
        String reg = REGEX_EXP_FUNCTION_1_ELEMENT + "|" + REGEX_EXP_FUNCTION_2_ELEMENT + "|" + REGEX_EXP_1_ELEMENT;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(leftPart);
        while (matcher.find()) {
            if (matcher.group(Type.ExpF1E.name()) != null) {
                listElements.add(new String[]{matcher.group(Type.ExpF1E.name()), matcher.group(Type.ExpF1EF.name()), matcher.group(Type.ExpF1E1P.name())});
            } else if (matcher.group(Type.ExpF2E.name()) != null) {
                listElements.add(new String[]{matcher.group(Type.ExpF2E.name()), matcher.group(Type.ExpF2EF.name()), matcher.group(Type.ExpF2E1P.name()), matcher.group(Type.ExpF2E2P.name())});
            } else if (matcher.group(Type.ExpE.name()) != null) {
                listElements.add(new String[]{matcher.group(Type.ExpE.name()), matcher.group(Type.ExpE.name())});
            }
        }
        Collections.reverse(listElements);
    }

    // Метод для генерации отсутствующей буквы в строке
    public static String generateMissingLetter(String str) {
        // Создаем массив булевых значений размером 26, соответствующий английскому алфавиту
        boolean[] alphabet = new boolean[26];
        // Заполняем массив значениями true для букв, которые присутствуют в строке
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                alphabet[Character.toLowerCase(c) - 'a'] = true;
            }
        }
        // Генерируем случайную букву, которая отсутствует в строке
        Random rand = new Random();
        char missingLetter;
        do {
            missingLetter = (char) (rand.nextInt(26) + 'a');
        } while (alphabet[missingLetter - 'a']);

        return String.valueOf(missingLetter);
    }

    // Главный метод программы
    public static void main(String[] args) {
        new Task().run();
    }
}
