package performance;

import static java.lang.System.out;

import java.lang.reflect.Field;

public class InterThreadLatencyWithCAS {

	private static final int REPETITIONS = 100 * 1000 * 1000;
	static sun.misc.Unsafe unsafe;
	private static final long pingStatic;
	private static final long pongStatic;
	private static final Contain con = new Contain();
	private static int pingCount = 0;
	private static int pongCount = 0;
	static {
		try {
			Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (sun.misc.Unsafe) field.get(null);
			pingStatic = unsafe.objectFieldOffset(Contain.class
					.getDeclaredField("ping"));
			pongStatic = unsafe.objectFieldOffset(Contain.class
					.getDeclaredField("pong"));
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	public static void main(final String[] args) throws Exception {
		for (int i = 0; i < 5; i++) {
			con.ping = -1;
			con.pong = -1;
			final long duration = runTest();

			out.printf("%d - %dns avg latency - ping=%d pong=%d\n", i, duration
					/ (REPETITIONS * 2), con.ping, con.pong);
			out.printf("ping count %d, pong count %d\n", pingCount, pongCount);
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

				boolean result = unsafe.compareAndSwapInt(con, pingStatic,
						i - 1, i);
				if (!result) {
					pingCount++;
				}
				while (i != con.pong) {
				}
			}
		}
	}

	public static class PongRunner implements Runnable {
		public void run() {
			for (int i = 0; i < REPETITIONS; i++) {
				while (i != con.ping) {
				}

				boolean result = unsafe.compareAndSwapInt(con, pongStatic,
						i - 1, i);
				if (!result) {
					pongCount++;
				}
			}
		}
	}

	public static class Contain {
		public volatile int ping = -1;
		public volatile int pong = -1;
	}
}
