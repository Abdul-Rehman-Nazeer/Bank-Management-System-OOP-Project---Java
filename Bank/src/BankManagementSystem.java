import java.util.ArrayList;
import java.util.Scanner;

// Abstract class for Account
abstract class Account {
    protected int accountNumber;
    protected String name;
    protected double balance;
    protected String accountType;

    public Account(int accountNumber, String name, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        this.accountType = accountType;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful. New balance: $" + balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public abstract boolean withdraw(double amount);

    public void display() {
        System.out.println("\nAccount Details:");
        System.out.println("Account Type: " + accountType);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Name: " + name);
        System.out.println("Balance: $" + balance);
    }
}

// Savings Account class
class SavingsAccount extends Account {
    private static final double MINIMUM_BALANCE = 500;
    private static final double INTEREST_RATE = 0.05;

    public SavingsAccount(int accountNumber, String name, double balance) {
        super(accountNumber, name, balance, "Savings");
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && (balance - amount) >= MINIMUM_BALANCE) {
            balance -= amount;
            System.out.println("Withdrawal successful. New balance: $" + balance);
            return true;
        }
        System.out.println("Withdrawal failed. Minimum balance requirement not met.");
        return false;
    }

    public void addInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Interest added: $" + interest);
    }
}

// Checking Account class
class CheckingAccount extends Account {
    private static final double OVERDRAFT_LIMIT = 1000;

    public CheckingAccount(int accountNumber, String name, double balance) {
        super(accountNumber, name, balance, "Checking");
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && (balance - amount) >= -OVERDRAFT_LIMIT) {
            balance -= amount;
            System.out.println("Withdrawal successful. New balance: $" + balance);
            return true;
        }
        System.out.println("Withdrawal failed. Overdraft limit exceeded.");
        return false;
    }
}

// Interface for bank operations
interface BankOperations {
    Account createAccount(String name, double initialDeposit, String accountType);
    boolean deleteAccount(int accNo);
    Account findAccount(int accNo);
    boolean deposit(int accNo, double amount);
    boolean withdraw(int accNo, double amount);
    void displayAccount(int accNo);
    void displayAllAccounts();
}

// Bank class implementing BankOperations interface
class Bank implements BankOperations {
    private ArrayList<Account> accounts = new ArrayList<>();
    private int nextAccountNumber = 1001;

    @Override
    public Account createAccount(String name, double initialDeposit, String accountType) {
        Account acc;
        if (accountType.equalsIgnoreCase("Savings")) {
            acc = new SavingsAccount(nextAccountNumber++, name, initialDeposit);
        } else {
            acc = new CheckingAccount(nextAccountNumber++, name, initialDeposit);
        }
        accounts.add(acc);
        return acc;
    }

    @Override
    public boolean deleteAccount(int accNo) {
        Account acc = findAccount(accNo);
        if (acc != null) {
            accounts.remove(acc);
            return true;
        }
        return false;
    }

    @Override
    public Account findAccount(int accNo) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber() == accNo) {
                return acc;
            }
        }
        return null;
    }

    @Override
    public boolean deposit(int accNo, double amount) {
        Account acc = findAccount(accNo);
        if (acc != null && amount > 0) {
            acc.deposit(amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean withdraw(int accNo, double amount) {
        Account acc = findAccount(accNo);
        if (acc != null) {
            return acc.withdraw(amount);
        }
        return false;
    }

    @Override
    public void displayAccount(int accNo) {
        Account acc = findAccount(accNo);
        if (acc != null) {
            acc.display();
        } else {
            System.out.println("Account not found.");
        }
    }

    @Override
    public void displayAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts to display.");
        } else {
            for (Account acc : accounts) {
                acc.display();
                System.out.println("----------------------");
            }
        }
    }

    public void addInterestToSavings() {
        for (Account acc : accounts) {
            if (acc instanceof SavingsAccount) {
                ((SavingsAccount) acc).addInterest();
            }
        }
    }
}

// UI Handler class to separate UI logic from business logic
class UIHandler {
    private Scanner scanner;
    private Bank bank;

    public UIHandler() {
        this.scanner = new Scanner(System.in);
        this.bank = new Bank();
    }

    public void start() {
        int choice;
        do {
            displayMenu();
            choice = getValidChoice();
            processChoice(choice);
        } while (choice != 8);
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n--- Bank Management System ---");
        System.out.println("1. Create Savings Account");
        System.out.println("2. Create Checking Account");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Delete Account");
        System.out.println("6. View Account Details");
        System.out.println("7. View All Accounts");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getValidChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return choice;
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
            case 2:
                createAccount(choice == 1 ? "Savings" : "Checking");
                break;
            case 3:
                performDeposit();
                break;
            case 4:
                performWithdrawal();
                break;
            case 5:
                deleteAccount();
                break;
            case 6:
                viewAccount();
                break;
            case 7:
                bank.displayAllAccounts();
                break;
            case 8:
                System.out.println("Exiting. Thank you!");
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private void createAccount(String accountType) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial deposit: ");
        double deposit = scanner.nextDouble();
        Account acc = bank.createAccount(name, deposit, accountType);
        System.out.println(accountType + " Account created. Account Number: " + acc.getAccountNumber());
    }

    private void performDeposit() {
        System.out.print("Enter account number: ");
        int accNo = scanner.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        if (bank.deposit(accNo, amount)) {
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Deposit failed. Check account number or amount.");
        }
    }

    private void performWithdrawal() {
        System.out.print("Enter account number: ");
        int accNo = scanner.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        if (bank.withdraw(accNo, amount)) {
            System.out.println("Withdrawal successful.");
        } else {
            System.out.println("Withdrawal failed. Check account number or balance.");
        }
    }

    private void deleteAccount() {
        System.out.print("Enter account number to delete: ");
        int accNo = scanner.nextInt();
        if (bank.deleteAccount(accNo)) {
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("Account not found.");
        }
    }

    private void viewAccount() {
        System.out.print("Enter account number: ");
        int accNo = scanner.nextInt();
        bank.displayAccount(accNo);
    }
}

public class BankManagementSystem {
    public static void main(String[] args) {
        UIHandler ui = new UIHandler();
        ui.start();
    }
}