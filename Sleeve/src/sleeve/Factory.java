package sleeve;

import java.util.HashMap;
import java.util.Set;

public class Factory
{
	private static Factory instance = null;

	private HashMap<String, Pipeline> map = null;

	private Factory()
	{
		this.map = new HashMap<String, Pipeline>();
	}

	/**
	 * 获得一个工厂实例
	 * 
	 * @return
	 */
	public static Factory getInstance()
	{
		if (instance == null)
		{
			instance = new Factory();
		}
		return instance;
	}

	/**
	 * 创建一条流水线
	 * 
	 * @param pipelineID
	 * @return
	 */
	public boolean loadPipeline(Pipeline pipeline)
	{
		if (map.containsKey(pipeline.getId()))
		{
			return false;
		}
		map.put(pipeline.getId(), pipeline);
		pipeline.start();
		return true;
	}

	/**
	 * 获取所有流水线对应的实体类型
	 * 
	 * @return
	 */
	public Set<String> getPipelineIDs()
	{
		return map.keySet();
	}

	/**
	 * 获得一条流水线
	 * 
	 * @param entityType
	 *            流水线对应的实体类型
	 * @return
	 */
	public Pipeline getPipeline(String pipelineID)
	{
		return map.get(pipelineID);
	}

	public void unloadPipeline(String pipelineID)
	{
		this.map.get(pipelineID).stop();
		this.map.remove(pipelineID);
		System.gc();
	}
}
