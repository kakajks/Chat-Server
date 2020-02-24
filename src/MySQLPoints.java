import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLPoints {
	
	public static String getCode(String name) {
		
		try {
			PreparedStatement ps = MySQL.getCon().prepareStatement("SELECT CODE FROM LOGIN WHERE NAME = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				return rs.getString("CODE");
			}
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

}
