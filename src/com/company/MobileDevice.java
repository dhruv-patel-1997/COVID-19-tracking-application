package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.MessageDigest;

public class MobileDevice {
    Government gov;
    String config;
    DocumentBuilder builder;

    {
        try {
            builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    Document dom = builder.newDocument();

    MobileDevice(String configFile, Government contactTracer){
        this.config=configFile;
        this.gov=contactTracer;
        this.rootTags(configFile);
    }

    private void rootTags(String configFile){
        Element root=dom.createElement("covid_summary");
        dom.appendChild(root);

        Element initiator_info=dom.createElement("initiator_info");
        Element initiator_name=dom.createElement("initiator_name");
        //initiator_name.setTextContent(getSha256(configFile));
        initiator_name.setTextContent(configFile);
        initiator_info.appendChild(initiator_name);

        Element contact_list=dom.createElement("contact_list");

        root.appendChild(contact_list);
        root.appendChild(initiator_info);

    }

    private static String getSha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(value.getBytes());
            return bytesToHex(md.digest());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    public void recordContact(String individual, int date, int duration){
        NodeList contactListElements=dom.getElementsByTagName("contact_list");
        Element root=dom.getDocumentElement();
        if(contactListElements.getLength()==1){
            Element contact=dom.createElement("contact");
            Element contact_name=dom.createElement("contact_name");
            //contact_name.setTextContent(getSha256(individual));
            contact_name.setTextContent(individual);
            Element contact_date=dom.createElement("contact_date");
            contact_date.setTextContent(String.valueOf(date));
            Element contact_duration=dom.createElement("contact_duration");
            contact_duration.setTextContent(String.valueOf(duration));

            contact.appendChild(contact_name);
            contact.appendChild(contact_date);
            contact.appendChild(contact_duration);

            contactListElements.item(0).appendChild(contact);
            root.appendChild(contactListElements.item(0));
        }
    }

    //method to convert Document to String
    private String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public void positiveTest(String testHash){
        NodeList initiator_info=dom.getElementsByTagName("initiator_info");
        Element root=dom.getDocumentElement();
        if(initiator_info.getLength()==1){
            Element initiator_testHash=dom.createElement("initiator_testHash");
            initiator_testHash.setTextContent(testHash);

            initiator_info.item(0).appendChild(initiator_testHash);
            root.appendChild(initiator_info.item(0));
        }
    }
//Commented sha
    public boolean synchronizeData(){
        return this.gov.mobileContact(config,getStringFromDocument(dom));
    }
}
