/*
 * Question.java
 * Written by Amalia Safer (asafer@bu.edu)
 * This program represents one question used in the sorting hat quiz.
 */ 

import java.io.*;
import java.util.*;

public class Question {
    private int qNum;   //Number of question (ex 1st q, 2nd q...or 7th q)
    private String text; //Text of the question
    //private int numAnswers; //Number of possible answers to the question
    private Answer[] answers; //array of all possible answers
    
    public Question(int qNum, String text, /*int numAnswers, String[] answers, int[] ansVals, */Answer[] answers) {
        this.qNum = qNum;
        this.text = text;
        //this.numAnswers = numAnswers;
        //this.answers = new Answer[numAnswers];
        //for (int i = 0; i < numAnswers; i++) {
        //    this.answers[i] = new Answer(answers[i], ansVals[i]);
        //}
        this.answers = answers;
    }
        
    //returns question text
    public String getQuestion() {
        return text;
    }
    
    //returns number of answers
    public int getNumAnswers() {
        return answers.length;
    }
    
    //returns number of the question
    public int getQNum() {
        return qNum;
    }
    
    //returns the array of answers
    public Answer[] getAnswers() {
        return answers;
    }
    
    //prints question, gets answer from user, returns values of 
    //the chosen answer
    public int[] runQ() {
        Scanner console = new Scanner(System.in);
        System.out.println(text);
        for (int i = 0; i < answers.length; i++) {
            System.out.println((i+1) + ": " + answers[i].getAns());
        }
        int ans = console.nextInt();
        while (ans > answers.length || ans < 1) {
            System.out.println("Not an option!");
            ans = console.nextInt();
        }
        //int ansVal = answers[ans].getVal();
        int[] ansVal = answers[ans-1].getVals();
        return ansVal;
    }
        
}