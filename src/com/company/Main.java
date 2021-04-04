package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Government gov=new Government("dbname=jdbc:mysql://db.cs.dal.ca:3306/dhruvp\nuser=dhruvp\npassword=B00868931");
        MobileDevice mb=new MobileDevice("address=127.0.0.1\ndeviceName=DhruvPatel",gov);
        MobileDevice mb1=new MobileDevice("address=127.0.1.2\ndeviceName=Kishan",gov);
        MobileDevice mb2=new MobileDevice("address=127.0.1.3\ndeviceName=Kishan2",gov);
        MobileDevice mb3=new MobileDevice("address=127.0.1.4\ndeviceName=Kishan3",gov);
        MobileDevice mb4=new MobileDevice("address=127.0.1.5\ndeviceName=Kishan4",gov);


        mb.recordContact("address=127.0.1.2\ndeviceName=Kishan",9,9);
        mb.recordContact("address=127.0.1.5\ndeviceName=Kishan4",10,9);
        mb.positiveTest("ABCDE");

        mb1.recordContact("address=127.0.0.1\ndeviceName=DhruvPatel",90,5);
        mb1.recordContact("address=127.0.1.4\ndeviceName=Kishan3",89,9);
        mb1.recordContact("address=127.0.1.5\ndeviceName=Kishan4",91,9);

        mb2.recordContact("address=127.0.0.1\ndeviceName=DhruvPatel",88,9);
        mb2.recordContact("address=127.0.1.2\ndeviceName=Kishan",89,9);
        mb2.recordContact("address=127.0.1.5\ndeviceName=Kishan4",86,9);

        mb3.recordContact("address=127.0.1.2\ndeviceName=Kishan",87,9);
        mb3.recordContact("address=127.0.1.3\ndeviceName=Kishan2",89,9);
        mb3.recordContact("address=127.0.1.5\ndeviceName=Kishan4",90,9);

        //mb4.recordContact("address=127.0.0.1\ndeviceName=DhruvPatel",4,9);
        mb4.recordContact("address=127.0.1.2\ndeviceName=Kishan",89,9);


        gov.recordTestResult("ABCDE",90,true);
        System.out.println(mb.synchronizeData());
        System.out.println(mb1.synchronizeData());
        System.out.println(mb2.synchronizeData());
        System.out.println(mb3.synchronizeData());
        System.out.println(mb4.synchronizeData());

        System.out.println(gov.findGatherings(89,1,2,-1f));

    }
}
