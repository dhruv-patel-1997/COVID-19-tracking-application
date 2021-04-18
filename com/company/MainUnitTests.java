package com.company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test suites")
public class MainUnitTests {

    // Valid input govt path
    static String govtPath="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\GovernmentConfig"; //Content : dbname=jdbc:mysql://db.cs.dal.ca:3306/dhruvp\nuser=dhruvp\npassword=B00868931

    // Invalid government path
    static String invalidGovtPath="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\GovernmentConf"; //Content : dbname=jdbc:mysql://db.cs.dal.ca:3306/dhruvp\nuser=dhruvp\npassword=B00868931
    // Invalid credentials as user and password in government config file
    static String invalidGovtCredentialsPath="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\InvalidGovernmentConfig"; //Content : dbname=jdbc:mysql://db.cs.dal.ca:3306/dhruvp\nuser=dhruv\npassword=B00868931
    // Invalid dbname in goverment config file
    static String invalidGovtDatabasePath="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\InvalidDbGovernmentDatabaseConfig"; //Content : dbname=jdbc:mysql://db.cs.dal.ca:3306/dhruvp\nuser=dhruvp\npassword=B00868931

    //Invalid mobile Device config Path
    static String invalidMobilePath="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileCon"; //address=127.0.0.1\ndeviceName=Dhruv1

    // Valid mobile paths
    static String mobPath1="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig1"; //address=127.0.0.1\ndeviceName=Dhruv1
    static String mobPath2="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig2"; //address=127.0.0.2\ndeviceName=Dhruv2
    static String mobPath3="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig3"; //address=127.0.0.3\ndeviceName=Dhruv3
    static String mobPath4="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig4"; //address=127.0.0.4\ndeviceName=Dhruv4

