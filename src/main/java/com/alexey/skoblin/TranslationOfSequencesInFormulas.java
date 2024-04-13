package com.alexey.skoblin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
s... = As()
..r.. = ...>Ar()
Если только не стоит перед этим E иначе, не писать A

P(s)T(s) = P(s)&T(s)
P(s)T(s,s) = P(s)&T(s,s)


(?<symb>[a-z]) = A<symbol>(...)
если есть что то до него, то поставить перед ним >

        (?<function>[A-Z]\((?:[a-z]|(?:[a-z],[a-z]))\))
если есть такая же до него, то поставить знак &, если его еще нет

& = &
E = E
-> = =>

Пример разбора
rT(r)dQ(d,r)&Z(r)S(r)->EcD(c)

r = Ar(
        T(r) = Ar(T(r)
d = Ar(T(r)>Ad(
        Q(d,r) = Ar(T(r)>Ad(Q(d,r)
& = Ar(T(r)>Ad(Q(d,r)&
Z(r) = Ar(T(r)>Ad(Q(d,r)&Z(r)
S(r) = Ar(T(r)>Ad(Q(d,r)&Z(r)&S(r)
-> = Ar(T(r)>Ad(Q(d,r)&Z(r)&S(r)=>
E = Ar(T(r)>Ad(Q(d,r)&Z(r)&S(r)=>E
        c = Ar(T(r)>Ad(Q(d,r)&Z(r)&S(r)=>Ec(
        D(c) = Ar(T(r)>Ad(Q(d,r)&Z(r)&S(r)=>Ec(D(c))))))

        (?<function>[A-Z]\((?:[a-z]|(?:[a-z],[a-z]))\))|(?<symb>[a-z])|(?<spec>&)|(?<in>->)|(?<exist>E)
*/



public class TranslationOfSequencesInFormulas {

    private static final String NAME_FUNCTION = "function";
    private static final String NAME_SYMBOL = "symbol";
    private static final String NAME_CONJUNCTION_SYMBOL = "spec";
    private static final String NAME_TRANSITIONAL_SYMBOL = "transit";
    private static final String NAME_EXISTENCE_SYMBOL = "exist";
    private static final String EMPTY = "";

    // Получение регулярного выражения для функций
    private String getFunction() {
        return MessageFormat.format("(?<{0}>[A-Z]\\((?:[a-z]|(?:[a-z],[a-z]))\\))", NAME_FUNCTION);
    }

    // Получение регулярного выражения для символов
    private String getSymbol() {
        return MessageFormat.format("(?<{0}>[a-z])", NAME_SYMBOL);
    }

    // Получение регулярного выражения для специальных символов
    private String getConjunctionSymbol() {
        return MessageFormat.format("(?<{0}>&)", NAME_CONJUNCTION_SYMBOL);
    }

    // Получение регулярного выражения для переходных символов
    private String getTransitionalSymbol() {
        return MessageFormat.format("(?<{0}>->)", NAME_TRANSITIONAL_SYMBOL);
    }

    // Получение регулярного выражения для символов существования
    private String getExistenceSymbol() {
        return MessageFormat.format("(?<{0}>E)", NAME_EXISTENCE_SYMBOL);
    }

    // Составление регулярного выражения из различных компонентов
    private String getRegEx() {
        return getFunction()
                + "|" + getSymbol()
                + "|" + getConjunctionSymbol()
                + "|" + getTransitionalSymbol()
                + "|" + getExistenceSymbol();
    }

    // Метод выполнения программы
    public void run() {
        System.out.println(getRegEx()); // Вывод регулярного выражения для отладки
        Scanner in = new Scanner(System.in);
        System.out.println("Input: ");
        String input = in.nextLine(); // Получение входной строки
        in.close();
        Pattern pattern = Pattern.compile(getRegEx()); // Компиляция регулярного выражения
        Matcher matcher = pattern.matcher(input); // Создание объекта Matcher для поиска соответствий
        List<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        String last = EMPTY;
        while (matcher.find()) {
            // Проверка соответствия группы функции
            if (matcher.group(NAME_FUNCTION) != null) {
                if (last.equals(NAME_FUNCTION))
                    result.add("&");
                result.add(matcher.group(NAME_FUNCTION));
                last = NAME_FUNCTION;
            } else if (matcher.group(NAME_SYMBOL) != null) {
                // Проверка соответствия группы символа
                if (!(last.equals(EMPTY) || last.equals(NAME_TRANSITIONAL_SYMBOL) || last.equals(NAME_EXISTENCE_SYMBOL))) {
                    result.add(">");
                }
                if (!last.equals(NAME_EXISTENCE_SYMBOL))
                    result.add("A");
                result.add(matcher.group(NAME_SYMBOL));
                result.add("(");
                stack.add(")");
                last = NAME_SYMBOL;
            } else if (matcher.group(NAME_CONJUNCTION_SYMBOL) != null) {
                // Проверка соответствия группы специального символа
                result.add(matcher.group(NAME_CONJUNCTION_SYMBOL));
                last = NAME_CONJUNCTION_SYMBOL;
            } else if (matcher.group(NAME_TRANSITIONAL_SYMBOL) != null) {
                // Проверка соответствия группы переходного символа
                result.add("=>");
                last = NAME_TRANSITIONAL_SYMBOL;
            } else if (matcher.group(NAME_EXISTENCE_SYMBOL) != null) {
                // Проверка соответствия группы символа существования
                result.add(matcher.group(NAME_EXISTENCE_SYMBOL));
                last = NAME_EXISTENCE_SYMBOL;
            }
        }
        result.addAll(stack);
        String out = String.join("", result);
        System.out.println("Output: ");
        System.out.println(out);
    }

    public static void main(String[] args) {
        new TranslationOfSequencesInFormulas().run();
    }

}
