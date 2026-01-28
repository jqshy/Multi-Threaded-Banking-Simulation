Core Functionality
The system simulates two main bank accounts, JA-1 and JA-2, which are initialized with a zero balance. The simulation runs indefinitely, spawning a variety of specialized agents that perform distinct financial tasks:

Depositors (5 Threads): Randomly deposit amounts between $1 and $600 into accounts. They signal waiting withdrawal threads whenever new funds become available.

Withdrawal Agents (10 Threads): Attempt to withdraw amounts between $1 and $99. If an account has insufficient funds, these threads enter a blocked state until a deposit is made.

Transfer Agents (2 Threads): Move funds (up to $99) between accounts. They utilize a "try-lock" mechanism to attempt to acquire locks on both the source and destination accounts simultaneously; if both locks cannot be acquired within 10ms, the transaction is aborted to prevent deadlocks.

Audit Agents (2 Threads): Periodic audits are conducted by an Internal Auditor and a U.S. Treasury Department Auditor. These agents lock both accounts to calculate the total transaction volume since the last audit and report current balances.

Key Technical Features
Thread Synchronization: Uses ReentrantLock for fine-grained control over account access and Condition variables to coordinate "wait-and-notify" logic between depositors and withdrawers.

Deadlock Avoidance: Implements tryLock() with time limits in the TransferAgent and AuditAgent classes to ensure the simulation never freezes during complex multi-account operations.

Thread-Safe Logging: Features a FlaggedTransactionLogger that records high-value transactions—deposits over $450 and withdrawals over $90—into a transactions.csv file for audit purposes.

Atomic Operations: Utilizes AtomicLong to maintain a globally synchronized transaction counter across all 19 concurrent threads.

File Structure
CNT4714Project2.java: The entry point that configures the simulation and manages the thread pool.

BankAccount.java: The shared resource managing balances and locks.

Agent.java / AuditAgent.java: Abstract base classes providing the framework for thread execution and auditing logic.

FlaggedTransactionLogger.java: Handles persistent, thread-safe storage of flagged activities to a CSV file.
