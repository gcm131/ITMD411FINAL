package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE IF NOT EXISTS "
				+ "G_CeMe_tickets("
				+ "ticket_id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "ticket_issuer VARCHAR(30), "
				+ "ticket_description VARCHAR(200), "
				+ "start_date TIMESTAMP, "
				+ "end_date TIMESTAMP)";
		final String createUsersTable = "CREATE TABLE IF NOT EXISTS "
				+ "G_CeMe_users("
				+ "uid INT AUTO_INCREMENT PRIMARY KEY, "
				+ "uname VARCHAR(30), "
				+ "upass VARCHAR(30), "
				+ "admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into g_ceme_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into g_ceme_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
					+ ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {
	    ResultSet results = null;
	    try {
	        Connection conn = getConnection(); // always get a fresh connection
	        Statement stmt = conn.createStatement();
	        results = stmt.executeQuery("SELECT ticket_id, ticket_issuer, ticket_description, " +
	                                    "IFNULL(start_date, 'N/A') AS start_date, " +
	                                    "IFNULL(end_date, 'N/A') AS end_date " +
	                                    "FROM g_ceme_tickets");
	    } catch (SQLException e1) {
	        e1.printStackTrace();
	    }
	    return results;
	}

	public void updateRecord(int ticketId, String newDesc) {
	    try {
	        statement = getConnection().createStatement();
	        String sql = "UPDATE g_ceme_tickets SET ticket_description='" + newDesc + 
	                     "' WHERE ticket_id=" + ticketId;
	        statement.executeUpdate(sql);
	        System.out.println("Record updated successfully.");
	        statement.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	public void deleteRecord(int ticketId) {
	    try {
	        statement = getConnection().createStatement();
	        String sql = "DELETE FROM g_ceme_tickets WHERE ticket_id=" + ticketId;
	        statement.executeUpdate(sql);
	        System.out.println("Record deleted successfully.");
	        statement.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	public void closeTicket(int ticketId) {
	    try {
	        statement = getConnection().createStatement();
	        String sql = "UPDATE g_ceme_tickets SET end_date = CURRENT_TIMESTAMP " +
	                     "WHERE ticket_id = " + ticketId;
	        statement.executeUpdate(sql);
	        statement.close();
	        System.out.println("Ticket closed successfully.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


}
