import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

	public static String host = "localhost";
	public static String port = "3306";
	public static String database = "teamchat";
	public static String username = "root";
	public static String password = "leon1383";
    public static String driver = "com.mysql.cj.jdbc.Driver";
	public static Connection con;

	public static void connect() {
		if (!isConnected()) {
			try {
		        Class.forName(driver);
		      
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,password);
				System.out.println("[MySQL] verbindung aufgebaut!");
			
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}

	}

	public static void disconnect() {
		if (isConnected()) {
			try {
				con.close();
				System.out.println("[MySQL] verbindung getrennt!");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static boolean isConnected() {
		return (con == null ? false : true);

	}

	public static Connection getCon() {
		return con;
	}

}
