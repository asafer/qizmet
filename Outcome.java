/*
 * Outcome.java
 * Written by Amalia Safer (asafer@bu.edu)
 * Represents a possible outcome from a quiz. Each outcome has a title, text, and a value.
 */ 

public class Outcome {
    
    public String title;
    public String text;
    public int value;
    
    public Outcome(String title, String text) {
        this.title = title;
        this.text = text;
        this.value = 0;
    }
    
}