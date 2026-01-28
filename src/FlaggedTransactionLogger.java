/*
Name: Joshua Ortega
Course: CNT 4714 Fall 2025
Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
Due Date: September 28, 2025 
*/

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class FlaggedTransactionLogger {
    private static final String CSV_FILE = "transactions.csv";
    private final ReentrantLock fileLock = new ReentrantLock();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    public FlaggedTransactionLogger() {
        fileLock.lock();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, false))) {
            writer.println("Transaction Number,Agent,Transaction Type,Amount,Account,Timestamp");
        } catch (IOException e) {
            System.err.println("Error initializing transaction log file: " + e.getMessage());
        } finally {
            fileLock.unlock();
        }
    }

    public void logTransaction(long transNum, String agentName, String type, int amount, String accountId) {
        fileLock.lock();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = String.format("%d,%s,%s,%d,%s,%s", 
                transNum, 
                agentName, 
                type, 
                amount, 
                accountId, 
                timestamp);
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to transaction log file: " + e.getMessage());
        } finally {
            fileLock.unlock();
        }
    }
}
