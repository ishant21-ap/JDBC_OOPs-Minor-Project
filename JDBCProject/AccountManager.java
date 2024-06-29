package JDBCProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

    private Connection connection;
    private Scanner sc;

    AccountManager(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    public void credit_money(long account_number) throws SQLException {
        System.out.println("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline
        System.out.println("Enter Security Pin: ");
        String security_pin = sc.nextLine();

        performTransaction(account_number, amount, security_pin, true);
    }

    public void debit_money(long account_number) throws SQLException {
        System.out.println("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline
        System.out.println("Enter Security Pin: ");
        String security_pin = sc.nextLine();

        performTransaction(account_number, amount, security_pin, false);
    }

    
    private void performTransaction(long account_number, double amount, String security_pin, boolean isCredit) throws SQLException {
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "select * from accounts where account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    if (!isCredit) {
                        double current_balance = resultSet.getDouble("balance");
                        if (amount > current_balance) {
                            System.out.println("Insufficient Balance!");
                            return;
                        }
                    }

                    String transaction_query = "update accounts set balance = balance " +
                            (isCredit ? "+" : "-") + " ? where account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(transaction_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs." + amount + (isCredit ? " credited " : " debited ") + "Successfully");
                        connection.commit();
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                    }
                } else {
                    System.out.println("Invalid Security Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }
    public void transfer_money(long sender_account_number) throws SQLException {
        sc.nextLine();
        System.out.print("Enter Receiver Bank Account Number: ");
        long receiver_account_number = sc.nextLong();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        try{
            connection.setAutoCommit(false);
            if(sender_account_number!=0 && receiver_account_number!=0){
                PreparedStatement preparedStatement = connection.prepareStatement("select * from accounts where account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount<=current_balance){

                        // Write debit and credit queries
                        String debit_query = "update accounts set balance = balance - ? where account_number = ?";
                        String credit_query = "update accounts set balance = balance + ? where account_number = ?";

                        // Debit and Credit prepared Statements
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        // Set Values for debit and credit prepared statements
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs."+amount+" Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.println("Insufficient Balance!");
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }else{
                System.out.println("Invalid account number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void getBalance(long account_number){
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = sc.nextLine();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("select balance from accounts where account_number = ? and security_pin = ?");
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: "+balance);
            }else{
                System.out.println("Invalid Pin!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}



