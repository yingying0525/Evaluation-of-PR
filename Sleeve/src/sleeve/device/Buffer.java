package sleeve.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 该generics实现了一个线程安全的缓冲区
 * 
 * @author bianhaoqiong@163.com
 * 
 * @param <T>
 *            T类型应继承自Utility，并具有有意义的CompareTo()方法，
 *            UtilityHeap将根据此方法对缓冲区进行调整，使其中的元素保持有序
 */
public class Buffer<T extends Utility>
{
	private List<T> sortedList = null;
	private boolean readyToExhaust = false;
	private int minBufferSize = 0;
	private int maxBufferSize = 0;
	private int importNum = 0;

	public Buffer(int minBufferSize, int maxBufferSize)
	{
		this.maxBufferSize = maxBufferSize;
		this.minBufferSize = minBufferSize;
		sortedList = new ArrayList<T>();
	}
	
	/*public Buffer(List<T> list, int minBufferSize, int maxBufferSize)
	{
		if (list != null && list.size() >= minBufferSize && list.size() <= maxBufferSize)
		{
			sortedList = list;
		}
		else
		{
			sortedList = new ArrayList<T>();
		}
		importNum = sortedList.size();
		this.maxBufferSize = maxBufferSize;
		this.minBufferSize = minBufferSize;
	}*/
	
	public synchronized void setReadyToExhaust(boolean readyToExhaust)
	{
		this.readyToExhaust = readyToExhaust;
	}
	
	public synchronized boolean isExhaust()
	{
		return this.readyToExhaust && size() <= minBufferSize;
	}
	
	public synchronized void sort()
	{
		Collections.sort(sortedList);
	}

	public synchronized int getImportedNum()
	{
		return importNum;
	}

	public synchronized int getExportedNum()
	{
		return importNum - size();
	}

	/**
	 * 从缓冲区中抽取最顶端的元素，如果缓冲区中元素个数小于等于最小元素个数的限制，
	 * 调用该方法的线程将被阻塞。
	 * 该方法可能唤醒因调用push()方法而在该对象上阻塞的其他线程
	 * 
	 * @return 缓冲区中最顶端的元素
	 * @throws InterruptedException
	 */
	public synchronized T pop() throws InterruptedException
	{
		while (sortedList.size() <= minBufferSize)
		{
			this.wait();
		}
		T e = sortedList.remove(0);
		if (sortedList.size() < maxBufferSize)
		{
			this.notifyAll();
		}
		return e;
	}

	/**
	 * 将元素插入到缓冲区中，插入后会自动调整缓冲区，使其有序。，如果缓冲区中元素个数大于等于最大元素个数的限制，调用该方法的线程将被阻塞
	 * 如果缓冲区中不含有元素e，该方法将e插入缓冲区中，并可能唤醒因调用popTop()方法而在该对象上阻塞的其他线程
	 * 
	 * @param e
	 * @return 如果缓冲区中不含有元素e，返回true，否则返回false
	 * @throws InterruptedException
	 */
	public synchronized boolean push(T e) throws InterruptedException
	{
		while (sortedList.size() >= maxBufferSize)
		{
			this.wait();
		}

		if (sortedList.add(e))
		{
			importNum++;
			if (sortedList.size() > minBufferSize)
			{
				this.notifyAll();
			}
			return true;
		}
		return false;
	}

	/**
	 * 检查缓冲区中是否存在元素e
	 * 
	 * @param e
	 * @return 如果存在，返回true，不存在则返回false
	 */
	public synchronized boolean contains(T e)
	{
		return sortedList.contains(e);
	}

	/**
	 * 将元素e与缓冲区中现有的元素的utility相加
	 * 
	 * @param e
	 * @return 如果缓冲区中含有元素e，返回true，否则返回false
	 */
	public synchronized boolean merge(T e)
	{
		if (sortedList.contains(e))
		{
			T element = sortedList.get(sortedList.indexOf(e));
			element.setUtility(element.getUtility() + e.getUtility());
			return true;
		}
		return false;
	}

	/**
	 * 从缓冲区中抽取最顶端的元素。 如果缓冲区中元素个数小于等于最小元素个数的限制，该方法返回null，不会将调用该方法的现成阻塞。
	 * 但可能唤醒因调用insert()方法而在该对象上阻塞的其他线程
	 * 
	 * @return 缓冲区中最顶端的元素
	 * @throws InterruptedException
	 */
	/*public synchronized T popNoBlock()
	{
		if (sortedList.size() <= minBufferSize)
		{
			return null;
		}
		T e = sortedList.remove(0);
		if (sortedList.size() < maxBufferSize)
		{
			this.notifyAll();
		}
		return e;
	}*/

	/**
	 * 从缓冲区中获得最顶端的元素，并不将元素从缓冲区中删除
	 * 
	 * @return 缓冲区中最顶端的元素
	 * @throws InterruptedException
	 */
	public synchronized T getTopNoBlock()
	{
		if (sortedList.size() > minBufferSize)
		{
			return sortedList.get(0);
		}
		return null;
	}

	/**
	 * 从缓冲区中删除对应的元素，该方法不会导致调用该方法的线程被阻塞
	 * 
	 * @return 删除成功-true，删除失败-false
	 * @throws InterruptedException
	 */
	/*public synchronized boolean removeNoBlock(T e)
	{
		if (sortedList.size() <= minBufferSize)
		{
			return false;
		}
		boolean res = sortedList.remove(e);
		if (sortedList.size() < maxBufferSize)
		{
			this.notifyAll();
		}
		return res;
	}*/

	/**
	 * 从缓冲区中获得第eid个元素，并不将元素从缓冲区中删除
	 * 
	 * @param eid
	 *            元素的下标（缓冲区中的元素已经根据utility排序）
	 * @return 第eid个元素
	 * @throws InterruptedException
	 */
	public synchronized T getNoBlock(int eid)
	{
		if (eid >= 0 && sortedList.size() > eid)
		{
			return sortedList.get(eid);
		}
		return null;
	}

	/**
	 * 获取缓冲区中元素的个数
	 * 
	 * @return
	 */
	public synchronized int size()
	{
		return sortedList.size();
	}

	public synchronized void clear()
	{
		this.sortedList.clear();
		this.importNum = 0;
	}
}
