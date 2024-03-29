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
    
    
    // constructor for xml files
    public Quiz(String filename) 
        throws FileNotFoundException {
        processInput(filename);
    }
    
    // constructor for txt files
    public Quiz(String filename, int numQs, int numOs) 
        throws FileNotFoundException {
        this.numQuestions = numQs;
        this.questions = new Question[numQs];
        this.numOutcomes = numOs;
        this.outcomes = new Outcome[numOs];
        
        processInput(filename);
        
    }
    
    // checks if file is .txt or .xml and sends it to the appropriate parser
    private void processInput(String filename) 
        throws FileNotFoundException {
        
        //if file is .txt
        if (filename.matches("(.*)txt$")) {
            Scanner input = new Scanner(new File(filename));
            //fills all arrays from txt file
            fillArrays(input);
        } else if (filename.matches("(.*)xml$")) { //if file is xml
            Document dom = parseXmlFile(filename);
            //fills all arrays from xml file
            processDom(dom);
        }
    }
    
    /*
     * Code for parsing xml files begins here
     */ 
    
    // creates a document from xml file
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
    
    // processes the document from the xml file and uses it to fill arrays
    private void processDom(Document dom) {
        //get the root element (in this case should be title of quiz)
        Element docEle = dom.getDocumentElement();
        
        // gets all possible outcomes
        // note: gets outcomes first because it's necessary to know
        // number of outcomes when creating Answer objects
        NodeList nl = docEle.getElementsByTagName("Outcome");
        if(nl != null && nl.getLength() > 0) {
            outcomes = new Outcome[nl.getLength()];
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the outcome element
                Element ol = (Element)nl.item(i);
                
                //get the Outcome object
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
    
    // parses through xml inside question element to create Question object
    private Question getQuestion(Element ql) {
        String text = getTextValue(ql,"Text"); // question text
        Answer[] answers = null;
        
        //get a nodelist of answers
        NodeList nl = ql.getElementsByTagName("Answer");
        if(nl != null && nl.getLength() > 0) {
            answers = new Answer[nl.getLength()];
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the answer element
                Element al = (Element)nl.item(i);
                
                //get the Answer object
                Answer a = getAnswer(al);
                
                //add it to list of answers
                answers[i] = a;
            }
        }
        
        // currently have question numbers auto set to 0 because
        // they're stupid and irrelevant but it's a process to take them
        // out. Not a really long process, but I don't feel like it.
        Question q = new Question(0, text, answers);
        return q;
    }
    
    // parses through xml within answer element to create Answer object
    private Answer getAnswer(Element al) {
        String text = getTextValue(al,"Text"); // text of answer
        int[] values = new int[outcomes.length]; // should correspond to outcomes
        
        NodeList nl = al.getElementsByTagName("Value");
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
                
                //get the value element
                Element vl = (Element)nl.item(i);
                
                // finds the outcome the value corresponds to
                int outcome = Integer.parseInt(vl.getAttribute("outcome"));
                // finds the value we're giving that outcome for this answer
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
    
    /*
     * Code for parsing txt files begins here
     */ 
    
    //gets one question from the txt file
    private Question importQ(Scanner qs) {
        // question numbers are currently irrelevant and useless, but
        // they're in the txt files, so if you leave them out the program
        // will break. Is on the to do list for fixing.
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
    
    //gets an outcome from the txt file
    private Outcome importO(Scanner data) {
        //System.out.println("in getOutcome");
        String title = data.nextLine(); // gets the outcome name (eg. You're a cat)
        //System.out.println("outcome name = " + title);
        String text = data.nextLine(); // gets the outcome text
        //System.out.println("outcome text = " + text);
        Outcome o = new Outcome(title, text);
        return o; // Returns the outcome
    }
    
    //fills questions array and outcome array from txt file
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
    
    /*
     * Code for running the quiz begins here
     */ 
    
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
    
    // runs the quiz
    public void runQuiz() 
        throws FileNotFoundException {
        
        // asks all the questions
        for (int i = 0; i < questions.length; i++) {
            System.out.print("Question " + (i+1) + "/" + (questions.length) + ": ");
            ask(questions[i]);
        }
        
        // calculates and prints result!
        Outcome result = calcResult();
        System.out.println(result.title); 
        System.out.println(result.text);
        //for debugging (or curiosity)
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
        //Quiz test = new Quiz("test.xml");
        //test.runQuiz();
    }
    
}