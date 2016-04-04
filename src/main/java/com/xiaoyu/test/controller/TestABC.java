/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */ 
package com.xiaoyu.test.controller;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import org.springframework.security.access.intercept.RunAsManager;

/**
 * @author xiaoyu
 *2016年4月3日
 */
public class TestABC {
	
	public static void main(String args[]) {
		Deque<Event> deque = new ArrayDeque<Event>();
		WriterTask w = new WriterTask(deque);
		for(int i =0 ; i < 3; i++) {
			Thread t = new Thread(w);
			t.start();
		}
		Thread a = new Thread(new CleanerTask(deque));
		a.start();
	}
}

class Event {
	private Date date;
	private String event;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
	
}

class WriterTask implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i = 1; i< 100; i++) {
			Event event = new Event();
			event.setDate(new Date());
			event.setEvent(String.
					format("the thread %s has generated"
							+ "an event" , Thread.currentThread().getId()));
			deque.addFirst(event);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private Deque<Event> deque;
	public WriterTask (Deque<Event> deque) {
		this.deque = deque;
	}
	
}
	
 class CleanerTask implements Runnable {

	private Deque<Event> deque;
	
	public CleanerTask(Deque<Event> deque) {
		this.deque = deque;
		Thread.currentThread().setDaemon(true);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			Date date = new Date();
			clean(date);
		}
	}
	private void clean(Date date) {
		// TODO Auto-generated method stub
		long difference;
		boolean delete;
		if(deque.size() == 0) {
			return ;
		}
		delete = false;
		
		do {
			Event e = deque.getLast();
			difference = date.getTime() - e.getDate().getTime();
			if(difference > 5000 || deque.size() > 10) {
				System.out.printf("Cleaner:%s\n",e.getEvent());
				deque.removeLast();
				delete = true;
			}
		}while (difference > 5000 || deque.size() > 10);
		if(delete) {
			System.out.println("Cleaner:Size of the queue"+deque.size());
		}
	}
	
}