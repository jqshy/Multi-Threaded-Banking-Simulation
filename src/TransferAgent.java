/*
Name: Joshua Ortega
Course: CNT 4714 Fall 2025
Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
Due Date: September 28, 2025 
*/

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class TransferAgent extends Agent {
    private static final int MIN_TRANSFER = 1;
    private static final int MAX_TRANSFER = 99;

    public TransferAgent(String agentName, BankAccount[] accounts, AtomicLong transactionCounter, 
                         FlaggedTransactionLogger logger, int maxSleepTime) {
        super(agentName, accounts, transactionCounter, logger, maxSleepTime);
    }

    @Override
    public void run() {
        while (true) {
            BankAccount source = selectRandomAccount();
            BankAccount destination = accounts[source == accounts[0] ? 1 : 0];

            int amount = random.nextInt(MAX_TRANSFER - MIN_TRANSFER + 1) + MIN_TRANSFER; 

            Lock lock1 = source.getLock();
            Lock lock2 = destination.getLock();

            boolean lock1Acquired = false;
            boolean lock2Acquired = false;

            try {
                lock1Acquired = lock1.tryLock(10, TimeUnit.MILLISECONDS);

                if (lock1Acquired) {
                    lock2Acquired = lock2.tryLock(10, TimeUnit.MILLISECONDS);

                    if (lock2Acquired) {
                        if (source.getBalance() < amount) {
                            String abortOutput = String.format("TRANSFER- %s attempts to transfer $%d from %s to %s. Balance only $%d (******) TRANSFER ABORTED INSUFFICIENT FUNDS!!!",
                                agentName, amount, source.getAccountId(), destination.getAccountId(), source.getBalance());
                            printOutput(abortOutput);
                        } else {
                            long currentTransNum = transactionCounter.incrementAndGet();
                            
                            source.withdraw(amount, agentName, logger, transactionCounter);
                            destination.deposit(amount, logger, transactionCounter, agentName);

                            String completeOutput = String.format("TRANSFER- %s transferring $%d from %s to %s\n%s balance is now $%d\nTRANSFER COMPLETE   Account %s balance now $%d\n%d",
                                agentName, amount, source.getAccountId(), destination.getAccountId(),
                                source.getAccountId(), source.getBalance(),
                                destination.getAccountId(), destination.getBalance(),
                                currentTransNum);
                            printOutput(completeOutput);
                        }
                    } else {
                        String abortOutput = String.format("TRANSFER- %s attempts to acquire both locks. Lock on %s acquired, but lock on %s FAILED. TRANSFER ABORTED.",
                            agentName, source.getAccountId(), destination.getAccountId());
                    }
                } else {
                    String abortOutput = String.format("TRANSFER- %s attempts to acquire both locks. Lock on %s FAILED. TRANSFER ABORTED.",
                        agentName, source.getAccountId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                if (lock2Acquired) {
                    lock2.unlock();
                }
                if (lock1Acquired) {
                    lock1.unlock();
                }
            }
            
            pause();
        }
    }
}
