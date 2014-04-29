package performance;

import static java.lang.System.out;

import java.lang.reflect.Field;

public class InterThreadLatencyWithLazySet {
	private static final int REPETITIONS = 100 * 1000 * 1000;
	static sun.misc.Unsafe unsafe;
	private static final long pingStatic;
	private static final long pongStatic;
	private static final Contain con = new Contain();

	static {
		try {
			Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (sun.misc.Unsafe) field.get(null);
			pingStatic = unsafe
					.objectFieldOffset(Contain.class
							.getDeclaredField("ping"));
			pongStatic = unsafe
					.objectFieldOffset(Contain.class
							.getDeclaredField("pong"));
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	public static void main(final String[] args) throws Exception {
		for (int i = 0; i < 5; i++) {
			final long duration = runTest();

			out.printf("%d - %dns avg latency - ping=%d pong=%d\n", i, duration
					/ (REPETITIONS * 2), con.ping, con.pong);
		}
	}

	private static long runTest() throws InterruptedException {
		final Thread pongThread = new Thread(new PongRunner());
		final Thread pingThread = new Thread(new PingRunner());
		pongThread.start();
		pingThread.start();

		final long start = System.nanoTime();
		pongThread.join();

		return System.nanoTime() - start;
	}

	public static class PingRunner implements Runnable {
		public void run() {
			for (int i = 0; i < REPETITIONS; i++) {
					
				unsafe.putOrderedInt(con, pingStatic, i);

				while (i != con.pong) {
					// busy spin
				}
			}
		}
	}

	public static class PongRunner implements Runnable {
		public void run() {
			for (int i = 0; i < REPETITIONS; i++) {
				while (i != con.ping) {
					// busy spin
				}

				unsafe.putOrderedInt(con, pongStatic, i);
			}
		}
	}
	
	public static class Contain{
		public volatile int ping = -1;
		public volatile int pong = -1;
	}
}
