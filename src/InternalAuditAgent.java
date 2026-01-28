import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class InternalAuditAgent extends AuditAgent {

    public InternalAuditAgent(String agentName, BankAccount[] accounts, AtomicLong transactionCounter, 
                              FlaggedTransactionLogger logger, int maxSleepTime) {
        super(agentName, accounts, transactionCounter, logger, maxSleepTime);
    }

    @Override
    protected void performAudit() {
        Lock lock1 = accounts[0].getLock();
        Lock lock2 = accounts[1].getLock();
        boolean lock1Acquired = false;
        boolean lock2Acquired = false;

        try {
            lock1Acquired = lock1.tryLock(10, TimeUnit.MILLISECONDS);
            if (lock1Acquired) {
                lock2Acquired = lock2.tryLock(10, TimeUnit.MILLISECONDS);

                if (lock2Acquired) {
                    long transactions = getTransactionsSinceLastAudit(transactionCounter.get());
                    
                    StringBuilder auditOutput = new StringBuilder();
                    auditOutput.append("\n********************\n");
                    auditOutput.append("Internal Bank Audit Beginning...\n");
                    auditOutput.append("The total number of transactions since the last Internal audit is: ").append(transactions).append("\n");
                    
                    for (BankAccount account : accounts) {
                        auditOutput.append(String.format("INTERNAL BANK AUDTIOR FINDS CURRENT ACCOUNT BALANCE FOR %s TO BE: $%d\n", 
                            account.getAccountId(), account.getBalance()));
                    }
                    
                    auditOutput.append("Internal Bank Audit Complete.\n");
                    auditOutput.append("********************\n");
                    
                    printOutput(auditOutput.toString());
                } else {
                    lock1.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock2Acquired) {
                lock2.unlock();
            }
            if (lock1Acquired && !lock2Acquired) {
                lock1.unlock();
            }
        }
    }
}
