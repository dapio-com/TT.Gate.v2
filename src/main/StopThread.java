package main;

import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class StopThread extends Thread {

    //private static final Logger log = Logger.getLogger(StopThread.class);
    private final Logger log = Logger.getLogger(StopThread.class);

    //private boolean WORKING;
    private ExecutorService ES;

    public StopThread(ExecutorService es) {
        this.ES = es;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("StopThread");
        Scanner scanner = new Scanner(System.in);
        String command = "";

        while (!command.equals("stop")) {

            command = scanner.nextLine();

            if (command.equals("stop")) {
                TTGate.WORKING = false;
                ES.shutdown();
                log.info("SHUTDOWN INITIATED BY OPERATOR");
                System.out.println("SHUTDOWN INITIATED BY OPERATOR");
            }
        }


    }


}
