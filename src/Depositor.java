import java.util.concurrent.atomic.AtomicLong;

public class Depositor extends Agent {
    private static final int MIN_DEPOSIT = 1;
    private static final int MAX_DEPOSIT = 600;

    public Depositor(String agentName, BankAccount[] accounts, AtomicLong transactionCounter, 
                     FlaggedTransactionLogger logger, int maxSleepTime) {
        super(agentName, accounts, transactionCounter, logger, maxSleepTime);
    }

    @Override
    public void run() {
        while (true) {
            BankAccount account = selectRandomAccount();
            int amount = random.nextInt(MAX_DEPOSIT - MIN_DEPOSIT + 1) + MIN_DEPOSIT;

            account.getLock().lock();
            try {
                long currentTransNum = transactionCounter.incrementAndGet();
                int newBalance = account.deposit(amount, logger, transactionCounter, agentName);
                
                String flagMessage = "";
                if (amount > 450) {
                    flagMessage = String.format("\n***Flagged Transaction *** %s Made A Deposit In Excess Of $450.00 USD See Flagged Transaction Log.", agentName);
                }

                String output = String.format("%s deposits $%d into: %s\n(+) %s balance is $%d%s\n%d",
                    agentName, amount, account.getAccountId(), 
                    account.getAccountId(), newBalance, flagMessage, currentTransNum);
                
                printOutput(output);
            } finally {
                account.getLock().unlock();
            }
            pause();
        }
    }
}
