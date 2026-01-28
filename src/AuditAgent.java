/*
Name: Joshua Ortega
Course: CNT 4714 Fall 2025
Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
Due Date: September 28, 2025 
*/

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public abstract class AuditAgent extends Agent {

    private long transactionsSinceLastAudit = 0; 
    
    public AuditAgent(String agentName, BankAccount[] accounts, AtomicLong transactionCounter, 
                      FlaggedTransactionLogger logger, int maxSleepTime) {
        super(agentName, accounts, transactionCounter, logger, maxSleepTime);
    }
   
    protected long getTransactionsSinceLastAudit(long currentTotalTrans) {
        long delta = currentTotalTrans - transactionsSinceLastAudit;
        transactionsSinceLastAudit = currentTotalTrans;
        return delta;
    }

    protected abstract void performAudit();

    @Override
    public void run() {
        while (true) {
            performAudit();
            pause();
        }
    }
}
