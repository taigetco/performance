package performance;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorTest {
	public static void main(String[] args){
		Runnable runTask1 = new Runnable(){
			public void run(){
				System.out.println(Thread.currentThread() + " task 1");
			}
		};
		
		Runnable runTask2 = new Runnable(){
			public void run(){
				System.out.println(Thread.currentThread() + " task 2");
			}
		};
		
		Runnable runTask3 = new Runnable(){
			public void run(){
				System.out.println(Thread.currentThread() + " task 3");
			}
		};
		
		Runnable runTask4 = new Runnable(){
			public void run(){
				System.out.println(Thread.currentThread() + " task 4");
			}
		};
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
		executor.scheduleAtFixedRate(runTask1, 20000, 20000, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(runTask2, 2, 2, TimeUnit.SECONDS);
		executor.submit(runTask3);
		executor.scheduleAtFixedRate(runTask4, 2, 2, TimeUnit.SECONDS);
	}
}
