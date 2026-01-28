import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final String accountId;
    private int balance;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition sufficientFunds = lock.newCondition();

    public BankAccount(String accountId, int initialBalance) {
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public String getAccountId() {
        return accountId;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public int getBalance() {
        return balance;
    }

    public int deposit(int amount, FlaggedTransactionLogger logger, AtomicLong transNum, String agentName) {
        lock.lock();
        try {
            balance += amount;
            if (amount > 450) {
                logger.logTransaction(transNum.get(), agentName, "deposit", amount, accountId);
            }
            sufficientFunds.signalAll();
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public int withdraw(int amount, String agentName, FlaggedTransactionLogger logger, AtomicLong transNum) throws InterruptedException {
        lock.lock();
        try {
            while (balance < amount) {
                return -1; 
            }
            balance -= amount;
            if (amount > 90) {
                logger.logTransaction(transNum.get(), agentName, "withdrawal", amount, accountId);
            }
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
