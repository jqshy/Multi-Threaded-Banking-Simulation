/*
Name: Joshua Ortega
Course: CNT 4714 Fall 2025
Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
Due Date: September 28, 2025 
*/

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Agent implements Runnable {
    protected final String agentName;
    protected final BankAccount[] accounts;
    protected final AtomicLong transactionCounter;
    protected final FlaggedTransactionLogger logger;
    protected final Random random = new Random();
    protected final int maxSleepTime;
    private static final Object outputLock = new Object();

    public Agent(String agentName, BankAccount[] accounts, AtomicLong transactionCounter, 
                 FlaggedTransactionLogger logger, int maxSleepTime) {
        this.agentName = agentName;
        this.accounts = accounts;
        this.transactionCounter = transactionCounter;
        this.logger = logger;
        this.maxSleepTime = maxSleepTime;
    }

    protected void pause() {
        try {
            long sleepTime = random.nextInt(maxSleepTime) + 1;
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    protected void printOutput(String message) {
        synchronized (outputLock) {
            System.out.println(message);
        }
    }
    
    protected BankAccount selectRandomAccount() {
        return accounts[random.nextInt(accounts.length)];
    }
}
