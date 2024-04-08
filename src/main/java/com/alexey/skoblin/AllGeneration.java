package com.alexey.skoblin;

import java.text.MessageFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Программа для обработки выражений вида "Порождение всеобщности (ag - "all" generation)"
 */
public class AllGeneration {

    // Константы для идентификаторов полей и шаблонов
    private static final String OUTPUT_FUNCTION = "function"; // Имя выходной функции
    private static final String INPUT_STRING = "input"; // Входная строка

    // Регулярные выражения для обработки входных данных
    private static final String EXP_WITHOUT_CAPTURE = "(?:(?:[A-Z]\\([a-z]\\))|(?:[a-z])|(?:A[a-z])|(?:E[a-z])|(?:[A-Z]\\([a-z],[a-z]\\)))";
    private static final String OUTPUT_SMALL_SYMBOL = "symb"; // Имя символа входного утверждения
    private static final String OUTPUT_SYMBOL = "Bsymb"; // Имя символа выходного утверждения

    // Методы для формирования частей регулярного выражения
    private String getName() {
        return "ag"; // Название операции "Порождение всеобщности"
    }

    private String getOutputFunction() {
        return MessageFormat.format("(?<{0}>[a-z])", AllGeneration.OUTPUT_FUNCTION); // Шаблон для извлечения имени функции
    }

    private String getOutputAllFunctions() {
        return MessageFormat.format("\\({0}\\)", getOutputFunction()); // Шаблон для группировки имени функции
    }

    private String getInputString() {
        String bracketIn = "(?:\\(){0,1}+";
        String bracketOut = "(?:\\)){0,1}+";
        return MessageFormat.format("(?<{3}>(?:(?:{0}{2}\\|{2}{1})|(?:{0}{2}{1})|(?:{0}{2}=>{2}{1}))+)", bracketIn, bracketOut, EXP_WITHOUT_CAPTURE, INPUT_STRING); // Шаблон для извлечения входной строки
    }

    private String getOutputString() {
        return MessageFormat.format("E(?<{0}>[a-z])(?<{1}>[A-Z])\\(\\k<{0}>\\)", OUTPUT_SMALL_SYMBOL, OUTPUT_SYMBOL); // Шаблон для извлечения выходной строки
    }

    private String getRegEx() {
        return getName()
                + getOutputAllFunctions()
                + ":"
                + getInputString() + "+"
                + "->"
                + getOutputString(); // Формирование регулярного выражения для поиска соответствий
    }

    // Метод для выполнения программы
    public void run() {
        //System.out.println(getRegEx()); // Вывод регулярного выражения для отладки
        Scanner in = new Scanner(System.in);
        System.out.println("Input: ");
        String input = in.nextLine(); // Получение входной строки
        in.close();
        Pattern pattern = Pattern.compile(getRegEx()); // Компиляция регулярного выражения
        Matcher matcher = pattern.matcher(input); // Создание объекта Matcher для поиска соответствий
        StringBuilder builder = new StringBuilder();
        if (matcher.find()) { // Если найдено соответствие
            try {
                // Извлечение данных из найденного соответствия
                String outputFunction = matcher.group(OUTPUT_FUNCTION);
                String inputString = matcher.group(INPUT_STRING);
                String outputSymbol = matcher.group(OUTPUT_SYMBOL);

                // Формирование выходной строки
                builder.append(outputFunction);
                builder.append(":");
                builder.append(inputString);
                builder.append("a");
                builder.append("<-");
                builder.append(outputSymbol);
                builder.append("(");
                builder.append("a");
                builder.append(")");
            } catch (Exception e) {
                builder.append("Unknown exceptions");
            }
        } else {
            builder.append("Invalid string.");
        }
        System.out.println("Output:");
        System.out.println(builder.toString()); // Вывод результата
    }

    // Точка входа в программу
    public static void main(String[] args) {
        new AllGeneration().run(); // Запуск программы
    }
}
