/*
Name: Joshua Ortega
Course: CNT 4714 Fall 2025
Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
Due Date: September 28, 2025 
*/

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

public class CNT4714Project2 {

    private static final BankAccount[] accounts = new BankAccount[2];
    private static final AtomicLong transactionCounter = new AtomicLong(0);
    private static FlaggedTransactionLogger logger;
    private static final int WT_MAX_SLEEP = 100;
    private static final int DT_MAX_SLEEP = 200;
    private static final int TR_MAX_SLEEP = 500;
    private static final int IA_MAX_SLEEP = 1500;
    private static final int TD_MAX_SLEEP = 3000; 

    public static void main(String[] args) {
        System.out.println("CNT 4714 Project 2 - Banking Simulator Running...");
        System.out.println("SIMULATION BEGINS...");
        
        accounts[0] = new BankAccount("JA-1", 0);
        accounts[1] = new BankAccount("JA-2", 0);
        
        Condition globalWithdrawalCondition = accounts[0].getLock().newCondition(); 
        
        logger = new FlaggedTransactionLogger();
        
        int numAgents = 19; 
        ExecutorService executor = Executors.newFixedThreadPool(numAgents);

        for (int i = 1; i <= 5; i++) {
            executor.execute(new Depositor("Agent DT" + i, accounts, transactionCounter, logger, DT_MAX_SLEEP));
        }
        for (int i = 1; i <= 10; i++) {
            executor.execute(new Withdrawal("Agent WT" + i, accounts, transactionCounter, logger, WT_MAX_SLEEP, accounts[0].getLock().newCondition())); // Passing a dummy condition here, the logic is in BankAccount.
        }
        for (int i = 1; i <= 2; i++) {
            executor.execute(new TransferAgent("Agent TR" + i, accounts, transactionCounter, logger, TR_MAX_SLEEP));
        }
        executor.execute(new InternalAuditAgent("Internal Auditor IA1", accounts, transactionCounter, logger, IA_MAX_SLEEP));
        
        executor.execute(new TreasuryDeptAuditAgent("Treasury Auditor TD1", accounts, transactionCounter, logger, TD_MAX_SLEEP));

        System.out.println("All agent threads are now executing indefinitely. Stop the program manually to end the simulation.");
    }
}
