package flashcards.service;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LoggerService {

    public static Scanner scanner = new Scanner(System.in);
    public static StringBuilder consoleLogs = new StringBuilder();

    public static void logConsole() {
        outputMessage("File name:");
        String fileName = getUserInput();

        try (PrintWriter printWriter = new PrintWriter(fileName)) {
            printWriter.printf(LoggerService.consoleLogs.toString());
            outputMessage("The log has been saved.\n");
        } catch (FileNotFoundException exception) {
            outputMessage("File not found.\n");
        }
    }

    public static void outputMessage(String message) {
        System.out.println(message);
        consoleLogs.append(message).append("\n");
    }

    public static void outputMessage(String message, String value) {
        System.out.printf(message, value);
        consoleLogs.append(String.format(message, value));
    }

    public static void outputMessage(String message, String firstValue, String secondValue) {
        System.out.printf(message, firstValue, secondValue);
        consoleLogs.append(String.format(message, firstValue, secondValue));
    }

    public static String getUserInput() {
        String input = scanner.nextLine();
        consoleLogs.append(input).append("\n");
        return input;
    }
}
