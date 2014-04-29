package performance;

import static java.lang.System.out;

public class InterThreadLatencyWithVolatile
{
    private static final int REPETITIONS = 100 * 1000 * 1000;
 
    private static volatile int ping = -1;
    private static volatile int pong = -1;
 
    public static void main(final String[] args)
        throws Exception
    {
        for (int i = 0; i < 5; i++)
        {
            final long duration = runTest();
 
            out.printf("%d - %dns avg latency - ping=%d pong=%d\n",
                       i,
                       duration / (REPETITIONS * 2),
                       ping,
                       pong);
        }
    }
 
    private static long runTest() throws InterruptedException
    {
        final Thread pongThread = new Thread(new PongRunner());
        final Thread pingThread = new Thread(new PingRunner());
        pongThread.start();
        pingThread.start();
 
        final long start = System.nanoTime();
        pongThread.join();
 
        return System.nanoTime() - start;
    }
 
    public static class PingRunner implements Runnable
    {
        public void run()
        {
            for (int i = 0; i < REPETITIONS; i++)
            {
                ping = i;
 
                while (i != pong)
                {
                    // busy spin
                }
            }
        }
    }
 
    public static class PongRunner implements Runnable
    {
        public void run()
        {
            for (int i = 0; i < REPETITIONS; i++)
            {
                while (i != ping)
                {
                    // busy spin
                }
 
                pong = i;
            }
        }
    }
}