// Quiz.java
// by Amalia Safer
// January 22, 2015
// A basic quiz outline
// Can be run with xml files (first constructor) or txt files (second constructor)
// See dogscats.xml and/or dogscats.txt for formatting examples

import java.util.*;
import java.io.*;

//these are for parsing xml files
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class Quiz {
    
    private int numOutcomes; // number of possible quiz outcomes
    private Outcome[] outcomes; // possible quiz outcomes
    private int numQuestions; // each quiz will have a specific number of questions
    private Question[] questions; // all of the questions in the quiz
    
    public Quiz(String filename) 
        throws FileNotFoundException {
        processInput(filename);
    }
    
    public Quiz(String filename, int numQs, int numOs) 
        throws FileNotFoundException {
        this.numQuestions = numQs;
        this.questions = new Question[numQs];
        this.numOutcomes = numOs;
        this.outcomes = new Outcome[numOs];
        
        processInput(filename);
        
    }
    
    private void processInput(String filename) 
        throws FileNotFoundException {
        
        //if file is .txt
        if (filename.matches("(.*)txt$")) {
            Scanner input = new Scanner(new File(filename));
            //fills all question arrays from file
            fillArrays(input);
        } else if (filename.matches("(.*)xml$")) {
            Document dom = parseXmlFile(filename);
            processDom(dom);
        }
    }
    
    private Document parseXmlFile(String filename){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(new File(filename));
            
            return dom;
            
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch(SAXException se) {
            se.printStackTrace();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
    
    private void processDom(Document dom) {
        //get the root element
        Element docEle = dom.getDocumentElement();
        
        NodeList nl = docEle.getElementsByTagName("Outcome");
        if(nl != null && nl.getLength() > 0) {
            outcomes = new Outcome[nl.getLength()];
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the question element
                Element ol = (Element)nl.item(i);
                
                //get the Question object
                Outcome o = getOutcome(ol);
                
                //add it to list
                outcomes[i] = o;
            }
        }
        
        //get a nodelist of questions
        nl = docEle.getElementsByTagName("Question");
        if(nl != null && nl.getLength() > 0) {
            questions = new Question[nl.getLength()];
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the question element
                Element ql = (Element)nl.item(i);
                
                //get the Question object
                Question q = getQuestion(ql);
                
                //add it to list
                questions[i] = q;
            }
        }
        
    }
    
    private Question getQuestion(Element ql) {
        String text = getTextValue(ql,"Text");
        Answer[] answers = null;
        
        //get a nodelist of questions
        NodeList nl = ql.getElementsByTagName("Answer");
        if(nl != null && nl.getLength() > 0) {
            answers = new Answer[nl.getLength()];
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the question element
                Element al = (Element)nl.item(i);
                
                //get the Question object
                Answer a = getAnswer(al);
                
                //add it to list
                answers[i] = a;
            }
        }
        
        Question q = new Question(0, text, answers);
        return q;
    }
    
    private Answer getAnswer(Element al) {
        String text = getTextValue(al,"Text");
        int[] values = new int[outcomes.length];
        
        NodeList nl = al.getElementsByTagName("Value");
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the value element
                Element vl = (Element)nl.item(i);
                
                int outcome = Integer.parseInt(vl.getAttribute("outcome"));
                int value = Integer.parseInt(vl.getFirstChild().getNodeValue());
                
                values[outcome] = value;
            }
        }
        
        Answer a = new Answer(text, values);
        return a;
    }
    
    private Outcome getOutcome(Element ol) {
        //for each <outcome> element get title and text
        String title = getTextValue(ol,"Title");
        String text = getTextValue(ol,"Text");
        
        //Create a new Outcome with the value read from the xml nodes
        Outcome o = new Outcome(title, text);
        
        return o;
    }
    
    
    /*
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is 'name' I will return John
     */
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        
        return textVal;
    }
    
    /*
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
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
    private Outcome importO(Scanner data) {
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
            outcomes[i] = importO(data); // imports all outcomes from file
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
        //Quiz animal = new Quiz("dogscats.txt", 5, 2);
        Quiz animal = new Quiz("dogscats.xml");
        animal.runQuiz();
    }
    
}