    private void clean(){
        Government gov=new Government(govtPath);

        try {
            gov.statement.execute("delete from TEST_RESULTS;");
            gov.statement.execute("delete from POSITIVE_COVID_LIST;");
            gov.statement.execute("delete from CONTACT_LIST;");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    @Nested
    @DisplayName("Constructor testing : Government and MobileDevice")
    class constructorTest {
        @Nested
        @DisplayName("Government Constructor Testing")
        class governmentTest {
            private Government govTest;

            @Test
            @DisplayName("Valid Government constructor")
            public void validConstructor() {
                Assertions.assertDoesNotThrow(() -> {
                    govTest = new Government(govtPath);
                });

            }
            @Test
            @DisplayName("Invalid Credentials passed in govtConfig File")
            public void invalidCredentialsTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                Government gov=new Government(invalidGovtCredentialsPath);
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened

                assertTrue(baos.toString().contains("SQLException"));
            }

            @Test
            @DisplayName("Invalid path passed in govtConfig File")
            public void invalidGovtPathTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                Government gov=new Government(invalidGovtPath);
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened

                assertTrue(baos.toString().contains("FileNotFoundException"));
            }

            @Test
            @DisplayName("Invalid Government Database path passed in govtConfig File")
            public void invalidDbConnectivityTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                Government gov=new Government(invalidGovtDatabasePath);
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened
                System.out.println("Here: " + baos.toString());

                assertTrue(baos.toString().contains("SQLSyntaxErrorException"));
            }
            @Test
            @DisplayName("Passing file path with .properties extension at the end")
            public void filePathWithExtensionTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to se your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                Government gov=new Government(govtPath+".properties");
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened

                assertTrue(baos.toString().contains("FileNotFoundException"));

            }
        }
        @Nested
        @DisplayName("Mobile Device Constructor Testing")
        class mobileDeviceTest {
            private Government gov=new Government(govtPath);
            private MobileDevice md;

            @Test
            @DisplayName("Valid mobileDevice constructor")
            public void validConstructor() {
                Assertions.assertDoesNotThrow(() -> {
                    md=new MobileDevice(mobPath1,gov);
                });
            }

            @Test
            @DisplayName("Invalid path passed in mobile Device config file")
            public void invalidCredentialsTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                md=new MobileDevice(invalidMobilePath,gov);
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened

                assertTrue(baos.toString().contains("FileNotFoundException"));
            }

            @Test
            @DisplayName("Passing null instead of government object")
            public void invalidGovtPathTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                md=new MobileDevice(mobPath1,null);
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened
                System.out.println("Here: " + baos.toString());

                Assertions.assertThrows(NullPointerException.class,() -> {
                    md.synchronizeData();
                });
            }

            @Test
            @DisplayName("Passing configFile with .properties extension")
            public void invalidDbConnectivityTest() {
                // Create a stream to hold the output
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                // Save the old System.out!
                PrintStream old = System.out;
                // Tell Java to use your special stream
                System.setOut(ps);
                // Print some output: goes to your special stream
                md=new MobileDevice(mobPath1+".properties",gov);
                // Put things back
                System.out.flush();
                System.setOut(old);
                // Show what happened
                assertTrue(baos.toString().contains("FileNotFoundException"));

            }
        }
    }

    @Nested
    @DisplayName("Ability to record contact")
    class RecordContact {
        @Nested
        @DisplayName("Input Validation")
        class Input {
            @Test
            @DisplayName("Null individual string")
            public void NullTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                Assertions.assertThrows(WrongInputException.class, () -> {
                    mb1.recordContact(null, 5, 5);
                });

            }

            @Test
            @DisplayName("Empty individual string")
            public void EmptyStringTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                Assertions.assertThrows(WrongInputException.class, () -> {
                    mb1.recordContact("", 5, 5);
                });
            }

            @Test
            @DisplayName("Invalid Date passed (Negative Date)")
            public void InvalidDateTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                Assertions.assertThrows(WrongInputException.class, () -> {
                    mb1.recordContact(mb2.getConfig(), -5, 5);
                });
            }

            @Test
            @DisplayName("Invalid Duration (Negative Duration)")
            public void InvalidDurationTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                Assertions.assertThrows(WrongInputException.class, () -> {
                    mb1.recordContact(mb2.getConfig(), 5, -5);
                });
            }
        }

        @Nested
        @DisplayName("Boundary cases")
        class Boundaries {
            @Test
            @DisplayName("Date=0")
            public void ZeroDateTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                NodeList list = mb1.dom.getElementsByTagName("contact");
                int beforeLength = list.getLength();

                try {
                    mb1.recordContact(mb2.getConfig(), 0, 50);
                } catch (WrongInputException e) {
                    System.out.println(e);
                }

                NodeList list2 = mb1.dom.getElementsByTagName("contact");
                int afterLength = list2.getLength();

                assertEquals(1, afterLength - beforeLength);
            }

            @Test
            @DisplayName("Pass only space string")
            public void SpaceStringTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                NodeList list = mb1.dom.getElementsByTagName("contact");
                int beforeLength = list.getLength();

                try {
                    mb1.recordContact("  ", 50, 50);
                } catch (WrongInputException e) {
                    System.out.println(e);
                }

                NodeList list2 = mb1.dom.getElementsByTagName("contact");
                int afterLength = list2.getLength();

                assertEquals(0, afterLength - beforeLength);
            }


            @Test
            @DisplayName("Duration=0")
            public void ZeroDurationTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                NodeList list = mb1.dom.getElementsByTagName("contact");
                int beforeLength = list.getLength();

                try {
                    mb1.recordContact(mb2.getConfig(), 50, 0);
                } catch (WrongInputException e) {
                    System.out.println(e);
                }

                NodeList list2 = mb1.dom.getElementsByTagName("contact");
                int afterLength = list2.getLength();

                assertEquals(1, afterLength - beforeLength);
            }
        }

        @Nested
        @DisplayName("Control flow")
        class CF {
            @Test
            @DisplayName("Normal operation")
            public void normalTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                NodeList list = mb1.dom.getElementsByTagName("contact");
                int beforeLength = list.getLength();

                try {
                    //Add 4 contacts
                    mb1.recordContact(mb2.getConfig(), 2, 50);
                    mb1.recordContact(mb2.getConfig(), 3, 50);
                    mb1.recordContact(mb2.getConfig(), 4, 50);
                    mb1.recordContact(mb2.getConfig(), 5, 50);
                } catch (WrongInputException e) {
                    System.out.println(e);
                }

                NodeList list2 = mb1.dom.getElementsByTagName("contact");
                int afterLength = list2.getLength();

                assertEquals(4, afterLength - beforeLength);
            }

            @Test
            @DisplayName("Duplicate contact. We will add the duration in this case and not duplicate it again")
            public void DuplicateContactTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                NodeList list = mb1.dom.getElementsByTagName("contact");
                int rows = 0;
                try {
                    clean();
                    //Add 4 contacts
                    mb1.recordContact(mb2.getConfig(), 2, 50);
                    mb1.recordContact(mb2.getConfig(), 3, 50);
                    mb1.recordContact(mb2.getConfig(), 3, 50); // In database this will store as one entry with contact 3 100
                    mb1.recordContact(mb2.getConfig(), 5, 50);
                    mb1.synchronizeData();

                    ResultSet rs = gov.statement.executeQuery("select * from CONTACT_LIST c where c.contact_date="+"3"+";");
                    rs.last();
                    rows = rs.getRow();
                } catch (Exception e) {
                    System.out.println(e);
                }
                assertEquals(1, rows);
            }

            @Test
            @DisplayName("Contact with itself")
            public void ContactWithItselfTest() {
                Government gov = new Government(govtPath);

                MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                Assertions.assertThrows(WrongInputException.class, () -> {
                    mb1.recordContact(mb1.getConfig(), 5, 5);
                });
            }
        }
    }

        @Nested
        @DisplayName("Records positive testHash to Mobile Device")
        class PositiveTest {
            @Nested
            @DisplayName("Input Validation")
            class Input {

                @Test
                @DisplayName("Null String in positiveTest")
                public void NullTestHashTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                    Assertions.assertThrows(WrongInputException.class, () -> {
                        mb1.positiveTest(null);
                    });
                }

                @Test
                @DisplayName("Empty String in positiveTest")
                public void EmptyTestHashStringTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                    Assertions.assertThrows(WrongInputException.class, () -> {
                        mb1.positiveTest("");
                    });
                }

                @Test
                @DisplayName("Only spaces string")
                public void SpaceStringTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);

                    Assertions.assertThrows(WrongInputException.class, () -> {
                        mb1.positiveTest("   ");
                    });
                }
            }

            @Nested
            @DisplayName("Boundary cases")
            class Boundaries {
                @Test
                @DisplayName("Store long string")
                public void LongStringTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    int length = 0;
                    try {
                        clean();
                        String testHash = "regrthiojdrthpuijsregophistioguhsroqIWNDKAFLNLSDGo" +
                                "idnrgfddddddrgwegterhrgedthdtyjfjfyujfyujaefaiouh" +
                                "gertoiuh34y78e5tjnkdfmgesrg8tn";
                        mb1.positiveTest(testHash);
                        gov.recordTestResult(testHash, 5, true);
                        mb1.synchronizeData();
                        ResultSet rs = gov.statement.executeQuery("select * from POSITIVE_COVID_LIST where test_key=\"" + testHash + "\";");
                        rs.last();
                        length = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    assertTrue(length > 0);
                }

                @Test
                @DisplayName("Only one letter/one digit string")
                public void SingleLengthStringTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    int length = 0;
                    try {
                        clean();
                        String testHash1 = "1";
                        String testHash2 = "A";
                        mb1.positiveTest(testHash1);
                        mb1.positiveTest(testHash2);
                        gov.recordTestResult(testHash1, 5, true);
                        gov.recordTestResult(testHash2, 5, true);
                        mb1.synchronizeData();
                        ResultSet rs = gov.statement.executeQuery("select * from POSITIVE_COVID_LIST where test_key=\"" + testHash1 + "\" or test_key=\""+testHash2+"\";");
                        rs.last();
                        length = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    assertTrue(length == 2);
                }
            }

            @Nested
            @DisplayName("Control flow")
            class CF {
                @Test
                @DisplayName("Normal operation")
                public void normalTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    int length = 0;
                    try {
                        clean();
                        String testHash = "Test1";
                        mb1.positiveTest(testHash);
                        gov.recordTestResult(testHash, 5, true);
                        mb1.synchronizeData();
                        ResultSet rs = gov.statement.executeQuery("select * from POSITIVE_COVID_LIST;");
                        rs.last();
                        length = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    assertTrue(length > 0);
                }

                @Test
                @DisplayName("Government does not have Test hash OR Test Hash does not match with govt test hash")
                public void GovtTestHashNotSyncTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    int length = 0;
                    boolean res=false;
                    try {
                        clean();
                        String testHash = "Test2";
                        mb1.positiveTest(testHash);
                        //gov.recordTestResult(testHash, 5, true);
                        res= mb1.synchronizeData(); // Console message of "Date not yet synchronised for test ID : Test1" will be displayed"
                        ResultSet rs = gov.statement.executeQuery("select * from POSITIVE_COVID_LIST where test_key=\"" + testHash + "\";");
                        rs.last();
                        length=rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Output: We ignore the passed testHash and not store in database
                    assertTrue(length==0);
                    assertEquals(false,res);
                }

                @Test
                @DisplayName("Pass duplicate TestHash")
                public void DuplicateTestHashTest() {
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    int length = 0;
                    boolean res=false;
                    try {
                        clean();
                        String testHash = "Test12";
                        mb1.positiveTest(testHash);
                        mb1.positiveTest(testHash);
                        mb1.positiveTest(testHash);
                        gov.recordTestResult(testHash, 5, true);
                        res= mb1.synchronizeData(); // Console message of "The test hash is already reported : Test1" will be displayed"
                        ResultSet rs = gov.statement.executeQuery("select * from POSITIVE_COVID_LIST where test_key=\"" + testHash + "\";");
                        rs.last();
                        length=rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Output : We only store one testHash and ignore all next duplicates
                    assertEquals(length, 1);
                    assertFalse(res);
                }
            }
        }

        @Nested
        @DisplayName("Store data to government database and inform if user has come into contact with someone who tested COVID-positive")
        class SynchronizeData {
            @Nested
            @DisplayName("Boundary cases")
            class Boundaries {
                @Test
                @DisplayName("Absolute Difference between contact date and test date is 0")
                public void ZeroDifferenceTest() {
                    clean();
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest3", 5, true);
                        mb2.positiveTest("CovidTest3");
                        mb2.synchronizeData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(mb1.synchronizeData());
                }

                @Test
                @DisplayName("Absolute Difference between contact date and test date is 14")
                public void MaxDifferenceTest() {
                    clean();
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest1", 19, true);
                        mb2.positiveTest("CovidTest1");
                        mb2.synchronizeData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(mb1.synchronizeData());
                }

                @Test
                @DisplayName("Absolute Difference between contact date and test date is more than 14")
                public void MoreThan14Test() {
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest", 20, true);
                        mb2.positiveTest("CovidTest");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertFalse(mb1.synchronizeData());
                }

                @Test
                @DisplayName("Absolute Difference between contact date and test date is less than 14")
                public void LessThan14DifferenceTest() {
                    clean();
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest2", 15, true);
                        mb2.positiveTest("CovidTest2");
                        mb2.synchronizeData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(mb1.synchronizeData());
                }
            }

            @Nested
            @DisplayName("Control flow")
            class CF {
                @Test
                @DisplayName("Multiple sync without inserting new data between subsequent sync")
                public void multipleSyncWithoutNewDataTest() {
                    clean();
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest", 15, true);
                        mb2.positiveTest("CovidTest");
                        mb2.synchronizeData();
                        assertTrue(mb1.synchronizeData());
                        assertFalse(mb1.synchronizeData());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Test
                @DisplayName("Multiple sync with inserting new data")
                public void multipleSyncWithNewDataTest() {
                    clean();

                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);
                    MobileDevice mb3 = new MobileDevice(mobPath3, gov);
                    int len = 0;
                    try {
                        clean();
                        mb1.recordContact(mb3.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest7", 15, true);
                        mb3.positiveTest("CovidTest7");
                        mb3.synchronizeData();
                        assertTrue(mb1.synchronizeData());
                        mb1.recordContact(mb2.getConfig(), 10, 10);
                        assertFalse(mb1.synchronizeData()); //Returns false but add new contact to database
                        //Check if new contact is present in db
                        ResultSet rs = gov.statement.executeQuery("select * from CONTACT_LIST c where (c.person1_key=\"" + mb1.getConfig() + "\" or c.person2_key=\"" + mb1.getConfig() + "\") and (c.person1_key=\"" + mb3.getConfig() + "\" or c.person2_key=\"" + mb3.getConfig() + "\");");
                        rs.last();
                        len = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(len > 0);
                }

                @Test
                @DisplayName("Change sync result from false to true by inserting positive contact")
                public void changeSyncWithNewDataTest() {
                    clean();

                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);
                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        assertFalse(mb1.synchronizeData());
                        gov.recordTestResult("CovidTest6", 15, true);
                        mb2.positiveTest("CovidTest6");
                        mb2.synchronizeData();
                        assertTrue(mb1.synchronizeData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Test
                @DisplayName("Call syncData() on the individual tested positive")
                public void callSyncOnPositiveTest() {
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);
                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest", 15, true);
                        mb1.positiveTest("CovidTest");
                        assertFalse(mb1.synchronizeData()); //Regardless of testing positive we return appropriate value if mb1 has been in contact with any covid patients
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Test
                @DisplayName("Call syncData() before recording contact or before any positive test")
                public void callSyncBeforAnyContactTest() {
                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);
                    try {
                        assertFalse(mb1.synchronizeData());
                        mb1.recordContact(mb2.getConfig(), 5, 50);
                        gov.recordTestResult("CovidTest", 15, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Nested
        @DisplayName("Store covid test result in database")
        class RecordTestResult {
            @Nested
            @DisplayName("Input Validation")
            class Input {
                @Test
                @DisplayName("Negative date in RecordTestResult")
                public void negativeDateTest() {
                    Government gov = new Government(govtPath);
                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.recordTestResult("Test2",-5,true);
                    });
                }
                @Test
                @DisplayName("Null string in RecordTestResult")
                public void nullStringTest() {
                    Government gov = new Government(govtPath);
                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.recordTestResult(null,5,true);
                    });
                }
                @Test
                @DisplayName("Empty string in RecordTestResult")
                public void emptyStringTest() {
                    Government gov = new Government(govtPath);
                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.recordTestResult("",5,true);
                    });
                }
                @Test
                @DisplayName("Only spaces string in RecordTestResult")
                public void spaceStringTest() {
                    Government gov = new Government(govtPath);
                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.recordTestResult("    ",5,true);
                    });
                }
            }
            @Nested
            @DisplayName("Boundary cases")
            class Boundaries {
                @Test
                @DisplayName("Pass long TestHash")
                public void longStringTest() {
                    Government gov = new Government(govtPath);
                    int length = 0;
                    try {
                        clean();
                        String testHash = "regrthiojdrthpuijsregophistioguhsroqIWNDKAFLNLSDGo" +
                                "idnrgfddddddrgwegterhrgedthdtyjfjfyujfyujaefaiouh" +
                                "gertoiuh34y78e5tjnkdfmgesrgtn";

                        gov.recordTestResult(testHash, 10, true);
                        ResultSet rs = gov.statement.executeQuery("Select * from TEST_RESULTS t where t.test_list=\"" + testHash + "\";");
                        rs.last();
                        length = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(length == 1);
                }

                @Test
                @DisplayName("Pass TestHash with length=1")
                public void singleLenthStringTest() {
                    Government gov = new Government(govtPath);
                    int length = 0;
                    try {
                        clean();
                        String testHash = "A";
                        gov.recordTestResult(testHash, 10, true);
                        ResultSet rs = gov.statement.executeQuery("Select * from TEST_RESULTS t where t.test_list=\"" + testHash + "\";");
                        rs.last();
                        length = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(length == 1);
                }

                @Test
                @DisplayName("Date=0")
                public void dateEqualsZeroTest() {
                    Government gov = new Government(govtPath);
                    int length = 0;
                    try {
                        clean();
                        String testHash = "Abcd";
                        gov.recordTestResult(testHash, 0, true);
                        ResultSet rs = gov.statement.executeQuery("Select * from TEST_RESULTS t where t.test_list=\"" + testHash + "\";");
                        rs.last();
                        length = rs.getRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assertTrue(length == 1);
                }
            }
            @Nested
            @DisplayName("Control flow")
            class CF {
                @Test
                @DisplayName("Pass duplicate testHash key again")
                public void duplicateTestHash() {
                    try {
                        Government gov = new Government(govtPath);

                        gov.recordTestResult("Test12345", 5, true);
                        gov.recordTestResult("Test12345", 5, true);

                    }
                    catch(Exception e){
                        Assertions.assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                        });
                    }
                }
            }

        }

        @Nested
        @DisplayName("Ability to find gatherings at particular date")
        class FindGatherings {
            @Nested
            @DisplayName("Input Validation")
            class Input {
                @Test
                @DisplayName("Negative date in findGatherings")
                public void NegativeDateTest() {
                    Government gov = new Government(govtPath);

                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.findGatherings(-5, 5, 5, 0.01f);
                    });
                }

                @Test
                @DisplayName("Negative Minimum Size in findGatherings")
                public void NegativeMinSizeTest() {
                    Government gov = new Government(govtPath);
                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.findGatherings(5, -5, 5, 0.01f);
                    });
                }

                @Test
                @DisplayName("Negative minTime in findGatherings")
                public void NegativeMinTimeTest() {
                    Government gov = new Government(govtPath);

                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.findGatherings(5, 5, -5, 0.01f);
                    });
                }

                @Test
                @DisplayName("Density not between 0 and 1 in findGatherings")
                public void InvalidDensityTest() {
                    Government gov = new Government(govtPath);

                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.findGatherings(5, 5, 5, 1.01f);
                    });
                }
                @Test
                @DisplayName("negative Density in findGatherings")
                public void negativeDensityTest() {
                    Government gov = new Government(govtPath);

                    Assertions.assertThrows(InvalidInputException.class, () -> {
                        gov.findGatherings(5, 5, 5, -1.01f);
                    });
                }
            }
            @Nested
            @DisplayName("Boundary cases")
            class Boundary {
                @Test
                @DisplayName("Date=0 in findGatherings")
                public void invalidDateTest() {
                    Government gov = new Government(govtPath);

                    try {
                        assertTrue(gov.findGatherings(0, 5, 5, 0.01f)>=0);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }
                @Test
                @DisplayName("minSize=0 in findGatherings")
                public void invalidMinSizeTest() {
                    Government gov = new Government(govtPath);

                    try {
                        assertTrue(gov.findGatherings(5, 0, 5, 0.01f)>=0);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }
                @Test
                @DisplayName("minTime=0 in findGatherings")
                public void invalidMinTimeTest() {
                    Government gov = new Government(govtPath);

                    try {
                        assertTrue(gov.findGatherings(5, 5, 0, 0.01f)>=0);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }
                @Test
                @DisplayName("density=0 in findGatherings")
                public void zeroDensityTest() {
                    Government gov = new Government(govtPath);

                    try {
                        assertTrue(gov.findGatherings(5, 5, 5, 0f)>=0);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }
                @Test
                @DisplayName("density=1 in findGatherings")
                public void oneDensityTest() {
                    Government gov = new Government(govtPath);

                    try {
                        assertTrue(gov.findGatherings(5, 5, 5, 1f)>=0);
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Nested
            @DisplayName("Control flow")
            class CF {
                @Test
                @DisplayName("Normal operation")
                public void normalOperationTest() {

                    Government gov = new Government(govtPath);
                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);

                    try {
                        mb1.recordContact(mb2.getConfig(), 100, 5);
                        mb1.synchronizeData();
                        assertEquals(1,gov.findGatherings(100, 1, 0, 0.001f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Test
                @DisplayName("Call findGatherings before recording any contact")
                public void callBeforeAnyTest() {
                    Government gov = new Government(govtPath);
                    try {
                        assertEquals(0,gov.findGatherings(5,5,5,0.01f));
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                }

                @Test
                @DisplayName("Change findGatherings value by recording new contact")
                public void changeGatheringValue() {
                    clean();
                    Government gov = new Government(govtPath);

                    MobileDevice mb1 = new MobileDevice(mobPath1, gov);
                    MobileDevice mb2 = new MobileDevice(mobPath2, gov);
                    MobileDevice mb3 = new MobileDevice(mobPath3, gov);
                    MobileDevice mb4 = new MobileDevice(mobPath4, gov);

                    mb1.synchronizeData();
                    try {
                        mb1.recordContact(mb2.getConfig(), 5, 5);
                        mb3.recordContact(mb4.getConfig(),5,5);
                        mb1.synchronizeData();
                        mb2.synchronizeData();
                        mb3.synchronizeData();
                        mb4.synchronizeData();
                        assertEquals(2,gov.findGatherings(5, 1, 2, 0.001f));

                        mb1.recordContact(mb3.getConfig(),5,3);
                        mb2.recordContact(mb3.getConfig(),5,3);
                        mb1.recordContact(mb4.getConfig(),5,3);
                        mb2.recordContact(mb4.getConfig(),5,3);
                        mb1.synchronizeData();
                        mb2.synchronizeData();
                        assertEquals(1,gov.findGatherings(5, 1, 2, 0.001f));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

