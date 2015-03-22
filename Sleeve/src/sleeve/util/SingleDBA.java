package sleeve.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SingleDBA implements DBA
{
	private Connection connection = null;
	private static Log dblog = null;
	private static String DBClassName = null;
	private static String DBName = null;
	private static String DBUrl = null;
	private static String DBUser = null;
	private static String DBPassword = null;

	protected SingleDBA()
	{
	}

	static
	{
		try
		{
			DBClassName = ConfigFactory.getInstance().get("db.classname");
			DBName = ConfigFactory.getInstance().get("db.name");
			DBUrl = ConfigFactory.getInstance().get("db.url");
			DBUser = ConfigFactory.getInstance().get("db.user");
			DBPassword = ConfigFactory.getInstance().get("db.password");
			dblog = LogFactory.getInstance().getLog("db");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException, InterruptedException
	{
		if (connection == null || connection.isValid(10) == false)
		{
			Class.forName(DBClassName);
			connection = DriverManager.getConnection(DBUrl + DBName, DBUser, DBPassword);
			if (connection == null)
			{
				dblog.exception("Can not load jdbc and get connection.");
			}
		}
		return connection;
	}

	@Override
	public void close(Connection conn)
	{
	}

	@Override
	public void close(Statement stat)
	{
		try
		{
			if (stat != null)
			{
				stat.close();
				stat = null;
			}
		}
		catch (SQLException e)
		{
			dblog.exception(e);
		}
	}

	@Override
	public void close(ResultSet rest)
	{
		try
		{
			if (rest != null)
			{
				rest.close();
				rest = null;
			}
		}
		catch (SQLException e)
		{
			dblog.exception(e);
		}
	}
}
