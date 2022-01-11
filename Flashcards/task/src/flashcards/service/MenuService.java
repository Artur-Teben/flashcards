package flashcards.service;

import static flashcards.service.LoggerService.getUserInput;
import static flashcards.service.LoggerService.outputMessage;

public class MenuService {

    private static final String MENU_ACTIONS =
            "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";

    public static void startMenu(String[] args) {
        parseArgs(args);
        FlashcardService.initializeData();
        boolean inProgress = true;

        while (inProgress) {
            outputMessage(MENU_ACTIONS);
            String action = getUserInput();

            inProgress = processAction(action);
        }
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-import".equals(args[i])) {
                FlashcardService.initialFlashcardsFilePath = args[i + 1];
            } else if ("-export".equals(args[i])) {
                FlashcardService.flashcardsSaveFilePath = args[i + 1];
            }
        }
    }

    private static boolean processAction(String action) {
        switch (action) {
            case "add": {
                FlashcardService.addFlashcard();
                break;
            }
            case "remove": {
                FlashcardService.removeFlashcard();
                break;
            }
            case "import": {
                FlashcardService.importFlashcards();
                break;
            }
            case "export": {
                FlashcardService.exportFlashcards();
                break;
            }
            case "ask": {
                FlashcardService.askFlashcards();
                break;
            }
            case "exit": {
                System.out.println("Bye bye!");
                FlashcardService.saveFlashcards();
                return false;
            }
            case "log": {
                LoggerService.logConsole();
                break;
            }
            case "hardest card": {
                FlashcardService.getHardestFlashcards();
                break;
            }
            case "reset stats": {
                FlashcardService.resetStatistics();
                break;
            }
        }
        return true;
    }
}
