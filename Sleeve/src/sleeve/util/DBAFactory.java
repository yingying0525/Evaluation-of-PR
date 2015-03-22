package sleeve.util;

public class DBAFactory
{
	private DBA dba = null;

	private static DBAFactory instance = null;

	private DBAFactory()
	{
		String dbaType = ConfigFactory.getInstance().get("db.dba");
		if (dbaType.equals("pooled"))
		{
			dba = new PooledDBA();
		}
		else if (dbaType.equals("single"))
		{
			dba = new SingleDBA();
		}
	}

	public static synchronized DBAFactory getInstance()
	{
		if (instance == null)
		{
			instance = new DBAFactory();
		}
		return instance;
	}

	public synchronized DBA getDBA()
	{
		return this.dba;
	}
}
