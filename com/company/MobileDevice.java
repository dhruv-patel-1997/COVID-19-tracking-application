package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Properties;

/*This is a class which implements mobileDevice functionality representing each mobile device of different users*/
public class MobileDevice {
    Government gov; // Government object representing government class
    String config; // SHA-256 unique key for each unique device
    DocumentBuilder builder; // DocumentBuilder object


    // Block to initialise builder
    {
        try {
            // Initialise builder
            builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    Document dom = builder.newDocument(); // Document object to store xml data
    // Constructor to initialise class variables
    MobileDevice(String configFile, Government contactTracer) {
        this.config = configData(configFile); // SHA-256 key for unique device
        this.gov = contactTracer; // Assign government object
        this.rootTags(this.config); // Initialise xml tags
    }

    // This method will return SHA-256 hash value for the string value passed
    // @value String that needs to be hashed
    private static String getSha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(value.getBytes());
            return bytesToHex(md.digest());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // This method will return string corresponding to bytes passed
    // @bytes Array of bytes to be converted to string
    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    private static void removeChilds(Node node) {
        while (node.hasChildNodes())
            node.removeChild(node.getFirstChild());
    }

    // This method returns hashed value of device IP address and device name
    // @configFile String containing path to mobileConfig File which contains device address and name
    private String configData(String configFile) {
        Properties pro = new Properties();
        String concat = "";
        try {
            //load into properties to fetch address and name
            pro.load(new FileInputStream(configFile + ".properties"));
            Enumeration em = pro.keys();

            //Loop to fetch and combine device IP adderess+device name
            while (em.hasMoreElements()) {
                String str = (String) em.nextElement();
                concat = concat.concat((String) pro.get(str));
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return concat;
        //return getSha256(concat);
    }

    // This method sets starting tags inside xml file
    // @configFile String containing hash key of device
    private void rootTags(String configFile) {
        // Initialise with covid_summary tag
        Element root = dom.createElement("covid_summary");
        dom.appendChild(root);

        // Create more tags
        Element initiator_info = dom.createElement("initiator_info");
        Element initiator_name = dom.createElement("initiator_name");
        //initiator_name.setTextContent(getSha256(configFile));
        initiator_name.setTextContent(configFile); // Store hashkey in initiator_name tag
        initiator_info.appendChild(initiator_name);

        Element contact_list = dom.createElement("contact_list");

        // Nest tags inside root
        root.appendChild(contact_list);
        root.appendChild(initiator_info);
    }

    // Getter for config String class variable
    public String getConfig() {
        return this.config;
    }

    // This method records contact between two mobile device at given date @date and duration @duration
    public void recordContact(String individual, int date, int duration) throws WrongInputException {
        // Input validation : Throws Custom made WrongInputException if invalid input is passed
        // Assumption : We assume that date, duration are 0 or positive
        if (date < 0 || duration < 0 || individual == null || individual.isEmpty() || individual.trim().isEmpty() || this.getConfig().equals(individual))
            throw new WrongInputException("Invalid Contact Input. Please check again");
        // Get contact_list tag and append contacts inside this tag
        NodeList contactListElements = dom.getElementsByTagName("contact_list");
        Element root = dom.getDocumentElement();
        // There should only be one contact_list tag
        if (contactListElements.getLength() == 1) {
            // Store <contact>
            //          <contact_name> data </contact_name>
            //          <contact_date> data </contact_date>
            //          <contact_duration> data </contact_duration>
            //        </contact>

            Element contact = dom.createElement("contact");
            Element contact_name = dom.createElement("contact_name");
            //contact_name.setTextContent(getSha256(individual));
            contact_name.setTextContent(individual);
            Element contact_date = dom.createElement("contact_date");
            contact_date.setTextContent(String.valueOf(date));
            Element contact_duration = dom.createElement("contact_duration");
            contact_duration.setTextContent(String.valueOf(duration));

            contact.appendChild(contact_name);
            contact.appendChild(contact_date);
            contact.appendChild(contact_duration);

            contactListElements.item(0).appendChild(contact);
            root.appendChild(contactListElements.item(0));
        }
    }

    //method to convert Document to String
    private String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // This method stores positive testHash for particular individual
    // @testHash testHash key
    public void positiveTest(String testHash) throws WrongInputException {
        // Input validation : Throws custom made WrongInputException
        if (testHash == null || testHash.isEmpty() || testHash.trim().isEmpty())
            throw new WrongInputException("Invalid testHash value passed. Please check again");
        // Get initiator_info tag to store this testHash in it
        NodeList initiator_info = dom.getElementsByTagName("initiator_info");
        Element root = dom.getDocumentElement();
        if (initiator_info.getLength() == 1) {
            // Store <initiator_info>
            //          <initiator_testHash> @testHash </initiator_testHash>
            //        </initiator_info>
            Element initiator_testHash = dom.createElement("initiator_testHash");
            initiator_testHash.setTextContent(testHash);

            initiator_info.item(0).appendChild(initiator_testHash);
            root.appendChild(initiator_info.item(0));
        }

    }

    // This method is used to pass data to store in government database. The data accumulated in xml format will be used to transfer to government database
    // This method also returns if this individual has come into contact with any individual who test positive for COVID 19
    public boolean synchronizeData() {
        // Pass xml document object dom which contains data in xml format to mobileContact method of government
        boolean result = this.gov.mobileContact(this.config, getStringFromDocument(dom));
        // Remove data once it is stored in database so that it does not duplicate again
        removeChilds(dom.getElementsByTagName("contact_list").item(0));
        removeChilds(dom.getElementsByTagName("initiator_info").item(0));
        // Return true if there was contact between any individual tested positive; false otherwise
        return result;
    }

}

// Class representing custom made Exception which denotes that there has been wrong input passed in one of the methods
class WrongInputException extends Exception {
    public WrongInputException(String s) {
        super(s);
    }
}