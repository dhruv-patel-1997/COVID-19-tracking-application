package com.company;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.*;
import java.util.*;

/*This is a class which represents government and their functionality*/
public class Government {
    Connection connection; // Connection object to connect to database
    Statement statement;
    public static final String DEFAULT_SQL = "DEFAULT "; // Default value for sql variable

    // Constructor for government
    Government(String configFile){
        // Establish connection to government database
        establishConnection(configFile);
    }

    // This method establishes connection to government database
    // @configFile String containing dbname,user,password
    private void establishConnection(String confiFile){
        //String dbname,user,password;
        InputStream inputStream;
        Properties pro=new Properties();
        // Load a connection library between Java and the database
        try {
            inputStream=new FileInputStream(confiFile+".properties");

            pro.load(inputStream);

            // Store dbname, user and password
            String dbname=pro.getProperty("dbname");
            String user=pro.getProperty("user");
            String password=pro.getProperty("password");

            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            // Connect to the Dal database
            connection = DriverManager.getConnection(dbname, user,password);
            // Create a statement
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

        } catch (Exception throwables) {
            System.out.println(throwables);
            //System.exit(1);
        }

    }

    // Returns 1 if contact is present; 0 otherwise
    private int checkIfExists(String initiator,String contact_name,String contact_date,String contact_duration){
        ResultSet resultSet_date;
        try {
            // SQL query to check if tuple is already present in database
            resultSet_date=statement.executeQuery("select * from CONTACT_LIST c where ((c.person1_key=\""+initiator+"\" and c.person2_key=\""+contact_name+"\") or (c.person1_key=\""+contact_name+"\" and c.person2_key=\""+initiator+"\")) and c.contact_date=\""+contact_date+"\";");

            //If condition specifying that resultSet contains one row; If it contains one row then we add the duration to existing duration and update this row value
            if(resultSet_date.isBeforeFirst() && resultSet_date.next() && resultSet_date.isFirst() && resultSet_date.isLast()){
                resultSet_date.updateInt("contact_duration", Integer.parseInt(contact_duration)+resultSet_date.getInt("contact_duration"));
                resultSet_date.updateRow();
                // Contact is present
                return 1;
            }
            else{
                // Contact does not exist between two individuals
                return 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // Return 0 to specify there is no such entry in database
        return 0;
    }

    // This method stores the contact information to government database.
    // @initiator SHA 256 hash specifying device name and address uniquely
    // @contactInfo xml formatted string containing contact information
    public boolean mobileContact(String initiator, String contactInfo){
        String contact_name,contact_date,contact_duration,query;
        // Map containing key= Primary key of table CONTACT_LIST i.e person1_key,person2_key and contact_date
        // AND value=0 or 1 i.e. 0 being person1 has contacted person2
        //                       1 being person2 has contacted person1
        Map<ArrayList<String>,Integer> contactMap= new HashMap<>();
        ResultSet resultSet_contact;

        try {
            // Deconstruct xml contactInfo string according to tag
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(contactInfo));

            Document doc = db.parse(is);
            // Get a list of node having initiator_info as tag
            NodeList initiator_nodes = doc.getElementsByTagName("initiator_info");
            // For loop to travserse through nodeList and check if any testHash is present if yes store to government database
            for (int i = 0; i < initiator_nodes.getLength(); i++) {
                Element element = (Element) initiator_nodes.item(i);
                Element line;

                NodeList title = element.getElementsByTagName("initiator_testHash");
                // For loop to traverse through all the testHash that individual has given test for
                for(int j=0;j<title.getLength();j++){
                    line=(Element) title.item(j);
                    String testKey=getCharacterDataFromElement(line);
                    // Check if this testHash has been synced by government; if yes it returns the date at which the test took place
                    String date=checkTestHash(testKey);
                    // If we have date then we go on to check if same testHash has already been reported earlier
                    if(date!=null){
                        // If the passed testHash is new; then we store to POSITIVE_COVID_LIST table
                        if(checkNewTestHash(testKey))
                            insertPositiveTestData(initiator,testKey,Integer.parseInt(date));
                        else  // If not then we dont store it in government database again.
                            System.out.println("The test hash " +testKey+ " is already reported.");
                    }
                    // If the date is null then government has not yet synchronised the testHash. Therefore, we will ignore this testHash and not store in database
                    else{
                        System.out.println("Date not yet synchronised for test ID : "+testKey);
                    }
                }
            }
            // Fetch all contacts and store in NodeList
            NodeList contact_nodes = doc.getElementsByTagName("contact");
            // For loop to traverse through nodeList to store in CONTACT_LIST table
            for (int i = 0; i < contact_nodes.getLength(); i++) {
                Element element = (Element) contact_nodes.item(i);

                // Deconstruct xml and fetch name,date and duration
                NodeList name = element.getElementsByTagName("contact_name");
                Element line = (Element) name.item(0);
                contact_name=getCharacterDataFromElement(line);

                NodeList title = element.getElementsByTagName("contact_date");
                line = (Element) title.item(0);
                contact_date=getCharacterDataFromElement(line);

                NodeList duration = element.getElementsByTagName("contact_duration");
                line = (Element) duration.item(0);
                contact_duration=getCharacterDataFromElement(line);

                // Check if contact already exists; This method will update row if it is already present adding the duration
                int checkExists=checkIfExists(initiator,contact_name,contact_date,contact_duration);
                // If contact is not present we insert to CONTACT_LIST table
                if(checkExists==0) {
                    // SQL Query to insert values in CONTACT_LIST table
                    query = "INSERT INTO CONTACT_LIST VALUES(\"" + initiator + "\",\"" + contact_name + "\"," + contact_date + "," + contact_duration + "," + DEFAULT_SQL + "," + DEFAULT_SQL + ");";
                    // Execute above query
                    try {
                        statement.execute(query);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
            // SQL query to know if there are any contacts which has been reported positive covid recently in 14 days between contact date and test date
            query="select * from CONTACT_LIST c inner join POSITIVE_COVID_LIST pcl on c.person2_key=pcl.person_key or c.person1_key=pcl.person_key where (c.person1_key=\""+initiator+"\" or c.person2_key=\""+initiator+"\") and pcl.person_key<>\""+initiator+"\" and (ABS(c.contact_date-pcl.test_date)<=14);";

            int count = 0; // Specifies the number of times the calling device has come in contact to COVID patient recently and havent been reported
            try {
                // Execute query
                resultSet_contact=statement.executeQuery(query);
                // Parse through resultSet containing multiple rows
                while(resultSet_contact.next()){
                    // Check if particular contact has not yet been reported and increment count
                    // Also check person1 contacted person2 and we are reporting this contact
                    if(resultSet_contact.getString("person1_key").equals(initiator) && !resultSet_contact.getBoolean("person1_contact_reported")){
                        ArrayList<String> al=new ArrayList<>();
                        al.add(resultSet_contact.getString("person1_key"));
                        al.add(resultSet_contact.getString("person2_key"));
                        al.add(resultSet_contact.getString("contact_date"));
                        // Insert in contactMap map
                        contactMap.put(al,0);
                        // Increment count to know contact has not been reported and needs to be reported
                        count++;
                    }
                    // Check if person2 contacted person1 and we are reporting this contact
                    else if(resultSet_contact.getString("person2_key").equals(initiator) && !resultSet_contact.getBoolean("person2_contact_reported")){
                        ArrayList<String> al=new ArrayList<>();
                        al.add(resultSet_contact.getString("person1_key"));
                        al.add(resultSet_contact.getString("person2_key"));
                        al.add(resultSet_contact.getString("contact_date"));
                        // Insert in contactMap map
                        contactMap.put(al,1);
                        // Increment count to know contact has not been reported and needs to be reported
                        count++;
                    }
                }
                // For loop traverse through contactMap and update which contact we are reporting
                for (Map.Entry<ArrayList<String>, Integer> arrayListIntegerEntry : contactMap.entrySet()) {
                    Map.Entry mapElement = arrayListIntegerEntry;
                    int t = (int) mapElement.getValue();
                    // ArrayList containing keys of contactMap
                    ArrayList<String> a = (ArrayList<String>) mapElement.getKey();
                    // SQL query to fetch particular row from table
                    query = "select * from CONTACT_LIST c where c.person1_key=\"" + a.get(0) + "\" and c.person2_key=\"" + a.get(1) + "\" and c.contact_date=\"" + a.get(2) + "\";";

                    ResultSet rs = statement.executeQuery(query);
                    //Travserse through resultSet rows to update contact reported or not
                    while (rs.next()) {
                        if (t == 0) {
                            // Update person1_contact_reported tp specify person1 contact has been reported with person2
                            rs.updateBoolean("person1_contact_reported", true);
                            rs.updateRow();
                        } else if (t == 1) {
                            // Update person2_contact_reported tp specify person2 contact has been reported with person1
                            rs.updateBoolean("person2_contact_reported", true);
                            rs.updateRow();
                        }
                    }
                }
                // Check the count of count to know if there has been positive contacts recently
                // Return true if count>0
                if(count>0){
                    return true;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // This method checks if @testKey is present in POSITIVE_COVID_LIST table. If yes, return false signifying that it is new testKey and true otherwise
    private boolean checkNewTestHash(String testKey){
        try {
            ResultSet resultSet_date=statement.executeQuery("select * from POSITIVE_COVID_LIST t where t.test_key=\""+testKey+"\";");
            // Returns true if testKey is new
            return !resultSet_date.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // Returns false if the testKey is not new one
        return false;
    }

    // This method if @testKey is present in TEST_RESULTS table returns date if present; null otherwise
    private String checkTestHash(String testKey){
        try {
            ResultSet resultSet_date=statement.executeQuery("select t.test_date from TEST_RESULTS t where t.test_list=\""+testKey+"\";");
            // Check if resultSet has rows
            if(resultSet_date.isBeforeFirst() && resultSet_date.next() && resultSet_date.isFirst() && resultSet_date.isLast()){
                return resultSet_date.getString("test_date");
            }
            // If there are no rows then return null
            else{
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    // This method inserts data to POSITIVE_COVID_LIST table
    // @personKey device hash value
    // @testKey COVID 19 test ID
    // @date number of days since January 01,2021
    private void insertPositiveTestData(String personKey,String testKey,int date){
        // SQL Query to insert values
        String query="INSERT INTO POSITIVE_COVID_LIST VALUES(\""+personKey+"\",\""+testKey+"\","+date+");";
        try {
            // Execute the SQL Query
            statement.execute(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // This method fetch string from Element
    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            // Return the data associated
            return cd.getData();
        }
        return "?";
    }

    // This method inserts data in TEST_RESULTS table
    // @testHash COVID 19 test key
    // @date number of days since January 01, 2021
    // @result Result of test; true if positive; false otherwise
    public void recordTestResult(String testHash, int date, boolean result) throws InvalidInputException{
        // Input validation; throws custom exception InvalidInputException
        if(testHash==null || testHash.isEmpty() || testHash.trim().isEmpty() || date<0)
            throw new InvalidInputException("Invalid Input passed either in test hash value or date for recordTestResult. Please check again");
        // Store only positive COVID tests
        if(result) {
            // SQL String to insert values to TEST_RESULTS table
            String query = "INSERT INTO TEST_RESULTS VALUES(\"" + testHash + "\"," + date + "," + result + ");";
            try {
                // Execute the query
                statement.execute(query);
            } catch (SQLException throwables) {
                System.out.println(throwables);
            }
        }
    }

    // This method finds common elements from list1 and list2 and return list containing common elements
        private <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>();
        // Traverse through list
        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    // This method returns number of large gatherings on particular date
    // @date number of days since January 01, 2021
    // @minSize the minSize required for a gathering
    // @minTime minTime required for individuals to be in contact with
    // @density Atleast the specified density for the gathering to be deemed as large
    public int findGatherings(int date, int minSize, int minTime, float density) throws InvalidInputException{
        // Input validation; throw custom made InvalidInputException
        if(density<0 || density>1 || date<0 || minSize<0 || minTime<0)
            throw new InvalidInputException("Invalid input passed for findGatherings. Please check again");
        ResultSet resultSet_contact,resultSet_contact3;
        Set<ArrayList<String>> contact_set=new HashSet<>(); // Set containing contact list of person1 and person2
        Set<ArrayList<String>> contact_TempSet=new HashSet<>(); // Set containing contact list of person1 and person2
        Map<String,ArrayList<String>> adjacencyMap=new HashMap(); // Adjacency Map containing list of person a person has come into contact with
        Set<String> person=new HashSet<>(); // Set to store individuals in a particular gathering
        int count,gatherCount=0; // gatherCount=The number of large gathering; count=Count of pair of individuals in gathering for atleast minTime

        try {
            // SQL Query to know contacts on @date
            resultSet_contact=statement.executeQuery("select c.person1_key,c.person2_key,c.contact_duration from CONTACT_LIST c where c.contact_date="+date+";");
            // Travserse through each rows
            while (resultSet_contact.next()){
                // Add to contact_set Set
                ArrayList<String> al=new ArrayList<>();
                al.add(resultSet_contact.getString("person1_key"));
                al.add(resultSet_contact.getString("person2_key"));
                contact_set.add(al);
            }

            // Traverse through shallow copy of contact_set
            for(ArrayList<String> it: new HashSet<>(contact_set)) {
                // If the existing list is not contained in contact_set or contact_set length is 0 removing elements then continue to next iteration of new object of contact_set
                if(contact_set.isEmpty() || !contact_set.contains(it))
                    continue;
                // Clear stored data at each iteration
                contact_TempSet.clear();
                person.clear();
                contact_TempSet.add(it);
                // Travserse through actual contact_set
                for(ArrayList<String> c:contact_set){
                    // Populate adjacencyMap
                    // Make new arraylist if key does not exist
                    if(!adjacencyMap.containsKey(c.get(0))){
                        adjacencyMap.put(c.get(0), new ArrayList<>());
                    }
                    // Fetch this list for key
                    ArrayList<String> contactList=adjacencyMap.get(c.get(0));
                    // Insert in contactList
                    contactList.add(c.get(1));

                    //Same as above but for person2
                    if(!adjacencyMap.containsKey(c.get(1))){
                        adjacencyMap.put(c.get(1), new ArrayList<>());
                    }
                    ArrayList<String> contactList1=adjacencyMap.get(c.get(1));
                    contactList1.add(c.get(0));
                }
                // This commonContact list will contain intersection of contact between two individual
                ArrayList<String> commonContact= (ArrayList<String>) intersection(adjacencyMap.get(it.get(0)),adjacencyMap.get(it.get(1)));
                // Iterate through each intersection
                for(String s:commonContact){
                    // Populate contact_TempSet
                    ArrayList<String> al1=new ArrayList<>();
                    al1.add(it.get(0));
                    al1.add(s);

                    ArrayList<String> al2=new ArrayList<>();
                    al2.add(it.get(1));
                    al2.add(s);

                    contact_TempSet.add(al1);
                    contact_TempSet.add(al2);
                }
                // Populate person Set to store unique individuals present in gatherings
                for(ArrayList<String> tmp:contact_TempSet){
                    person.add(tmp.get(0));
                    person.add(tmp.get(1));
                }

                count=0; // Count representing number of pairs of individuals in gathering which contacted for atleast @minTime minutes
                if(person.size()>=minSize){
                    // SQL Query to fetch rows having contact_duration for at least @minTime at @date date
                    resultSet_contact3=statement.executeQuery("select c.person1_key,c.person2_key,c.contact_duration from CONTACT_LIST c where c.contact_duration>="+minTime+" and c.contact_date="+date+";");
                    while(resultSet_contact3.next()){
                        // If this set is present in gathering then increment count
                        if(person.contains(resultSet_contact3.getString("person1_key")) && person.contains(resultSet_contact3.getString("person2_key")))
                            count++;
                    }
                    // Maximum pairs possible for gathering
                    int pair_max_size=person.size()* (person.size()-1)/2;
                    // Calculate density c/m
                    float res=(float)count/pair_max_size;
                    // Density should be more than @density
                    if(res>density){
                        //Increment gatherCount
                        gatherCount++;
                        // Delete tuples from contact_set which is already reported large and therefore not considering it again in next iterations
                        contact_set.removeIf(contact1 -> (person.contains(contact1.get(0)) && person.contains(contact1.get(1))));
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // return number of gatherings
        return gatherCount;
    }
}

// This class represents custom made exception InvalidInputException specifying invalid input
class InvalidInputException extends Exception {
    public InvalidInputException(String s) {
        super(s);
    }
}