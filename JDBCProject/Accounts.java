package JDBCProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Accounts {

    private Connection connection;
    private Scanner sc;

    Accounts(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    public long open_account(String email) {
        if (!account_exists(email)) {
            sc.nextLine();
            String open_account_query = "insert into accounts(account_number, full_name, email, balance, security_pin) values(?, ?, ?, ?, ?)";
            System.out.println("Enter your full name : ");
            String full_name = sc.nextLine();
            System.out.println("Enter initial amount : ");
            double balance = sc.nextDouble();
            sc.nextLine(); // consume newline
            System.out.println("Enter your security pin : ");
            String security_pin = sc.nextLine();

            try {
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);

                int rowsaffected = preparedStatement.executeUpdate();
                if (rowsaffected > 0) {
                    return account_number;
                } else {
                    throw new RuntimeException("Account Creation Failed!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exists!");
    }

    public long getAccount_Number(String email) {
        String query = "select account_number from accounts where email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong("account_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account number doesn't exist!");
    }

    private long generateAccountNumber() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select account_number from accounts order by account_number desc limit 1");
            if (resultSet.next()) {
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number + 1;
            } else {
                return 10000100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean account_exists(String email) {
        String query = "select account_number from accounts where email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
