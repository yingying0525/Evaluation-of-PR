package sleeve;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sleeve.device.Buffer;
import sleeve.worker.Worker;

@SuppressWarnings("rawtypes")
public class Pipeline
{
	private String pipelineId = null;
	private boolean isStarted = false;
	private ExecutorService executor = null;

	private List<Worker> workers = null;
	private List<Integer> workerNums = null;

	public Pipeline(String pipelineId)
	{
		this.pipelineId = pipelineId;
		executor = Executors.newCachedThreadPool();
		workers = new ArrayList<Worker>();
		workerNums = new ArrayList<Integer>();
		isStarted = false;
	}

	public String getId()
	{
		return this.pipelineId;
	}

	public void addWorker(Worker worker, int concurrentNum)
	{
		this.workers.add(worker);
		this.workerNums.add(new Integer(concurrentNum));
	}

	/**
	 * 启动流水线
	 */
	protected synchronized void start()
	{
		for (int i = 0; i < workers.size(); ++i)
		{
			for (int j = 0; j < workerNums.get(i); ++j)
			{
				executor.execute(workers.get(i).clone());
			}
		}
		executor.shutdown();
		isStarted = true;
	}

	/**
	 * 停止流水线
	 */
	protected synchronized void stop()
	{
		if (isStarted)
		{
			this.executor.shutdownNow();
			this.isStarted = false;
			for (int i = 0; i < workers.size(); ++i)
			{
				for (Object source : workers.get(i).getSources())
				{
					((Buffer) source).clear();
				}
				workers.get(i).getSources().clear();
				for (Object target : workers.get(i).getTargets())
				{
					((Buffer) target).clear();
				}
				workers.get(i).getTargets().clear();
			}
			workers.clear();
			workerNums.clear();
		}
	}

	/**
	 * 流水线是否已启动
	 * 
	 * @return
	 */
	public synchronized boolean isStarted()
	{
		return isStarted;
	}

	/**
	 * 流水线是否已关闭
	 * 
	 * @return
	 */
	public synchronized boolean isStoped()
	{
		return executor.isTerminated();
	}
}
