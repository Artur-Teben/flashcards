package flashcards.service;

import flashcards.model.Flashcard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static flashcards.service.LoggerService.getUserInput;
import static flashcards.service.LoggerService.outputMessage;
import static java.util.stream.Collectors.toList;

public class FlashcardService {

    private static final List<Flashcard> flashcards = new ArrayList<>();
    public static String initialFlashcardsFilePath = null;
    public static String flashcardsSaveFilePath = null;

    public static void addFlashcard() {
        outputMessage("The card:");
        String term = getUserInput();

        if (flashcardWithTermExists(term)) {
            outputMessage("The card \"%s\" already exists.\n\n", term);
            return;
        }

        outputMessage("The definition of the card:");
        String definition = getUserInput();

        if (flashcardWithDefinitionExists(definition)) {
            outputMessage("The definition \"%s\" already exists.\n\n", definition);
            return;
        }

        flashcards.add(new Flashcard(term, definition));
        outputMessage("The pair (\"%s\":\"%s\") has been added.\n\n", term, definition);
    }

    public static void removeFlashcard() {
        outputMessage("Which card?");
        String term = getUserInput();

        if (flashcardWithTermExists(term)) {
            removeFlashcardWithTerm(term);
            outputMessage("The card has been removed.\n");
        } else {
            outputMessage("Can't remove \"%s\": there is no such card.\n\n", term);
        }
    }

    public static void importFlashcards() {
        outputMessage("File name:");
        String fileName = getUserInput();

        importFlashcards(fileName);
    }

