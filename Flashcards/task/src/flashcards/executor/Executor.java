package flashcards.executor;

import flashcards.service.MenuService;

public class Executor {

    public static void run(String[] args) {
        MenuService.startMenu(args);
    }
}
