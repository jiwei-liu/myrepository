package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@192.168.172.10:1521:orcl";  
			String user = "hkc";
			String password = "smtserver";
			Connection con = DriverManager.getConnection(url, user, password);
			    
			System.out.println("123");
	} 

}
