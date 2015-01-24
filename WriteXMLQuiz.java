/*
 * WriteXMLQuiz.java
 * Created by Amalia Safer (asafer@bu.edu)
 * 
 * Not sure if/when this will be useful.
 * This object will create an xml file based on input.
 * At the moment, input is just into the constructor. It could
 * eventually become something that allows users to submit input,
 * hopefully with a GUI to go with.
 * 
 * The xml file created can then be run with Quiz.java
 */ 

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class WriteXMLQuiz {
    
    private String filename;
    private String quizname;
    private Question[] questions;
    private Outcome[] outcomes;
    
    public WriteXMLQuiz(String filename, String quizname, Question[] questions, Outcome[] outcomes) {
        this.filename = filename;
        this.quizname = quizname;
        this.questions = questions;
        this.outcomes = outcomes;
    }
    
    public void create() {
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(quizname);
            doc.appendChild(rootElement);
            
            // outcome elements
            for (int i = 0; i < outcomes.length; i++) {
                Element o = doc.createElement("Outcome");
                rootElement.appendChild(o);
                
                // outcome title elements
                Element title = doc.createElement("Title");
                title.appendChild(doc.createTextNode(outcomes[i].title));
                o.appendChild(title);
                
                // outcome text elements
                Element text = doc.createElement("Text");
                text.appendChild(doc.createTextNode(outcomes[i].text));
                o.appendChild(text);
            }
            
            // question elements
            for (int i = 0; i < questions.length; i++) {
                Element q = doc.createElement("Question");
                rootElement.appendChild(q);
                
                // question text elements
                Element text = doc.createElement("Text");
                text.appendChild(doc.createTextNode(questions[i].getQuestion()));
                q.appendChild(text);
                
                // answer elements
                for (int j = 0; j < questions[i].getNumAnswers(); j++) {
                    Element a = doc.createElement("Answer");
                    q.appendChild(a);
                    
                    // answer text elements
                    text = doc.createElement("Text");
                    text.appendChild(doc.createTextNode(questions[i].getAnswers()[j].getAns()));
                    a.appendChild(text);
                    
                    // answer value elements
                    for (int k = 0; k < outcomes.length; k++) {
                        int val = questions[i].getAnswers()[j].getVals()[k];
                        if (val != 0) {
                            Element v = doc.createElement("Value");
                            String strval = val + "";
                            v.appendChild(doc.createTextNode(strval));
                            a.appendChild(v);
                            String strk = k + "";
                            v.setAttribute("outcome", strk);
                        }
                    }
                }
            }
            

            
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));
            
            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            
            transformer.transform(source, result);
            
            System.out.println("File saved!");
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    // test file creation
    public static void main(String[] args) {
        
        //write all the info
        Outcome o1 = new Outcome("Outcome1", "This is the first outcome.");
        Outcome o2 = new Outcome("Outcome2", "This is the second outcome.");
        Outcome[] os = new Outcome[2];
        os[0] = o1;
        os[1] = o2;
        int[] vals1 = {13, -3};
        int[] vals2 = {0, 10};
        Answer a1 = new Answer("Yes", vals1);
        Answer a2 = new Answer("No", vals2);
        Answer[] as = new Answer[2];
        as[0] = a1;
        as[1] = a2;
        Question q = new Question(0, "Will you get outcome 1?", as);
        Question[] qs = new Question[1];
        qs[0] = q;
        
        // create WriteXMLQuiz object and run it to create xml file
        WriteXMLQuiz test = new WriteXMLQuiz("test.xml", "TestQuiz", qs, os);
        test.create();
    }
    
}