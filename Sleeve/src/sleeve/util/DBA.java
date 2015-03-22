package sleeve.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DBA
{
	public Connection getConnection() throws SQLException, ClassNotFoundException, InterruptedException;

	public void close(Connection conn);

	public void close(Statement stat);

	public void close(ResultSet rest);
}
