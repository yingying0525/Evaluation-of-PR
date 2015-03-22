package sleeve.device;

public class Utility implements Comparable<Utility>
{
	private double utility = 0;

	public Utility()
	{
	}

	public Utility(double utility)
	{
		this.utility = utility;
	}

	public double getUtility()
	{
		return utility;
	}

	public void setUtility(double utility)
	{
		this.utility = utility;
	}

	/**
	 * 默认comparator对其降序排列
	 */
	@Override
	public int compareTo(Utility utility)
	{
		if (utility.getUtility() < this.utility)
		{
			return -1;
		}
		else if (utility.getUtility() == this.utility)
		{
			return 0;
		}
		return 1;
	}

}
