import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

public class Withdrawal extends Agent {
    private static final int MIN_WITHDRAWAL = 1;
    private static final int MAX_WITHDRAWAL = 99;
    private final Condition sufficientFunds;

    public Withdrawal(String agentName, BankAccount[] accounts, AtomicLong transactionCounter, 
                      FlaggedTransactionLogger logger, int maxSleepTime, Condition sufficientFunds) {
        super(agentName, accounts, transactionCounter, logger, maxSleepTime);
        this.sufficientFunds = sufficientFunds;
    }

    @Override
    public void run() {
        while (true) {
            BankAccount account = selectRandomAccount();
            int amount = random.nextInt(MAX_WITHDRAWAL - MIN_WITHDRAWAL + 1) + MIN_WITHDRAWAL;
            
            account.getLock().lock();
            try {
                while (account.getBalance() < amount) {
                    String blockedOutput = String.format("%s attempts to withdraw $%d from %s (******) WITHDRAWAL BLOCKED\nINSUFFICIENT FUNDS!!! Balance only $%d", 
                        agentName, amount, account.getAccountId(), account.getBalance());
                    printOutput(blockedOutput);
                    sufficientFunds.await(); 
                }
                long currentTransNum = transactionCounter.incrementAndGet();
                int newBalance = account.withdraw(amount, agentName, logger, transactionCounter);

                String flagMessage = "";
                if (amount > 90) {
                    flagMessage = String.format("\n***Flagged Transaction *** %s Made A Withdrawal In Excess Of $90.00 USD - See Flagged Transaction Log.", agentName);
                }

                String output = String.format("%s withdraws $%d from %s\n(-) %s balance is $%d%s\n%d",
                    agentName, amount, account.getAccountId(), 
                    account.getAccountId(), newBalance, flagMessage, currentTransNum);
                
                printOutput(output);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                account.getLock().unlock();
            }
            pause();
        }
    }
}
