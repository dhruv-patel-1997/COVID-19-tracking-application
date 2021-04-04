package com.company;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.*;
import java.util.*;

public class Government {
    Connection connection;
    Statement statement;
    public static final String DEFAULT_SQL = "DEFAULT ";

    Government(String configFile){
        String dbname,user,password;
        String[] config_data=configFile.split("\n");
        dbname=config_data[0].split("=")[1];
        user=config_data[1].split("=")[1];
        password=config_data[2].split("=")[1];

        // Load a connection library between Java and the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            System.out.println("Error connecting to jdbc");
        }

        // Connect to the Dal database
        try {
            connection = DriverManager.getConnection(dbname, user,password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Create a statement
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean mobileContact(String initiator, String contactInfo){
        String contact_name,contact_date,contact_duration,query;
        //System.out.println(initiator);
        //System.out.println(contactInfo);

        try {
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(contactInfo));

            Document doc = db.parse(is);
            NodeList initiator_nodes = doc.getElementsByTagName("initiator_info");

            for (int i = 0; i < initiator_nodes.getLength(); i++) {
                Element element = (Element) initiator_nodes.item(i);

                NodeList name = element.getElementsByTagName("initiator_name");
                Element line = (Element) name.item(0);
                //System.out.println("initiator_name: " + getCharacterDataFromElement(line));

                NodeList title = element.getElementsByTagName("initiator_testHash");

                if(title.getLength()!=0){
                    line = (Element) title.item(0);
                    System.out.println("initiator_testHash: " + getCharacterDataFromElement(line));
                    String date=checkTestHash(getCharacterDataFromElement(line));
                    if(date!=null){
                        insertPositiveTestData(initiator,getCharacterDataFromElement(line),Integer.parseInt(date));
                    }
                    else{
                        System.out.println("No such date");
                    }
                }
            }

            NodeList contact_nodes = doc.getElementsByTagName("contact");
            for (int i = 0; i < contact_nodes.getLength(); i++) {
                Element element = (Element) contact_nodes.item(i);

                NodeList name = element.getElementsByTagName("contact_name");
                Element line = (Element) name.item(0);
                contact_name=getCharacterDataFromElement(line);
                //System.out.println("contact_name: " + getCharacterDataFromElement(line));

                NodeList title = element.getElementsByTagName("contact_date");
                line = (Element) title.item(0);
                contact_date=getCharacterDataFromElement(line);
                //System.out.println("contact_date: " + getCharacterDataFromElement(line));

                NodeList duration = element.getElementsByTagName("contact_duration");
                line = (Element) duration.item(0);
                contact_duration=getCharacterDataFromElement(line);
                //System.out.println("contact_duration: " + getCharacterDataFromElement(line));

                query="INSERT INTO CONTACT_LIST VALUES(\""+initiator+"\",\""+contact_name+"\","+contact_date+","+contact_duration+","+"true"+","+DEFAULT_SQL+");";

                try {
                    statement.execute(query);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            query="select * from CONTACT_LIST c inner join POSITIVE_COVID_LIST pcl on c.person2_key=pcl.person_key where c.person1_key=\""+initiator+"\" and (DATEDIFF(CURDATE(),'2021-01-01')-c.contact_date)<=14 and (DATEDIFF(CURDATE(),'2021-01-01')-pcl.test_date)<=14;";

            try {
                ResultSet resultSet_contact=statement.executeQuery(query);
                if(resultSet_contact.next()){
                    return true;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
*/
        return false;
    }
    private String checkTestHash(String testKey){
        try {
            ResultSet resultSet_date=statement.executeQuery("select t.test_date from TEST_RESULTS t where t.test_list=\""+testKey+"\";");

            if(resultSet_date.isBeforeFirst() && resultSet_date.next() && resultSet_date.isFirst() && resultSet_date.isLast()){
                return resultSet_date.getString("test_date");
            }
            else{
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private void insertPositiveTestData(String personKey,String testKey,int date){

        String query="INSERT INTO POSITIVE_COVID_LIST VALUES(\""+personKey+"\",\""+testKey+"\","+date+");";
        try {
            statement.execute(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    public void recordTestResult(String testHash, int date, boolean result){
        String query="INSERT INTO TEST_RESULTS VALUES(\""+testHash+"\","+date+","+result+");";
        try {
            statement.execute(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public int findGatherings(int date, int minSize, int minTime, float density){
        Set<ArrayList<String>> contact_set=new HashSet<>();
        Set<ArrayList<String>> contact_TempSet=new HashSet<>();
        Set<String> person=new HashSet<>();
        int count,gatherCount=0;

        try {
            ResultSet resultSet_contact=statement.executeQuery("select c.person1_key,c.person2_key,c.contact_duration from CONTACT_LIST c where c.contact_date="+date+";");

            while (resultSet_contact.next()){
                ArrayList<String> al=new ArrayList<>();
                al.add(resultSet_contact.getString("person1_key"));
                al.add(resultSet_contact.getString("person2_key"));
                contact_set.add(al);
            }


            for(ArrayList<String> it:new HashSet<ArrayList<String>>(contact_set)) {
                //contact_TempSet.add(it);
                try {
                    ResultSet resultSet_contact1=statement.executeQuery("select c.person1_key,c.person2_key,c.contact_duration from CONTACT_LIST c where c.person1_key=\""+it.get(0)+"\" and c.contact_date="+date+";");
                    while(resultSet_contact1.next()){
                        ArrayList<String> al1=new ArrayList<>();
                        al1.add(resultSet_contact1.getString("person1_key"));
                        al1.add(resultSet_contact1.getString("person2_key"));
                        contact_TempSet.add(al1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                ResultSet resultSet_contact2=statement.executeQuery("select c.person1_key,c.person2_key,c.contact_duration from CONTACT_LIST c where c.person1_key=\""+it.get(1)+"\" and c.contact_date="+date+";");

                while(resultSet_contact2.next()){
                    ArrayList<String> al2=new ArrayList<>();
                    al2.add(resultSet_contact2.getString("person1_key"));
                    al2.add(resultSet_contact2.getString("person2_key"));
                    contact_TempSet.add(al2);
                }

                for(ArrayList<String> tmp:contact_TempSet){
                    person.add(tmp.get(0));
                    person.add(tmp.get(1));
                }

                count=0;
                if(person.size()>=minSize){
                    for(ArrayList<String> contact:contact_TempSet){
                        ResultSet resultSet_contact3=statement.executeQuery("select c.person1_key,c.person2_key,c.contact_duration from CONTACT_LIST c where c.person1_key=\""+contact.get(0)+"\" and c.person2_key=\""+contact.get(1)+"\" and c.contact_duration>="+minTime+" and c.contact_date="+date+";");
                        while(resultSet_contact3.next()){
                            count++;
                        }
                    }
                    int pair_max_size=person.size()* (person.size()-1)/2;
                    float res=(float)count/pair_max_size;
                    if(res>density){
                        gatherCount++;
                        for(ArrayList<String> contact1:contact_TempSet){
                            contact_set.removeIf(contactToDelete -> contact1.get(0).equals(contactToDelete.get(0)) && contact1.get(1).equals(contactToDelete.get(1)));
                        }
                    }
                }
                contact_TempSet.clear();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return gatherCount;
    }


}
