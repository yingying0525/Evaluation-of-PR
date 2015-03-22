package sleeve.util;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class PooledDBA implements DBA
{
	private ComboPooledDataSource cpds = null;
	private static Log dblog = null;
	private static String DBClassName = null;
	private static String DBName = null;
	private static String DBUrl = null;
	private static String DBUser = null;
	private static String DBPassword = null;

	protected PooledDBA()
	{
		cpds = new ComboPooledDataSource("metkb");

        try
		{
			cpds.setDriverClass(DBClassName);
		}
		catch (PropertyVetoException e)
		{
			dblog.exception(e);
		}
		cpds.setJdbcUrl(DBUrl + DBName);
		cpds.setUser(DBUser);
		cpds.setPassword(DBPassword);
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
	public synchronized Connection getConnection() throws SQLException, ClassNotFoundException, InterruptedException
	{
		return cpds.getConnection();
	}

	@Override
	public synchronized void close(Connection conn)
	{
		try
		{
			if (conn != null)
			{
				conn.close();
				conn = null;
			}
		}
		catch (SQLException e)
		{
			dblog.exception(e);
		}
	}

	@Override
	public synchronized void close(Statement stat)
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
	public synchronized void close(ResultSet rest)
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
