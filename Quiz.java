// Quiz.java
// by Amalia Safer
// January 22, 2015
// A basic quiz outline

import java.util.*;
import java.io.*;

public class Quiz {
    
    private String name;  // each quiz will have a name
    private int numOutcomes; // number of possible quiz outcomes
    private Outcome[] outcomes; // possible quiz outcomes
    private int numQuestions; // each quiz will have a specific number of questions
    private Question[] questions; // all of the questions in the quiz
    private String filename; // name of the file you're getting info from
    
    public Quiz(String filename, String name, int numQs, int numOs) {
        this.filename = filename;
        this.name = name;
        this.numQuestions = numQs;
        this.questions = new Question[numQs];
        this.numOutcomes = numOs;
        this.outcomes = new Outcome[numOs];
        
    }
    
    
    //gets one question from the file
    private Question importQ(Scanner qs) {
        int qNum = 0;
        int numAns = 0;
        if (qs.hasNextInt()) {
            qNum = qs.nextInt(); //imports number of question
        }
        if (qs.hasNextInt()) {
            numAns = qs.nextInt(); //imports number of answers
        }
        qs.nextLine();
        String qText = qs.nextLine(); //imports question text
        String[] ansText = new String[numAns];
        //int[] ansVals = new int[numAns];
        Answer[] answers = new Answer[numAns];
        for (int i = 0; i < numAns; i++) { //for each answer...
            //int numNums = qs.nextInt(); //imports number of vals for the answer
            int[] ansVal = new int[numOutcomes];
            for (int j = 0; j < numOutcomes; j++) {
                ansVal[j] = qs.nextInt(); //imports all vals for the answer
            }
            //ansVals[i] = qs.nextInt();
            ansText[i] = qs.nextLine(); //imports answer text
            answers[i] = new Answer(ansText[i], ansVal); //puts all answer info into answer array
        }
        Question q = new Question(qNum, qText, /*numAns, ansText, ansVals, */answers); 
        return q; //returns all information as a Question
    }
    
    //gets an outcome from the file
    private Outcome getOutcome(Scanner data) {
        //System.out.println("in getOutcome");
        String title = data.nextLine(); // gets the outcome name (eg. You're a cat)
        //System.out.println("outcome name = " + title);
        String text = data.nextLine(); // gets the outcome text
        //System.out.println("outcome text = " + text);
        Outcome o = new Outcome(title, text);
        return o; // Returns the outcome
    }
    
    //fills questions array and outcome array
    private void fillArrays(Scanner data) {
        //System.out.println("made it here");
        for (int i = 0; i < questions.length; i++) {
            questions[i] = importQ(data); // imports all questions from file
        }
        //System.out.println("finished importing questions");
        for (int i = 0; i < outcomes.length; i++) {
            data.nextLine(); // throws out the empty line between outcomes
            //System.out.println("threw out extra line");
            outcomes[i] = getOutcome(data); // imports all outcomes from file
        }
    }
    
    //asks the user a question, then increments points accordingly
    private void ask(Question q) {
        //int val = q.runQ();
        int[] vals = q.runQ();
        for (int i = 0; i < vals.length; i++) {
            this.outcomes[i].value += vals[i];
        }
    }
    
    // calculates and returns the outcome with the highest score
    private Outcome calcResult() {
        int best = 0;
        int bestVal = 0;
        for (int i = 0; i < this.outcomes.length; i++) {
            if (this.outcomes[i].value > best) {
                best = this.outcomes[i].value;
                bestVal = i;
            }
        }
        return this.outcomes[bestVal];
    }
    
    public void runQuiz() 
        throws FileNotFoundException {
        Scanner input = new Scanner(new File(filename));
        
        //fills all question arrays from file
        fillArrays(input);
        
        for (int i = 0; i < questions.length; i++) {
            System.out.print("Question " + (i+1) + "/" + (questions.length) + ": ");
            ask(questions[i]);
        }
        
        //prints result!
        Outcome result = calcResult();
        System.out.println(result.title); 
        System.out.println(result.text);
        //for debugging
        Scanner console = new Scanner(System.in);
        System.out.println("View Breakdown? (y/n)");
        String choice = console.next();
        if (choice.equals("y")) {
            for (int i = 0; i < outcomes.length; i++) {
                System.out.println(outcomes[i].title + ": " + outcomes[i].value + " points");
            }
        }
    }
    
    // How to use Quiz to make quizzes
    public static void main(String[] args) 
        throws FileNotFoundException {
        Quiz animal = new Quiz("dogscats.txt", "Cats or Dogs?", 5, 2);
        animal.runQuiz();
    }
    
}