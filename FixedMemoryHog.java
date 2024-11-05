import java.util.ArrayList;
import java.util.List;

public class FixedMemoryHog {
    public static void main(String[] args) {
        // 获取传入的 CPU 占用率参数
        double cpuLoad = args.length > 0 ? Double.parseDouble(args[0]) : 0.1; // 默认10%

        // 获取 JVM 允许的最大堆内存（-Xmx）
        long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("JVM 最大堆内存: " + maxMemory / (1024 * 1024 * 1024) + " GB");

        // 计算允许分配的最大内存，预留50M用于其他操作，避免 OutOfMemoryError
        long usableMemory = maxMemory - (50 * 1024 * 1024); // 预留50MB

        // 设置每块内存的分配大小，避免超出单个数组限制
        final int allocationSize = 1024 * 1024 * 64; // 每次分配64MB
        List<byte[]> memoryHogs = new ArrayList<>();

        // 分配尽可能多的内存块，直到达到最大可用内存
        long totalAllocated = 0;
        try {
            while (totalAllocated + allocationSize <= usableMemory) {
                byte[] memoryBlock = new byte[allocationSize]; // 分配64MB
                memoryHogs.add(memoryBlock);
                totalAllocated += allocationSize;
                System.out.println("已分配内存: " + totalAllocated / (1024 * 1024) + " MB");
            }
        } catch (OutOfMemoryError e) {
            System.err.println("内存分配失败: " + e.getMessage());
        }

        // 获取可用的 CPU 核心数
        int numCores = Runtime.getRuntime().availableProcessors();
        System.out.println("可用的 CPU 核心数: " + numCores);

        // 启动多线程，分散 CPU 负载到多个核心
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numCores; i++) {
            Thread thread = new Thread(new CpuHogTask(cpuLoad));
            thread.start();
            threads.add(thread);
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("线程中断: " + e.getMessage());
            }
        }
    }
}

// 负责 CPU 负载控制的任务
class CpuHogTask implements Runnable {
    private final double cpuLoad;

    public CpuHogTask(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    @Override
    public void run() {
        long duration = 100; // 每个周期100毫秒
        try {
            while (true) {
                long start = System.currentTimeMillis();
                // 密集计算阶段
                while (System.currentTimeMillis() - start < duration * cpuLoad) {
                    busyWork(); // 执行 CPU 计算
                }
                // 休眠阶段，减少 CPU 占用
                long sleepTime = (long) (duration * (1 - cpuLoad));
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
        } catch (InterruptedException e) {
            System.err.println("线程被中断: " + e.getMessage());
        }
    }

    // 简单的密集计算方法
    private void busyWork() {
        double x = 0;
        for (int i = 0; i < 1000; i++) {
            x += Math.sin(i);
        }
    }
}