    private static int importFlashcards(String fileName) {
        int flashcardCount = 0;

        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNext()) {
                String[] flashcard = fileScanner.nextLine().split("\\s");

                Flashcard existingFlashcard = findFlashcardByTerm(flashcard[0]);
                int errors;

                try {
                    errors = Integer.parseInt(flashcard[2]);
                } catch (NumberFormatException exception) {
                    errors = 0;
                }

                if (existingFlashcard != null) {
                    existingFlashcard.setDefinition(flashcard[1]);
                    existingFlashcard.setErrors(errors);
                    flashcardCount++;
                    continue;
                }
                flashcards.add(new Flashcard(flashcard[0], flashcard[1], errors));
                flashcardCount++;
            }
            outputMessage("%s cards have been loaded.\n\n", String.valueOf(flashcardCount));
            return flashcardCount;
        } catch (FileNotFoundException exception) {
            outputMessage("File not found.\n");
        }
        return flashcardCount;
    }

    public static void exportFlashcards() {
        outputMessage("File name:");
        String fileName = getUserInput();

        exportFlashcards(fileName);
    }

    private static int exportFlashcards(String fileName) {
        int flashcardCount = 0;

        try (PrintWriter printWriter = new PrintWriter(fileName)) {
            for (Flashcard flashcard : flashcards) {
                printWriter.printf("%s %s %s\n", flashcard.getTerm(), flashcard.getDefinition(), flashcard.getErrors());
                flashcardCount++;
            }
            outputMessage("%s cards have been saved.\n\n", String.valueOf(flashcards.size()));
            return flashcardCount;
        }
        catch (FileNotFoundException exception) {
            outputMessage("File not found.\n");
        }
        return flashcardCount;
    }

    public static void askFlashcards() {
        outputMessage("How many times to ask?");
        int number = Integer.parseInt(getUserInput());

        Random random = new Random();
        List<String> terms = getTerms();
        String term;

        for (int i = 0; i < number; i++) {
            term = terms.get(random.nextInt(terms.size()));
            examineFlashcard(term);
        }
    }

    public static void examineFlashcard(String term) {
        outputMessage("Print the definition of \"%s\":\n", term);
        String inputDefinition = getUserInput();
        String correctDefinition = null;
        Flashcard flashcardFoundByTerm = findFlashcardByTerm(term);

        if (flashcardFoundByTerm != null) {
            correctDefinition = flashcardFoundByTerm.getDefinition();
        }

        if (inputDefinition.equals(correctDefinition)) {
            outputMessage("Correct!\n");
        } else {
            if (flashcardWithDefinitionExists(inputDefinition)) {

                String termForInputDefinition = null;
                Flashcard flashcardFoundByDefinition = findFlashcardByDefinition(inputDefinition);

                if (flashcardFoundByDefinition != null) {
                    termForInputDefinition = flashcardFoundByDefinition.getTerm();

                }
                outputMessage("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".\n\n",
                        correctDefinition, termForInputDefinition);
            } else {
                outputMessage("Wrong. The right answer is \"%s\".\n\n", correctDefinition);
            }

            if (flashcardFoundByTerm != null) {
                flashcardFoundByTerm.setErrors(flashcardFoundByTerm.getErrors() + 1);
            }
        }
    }

    public static void getHardestFlashcards() {
        List<String> flashcardTermsWithMaxErrors = findFlashcardTermsWithMaxErrors();
        int flashcardsCountWithMaxErrors = flashcardTermsWithMaxErrors.size();

        if (flashcardsCountWithMaxErrors == 0) {
            outputMessage("There are no cards with errors.\n");
        } else {
            Flashcard flashcard = findFlashcardByTerm(flashcardTermsWithMaxErrors.get(0));
            String errors = null;
            String term = null;

            if (flashcard != null) {
                term = flashcard.getTerm();
                errors = String.valueOf(flashcard.getErrors());
            }

            if (flashcardsCountWithMaxErrors == 1) {
                outputMessage("The hardest card is \"%s\". You have %s errors answering it.\n\n", term, errors);
            } else {
                outputMessage("The hardest cards are %s. You have %s errors answering them.\n\n",
                        getFormattedTerms(flashcardTermsWithMaxErrors), errors);
            }
        }
    }

    private static String getFormattedTerms(List<String> terms) {
        StringBuilder formattedTerms = new StringBuilder();

        for (String term : terms) {
            formattedTerms.append(String.format("\"%s\", ", term));
        }
        formattedTerms.delete(formattedTerms.length() - 2, formattedTerms.length());
        return formattedTerms.toString();
    }

    public static void resetStatistics() {
        for (Flashcard flashcard : flashcards) {
            flashcard.setErrors(0);
        }
        outputMessage("Card statistics have been reset.\n");
    }

    private static List<String> findFlashcardTermsWithMaxErrors() {
        List<String> flashcardTermsWithMaxErrors = new ArrayList<>();
        int maxErrors = 0;
        int tempErrors;

        for (Flashcard flashcard : flashcards) {
            tempErrors = flashcard.getErrors();

            if (tempErrors > 0 && tempErrors == maxErrors) {
                flashcardTermsWithMaxErrors.add(flashcard.getTerm());
            } else if (tempErrors > maxErrors) {
                maxErrors = tempErrors;
                flashcardTermsWithMaxErrors.clear();
                flashcardTermsWithMaxErrors.add(flashcard.getTerm());
            }
        }
        return flashcardTermsWithMaxErrors;
    }

    private static boolean flashcardWithTermExists(String term) {
        for (Flashcard flashcard : flashcards) {
            if (term.equals(flashcard.getTerm())) {
                return true;
            }
        }
        return false;
    }

    private static boolean flashcardWithDefinitionExists(String definition) {
        for (Flashcard flashcard : flashcards) {
            if (definition.equals(flashcard.getDefinition())) {
                return true;
            }
        }
        return false;
    }

    private static void removeFlashcardWithTerm(String term) {
        flashcards.removeIf(flashcard -> term.equals(flashcard.getTerm()));
    }

    private static List<String> getTerms() {
        return flashcards.stream().map(Flashcard::getTerm).collect(toList());
    }

    private static Flashcard findFlashcardByTerm(String term) {
        for (Flashcard flashcard : flashcards) {
            if (term.equals(flashcard.getTerm())) {
                return flashcard;
            }
        }
        return null;
    }

    private static Flashcard findFlashcardByDefinition(String definition) {
        for (Flashcard flashcard : flashcards) {
            if (definition.equals(flashcard.getDefinition())) {
                return flashcard;
            }
        }
        return null;
    }

    public static void initializeData() {
        if (initialFlashcardsFilePath != null) {
            int loadedFlashcardCount = importFlashcards(initialFlashcardsFilePath);
            outputMessage("%s cards have been loaded.\n\n", String.valueOf(loadedFlashcardCount));
        }
    }

    public static void saveFlashcards() {
        if (flashcardsSaveFilePath != null) {
            int savedFlashcardCount = exportFlashcards(flashcardsSaveFilePath);
            outputMessage("%s cards have been saved.\n\n", String.valueOf(savedFlashcardCount));
        }
    }
}
