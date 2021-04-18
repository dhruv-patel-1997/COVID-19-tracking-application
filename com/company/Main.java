package com.company;

public class Main {

    public static void main(String[] args) {
        try {
            final String govPath="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\GovernmentConfig";

            // write your code here
            Government gov = new Government(govPath);

            // Each mobile device below has following patter:
            // mb1=> address=127.0.0.1  mb2=> address=127.0.0.2
            //       deviceName=Dhruv1        deviceName=Dhruv2 and so on....................
            final String mobPath1="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig1";
            final String mobPath2="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig2";
            final String mobPath3="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig3";
            final String mobPath4="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig4";
            final String mobPath5="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig5";
            final String mobPath6="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig6";
            final String mobPath7="D:\\CSCI 3901 Project\\Project\\untitled\\src\\Resources\\config\\MobileConfig7";


            MobileDevice mb1 = new MobileDevice(mobPath1, gov);
            MobileDevice mb2 = new MobileDevice(mobPath2, gov);
            MobileDevice mb3 = new MobileDevice(mobPath3, gov);
            MobileDevice mb4 = new MobileDevice(mobPath4, gov);
            MobileDevice mb5 = new MobileDevice(mobPath5, gov);
            MobileDevice mb6 = new MobileDevice(mobPath6, gov);
            MobileDevice mb7 = new MobileDevice(mobPath7, gov);

            mb1.recordContact(mb3.getConfig(), 50, 5);
            mb1.recordContact(mb5.getConfig(), 49, 9);
            mb1.positiveTest("mb1");
            System.out.println("MB1 tested positive");
            mb2.recordContact(mb4.getConfig(), 50, 9);
            mb2.recordContact(mb6.getConfig(), 49, 9);

            mb3.recordContact(mb4.getConfig(), 48, 9);
            mb3.recordContact(mb5.getConfig(), 44, 9);
            mb3.recordContact(mb6.getConfig(), 45, 9);
            mb3.recordContact(mb7.getConfig(), 50, 9);
            mb3.positiveTest("ABCDE");
            System.out.println("MB3 tested positive");
            mb4.recordContact(mb1.getConfig(), 50, 9);
            mb4.recordContact(mb7.getConfig(), 45, 9);

            mb5.recordContact(mb7.getConfig(), 45, 89);

            mb6.recordContact(mb2.getConfig(), 45, 9);

            mb7.recordContact(mb1.getConfig(), 50, 9);

            gov.recordTestResult("ABCDE", 50, true);


            //Multiple sync data
            System.out.println("MB1 sync result="+mb1.synchronizeData());
            System.out.println("MB1 sync result="+mb1.synchronizeData());
            System.out.println("Insert Contact to MB1");
            mb1.recordContact(mb6.getConfig(),50,50);
            mb1.recordContact(mb3.getConfig(), 50, 5); //Existing data add duration
            System.out.println("MB1 sync result="+mb1.synchronizeData());

            System.out.println("MB2 sync result="+mb2.synchronizeData());

            System.out.println("MB2 sync result="+mb2.synchronizeData());

            System.out.println("MB6 sync result="+mb6.synchronizeData());
            System.out.println("MB2 sync result="+mb2.synchronizeData());

            mb3.positiveTest("ABCDE");
            System.out.println("MB3 sync result="+ mb3.synchronizeData());
            mb3.positiveTest("ABCDE");
            System.out.println("MB3 sync result="+ mb3.synchronizeData());
            System.out.println("MB2 sync result="+ mb2.synchronizeData());
            System.out.println("Inserting MB2 contact with MB3");
            mb2.recordContact(mb3.getConfig(),45,50);
            System.out.println("MB2 sync result="+ mb2.synchronizeData());

            System.out.println("MB4 sync result="+ mb4.synchronizeData());
            System.out.println("MB5 sync result="+mb5.synchronizeData());
            System.out.println("MB6 sync result="+mb6.synchronizeData());
            System.out.println("MB7 sync result="+ mb7.synchronizeData());

            System.out.println("MB7 sync result="+ mb7.synchronizeData());
            gov.recordTestResult("AB", 52, true);
            mb1.positiveTest("AB");
            mb1.positiveTest("AB");
            mb1.positiveTest("AB");
            System.out.println("MB1 sync result="+ mb1.synchronizeData());
            mb1.positiveTest("AB");
            System.out.println("MB7 sync result="+ mb7.synchronizeData());
            System.out.println("MB7 sync result="+ mb7.synchronizeData());
            System.out.println("Inserting MB2 contact with MB3");
            mb2.recordContact(mb3.getConfig(),45,5);
            System.out.println("MB2 sync result="+ mb2.synchronizeData());
            System.out.println("MB2 sync result="+ mb2.synchronizeData());

            System.out.println("Find Gatherings at date 50\n"+gov.findGatherings(50, 0,0 , 0.00000001f));

            mb1.recordContact(mb2.getConfig(),50,3);
            mb3.recordContact(mb2.getConfig(),50,5);

            mb1.recordContact(mb4.getConfig(),50,4);
            mb3.recordContact(mb4.getConfig(),50,8);

            System.out.println("MB1 sync result="+ mb1.synchronizeData());

            System.out.println("MB1 sync result="+ mb3.synchronizeData());

            System.out.println("Find Gatherings at date 50 after inserting new contacts\n"+gov.findGatherings(50, 1, 2, 0.001f));

        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
