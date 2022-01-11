package flashcards.model;

public class Flashcard {
    private final String term;
    private String definition;
    private int errors;

    public Flashcard(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public Flashcard(String term, String definition, int errors) {
        this.term = term;
        this.definition = definition;
        this.errors = errors;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }
}
