package JDBCProject;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class User {

	private Connection connection;
	private Scanner sc;
	
	User(Connection connection, Scanner sc){
		this.connection = connection;
		this.sc = sc;
	}
	
	public void register(){
		sc.nextLine();
		System.out.println("Enter Your Name : ");
		String full_name = sc.nextLine();
		System.out.println("Enter Your email-id : ");
		String email = sc.nextLine();
		System.out.println("Enter your password : ");
		String password = sc.nextLine();
		
		if(user_exists(email)) {
			System.out.println("User Already Exists for this email !!!! ");
			return;
		}
		String register_query = "insert into user(full_name, email, password) values(?, ?, ?)";	
		
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(register_query);
			preparedStatement.setString(1, full_name);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, password);
			int rowsaffected = preparedStatement.executeUpdate();
			if(rowsaffected > 0) {
				System.out.println("Congratulations, Registeration Successfull ! ");
			}else {
				System.out.println("Registeration Failed ! ");
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public String login() {
		sc.nextLine();
		System.out.println("Enter Your email-id : ");
		String email = sc.nextLine();
		System.out.println("Enter your password : ");
		String password = sc.nextLine();
		
		String login_query = "select * from user where email = ? and password = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(login_query);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
			return email;
			}
			else {
				return null;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean user_exists(String email) {
		String query = "select * from user where email = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				return true;
			}
			else {
				return false;
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
}
