package JDBCProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_system","root", "password");
            Scanner sc = new Scanner(System.in);
            User user = new User(connection, sc);
            Accounts accounts = new Accounts(connection, sc);
            AccountManager accountManager = new AccountManager(connection, sc);

            String email;
            long account_number;

            while (true) {
                System.out.println("WELCOME TO ONLINE BANKING APPLICATION ");
//                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter your choice: ");
                int choice1 = sc.nextInt();
                sc.nextLine(); // consume newline
                switch (choice1) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if (email != null) {
                            System.out.println();
                            System.out.println("Logged in Successfully!");
                            if (!accounts.account_exists(email)) {
                                System.out.println();
                                System.out.println("Press 1 to Open a New Account");
                                System.out.println("Press 2 to Exit ");
                                if (sc.nextInt() == 1) {
                                    sc.nextLine(); // consume newline
                                    account_number = accounts.open_account(email);
                                    System.out.println("Account Created Successfully !");
                                    System.out.println("Your Account Number is : " + account_number);
                                } else {
                                    break;
                                }
                            }
                            account_number = accounts.getAccount_Number(email);
                            int choice2 = 0;
                            while (choice2 != 5) {
                                System.out.println();
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Log Out");
                                System.out.println("Enter your choice: ");
                                choice2 = sc.nextInt();
                                sc.nextLine(); // consume newline
                                switch (choice2) {
                                    case 1:
                                        accountManager.debit_money(account_number);
                                        break;
                                    case 2:
                                        accountManager.credit_money(account_number);
                                        break;
                                    case 3:
                                         accountManager.transfer_money(account_number);
                                        break;
                                    case 4:
                                         accountManager.getBalance(account_number);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println("Enter Valid Choice!");
                                        break;
                                }
                            }
                        } else {
                            System.out.println("Incorrect email or password!");
                        }
                        break;
                    case 3:
                        System.out.println("Thank You for Using APNA Online Banking Application !");
                        System.out.println("Exiting system......");
                        return;
                    default:
                        System.out.println("Please Enter a Valid Choice !");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
