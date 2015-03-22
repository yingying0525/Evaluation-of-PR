package sleeve.worker;

import java.util.Arrays;
import java.util.List;

import sleeve.device.Buffer;
import sleeve.device.Utility;
import sleeve.util.Log;
import sleeve.util.LogFactory;

public abstract class Worker<S extends Utility, T extends Utility> implements
		Runnable, Cloneable
{
	private String workerId = null;
	private List<Buffer<S>> sources = null;
	private List<Buffer<T>> targets = null;

	public Worker(String workerId, List<Buffer<S>> source,
			List<Buffer<T>> target)
	{
		this.workerId = workerId;
		this.sources = source;
		this.targets = target;
	}

	/*@SuppressWarnings("unchecked")
	public Worker(String workerId, Buffer<S> source, List<Buffer<T>> target)
	{
		this.workerId = workerId;
		this.sources = Arrays.asList(source);
		this.targets = target;
	}*/

	/*@SuppressWarnings("unchecked")
	public Worker(String workerId, Buffer<S> source, Buffer<T> target)
	{
		this.workerId = workerId;
		this.sources = Arrays.asList(source);
		this.targets = Arrays.asList(target);
	}*/
	
	@SuppressWarnings("unchecked")
	public Worker(String workerId, Buffer<S> source)
	{
		this.workerId = workerId;
		this.sources = Arrays.asList(source);
		this.targets = null;
	}

	/*@SuppressWarnings("unchecked")
	public Worker(String workerId, List<Buffer<S>> source, Buffer<T> target)
	{
		this.workerId = workerId;
		this.sources = source;
		this.targets = Arrays.asList(target);
	}*/

	@SuppressWarnings("unchecked")
	@Override
	public Worker<S, T> clone()
	{
		Log log = LogFactory.getInstance().getLog("system.log");
		try
		{
			return (Worker<S, T>) super.clone();
		} catch (CloneNotSupportedException e)
		{
			log.exception(WorkerId());
			log.exception(e);
			return null;
		}
	}

	public abstract void move(List<Buffer<S>> sources, List<Buffer<T>> targets)
			throws InterruptedException;

	public boolean readyToStop()
	{
		for (Buffer<S> s : sources)
		{
			if (!s.isExhaust())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void run()
	{
		Log log = LogFactory.getInstance().getLog("system.log");
		try
		{
			while (!Thread.interrupted())
			{
				if (readyToStop())
				{
					if (targets != null)
					{
						for (Buffer<T> t : targets)
						{
							t.setReadyToExhaust(true);
						}
					}
					break;
				}
				move(this.sources, this.targets);
			}
		} catch (InterruptedException e)
		{
			log.exception(WorkerId());
			log.exception(e);
		} finally
		{
			log.info("Thread: {" + Thread.currentThread() + "(" + WorkerId()
					+ ")" + "} terminate.");
		}
	}

	public String WorkerId()
	{
		return this.workerId;
	}

	public void addSource(Buffer<S>... source)
	{
		for (Buffer<S> s : source)
			sources.add(s);
	}

	public void addTarget(Buffer<T>... target)
	{
		for (Buffer<T> t : target)
			targets.add(t);
	}

	public List<Buffer<S>> getSources()
	{
		return this.sources;
	}

	public List<Buffer<T>> getTargets()
	{
		return this.targets;
	}
}
