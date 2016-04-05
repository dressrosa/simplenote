/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.test.controller;

import java.util.Date;
import java.util.LinkedList;
import org.apache.commons.lang3.RandomUtils;
import com.xiaoyu.common.utils.TimeUtils;

/**简单的生产消费者
 * @author xiaoyu 2016年4月3日
 */
public class TestABC {

	public static void main(String args[]) {
		LinkedList<Object> list = new LinkedList<>();
		Consumer con = new Consumer(list);
		Producer pro = new Producer(list);
		Thread tc = new Thread(con);
		Thread tp = new Thread(pro);
		tp.start();

		tc.start();

	}

}

class Consumer implements Runnable {

	private LinkedList<Object> list;

	public Consumer(LinkedList<Object> list) {
		this.list = list;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			consume();
		}

	}

	private synchronized void consume() {
		while (list.size() == 0) {
			try {
				System.out.println("暂无进货,请等待!!");
				wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Object o = list.remove();
		System.out.println("消费了一个商品:"
				+ TimeUtils.format((Date) o, "yyyy-MM-dd HH:mm:ss") + " 当前存货:"
				+ list.size());
		notifyAll();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class Producer implements Runnable {

	private LinkedList<Object> list;

	public Producer(LinkedList<Object> list) {
		this.list = list;
	}

	@Override
	public void run() {
		while (true) {
			produce();
		}
	}

	private synchronized void produce() {
		// TODO Auto-generated method stub
		if (list.size() == 10) {
			try {
				System.out.println("生产达到了10个,放假5天............");
				wait(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("开始投入生产:");
		Date d = new Date();
		list.add(d);
		System.out.println("生产了一个:"
				+ TimeUtils.format((d),
						"yyyy-MM-dd HH:mm:ss" + " 当前存货:" + list.size()));
		System.out.println("通知消费");
		notifyAll();
		try {
			int a = RandomUtils.nextInt(1, 10);
			Thread.sleep(a * 1000);
			System.out.println("睡眠:" + a + "秒");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}