import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FixedMemoryHog {
  public static void main(String[] args) {
    // 获取传入的 CPU 占用率参数
    double cpuLoad = args.length > 0 ? Double.parseDouble(args[0]) : 0.1; // 默认10%

    // 获取 JVM 允许的最大堆内存（-Xmx）
    long usableMemory = Runtime.getRuntime().maxMemory();
    System.out.println("最大可用内存: " + usableMemory / (1024 * 1024) + " MB");

    // 每次分配内存的大小
    // Math.min(1024 * 1024 * 32, (int) (usableMemory / 10));
    final int allocationSize = (int) (usableMemory / 10); // 动态分配10%

    List<ByteBuffer> memoryHogs = new ArrayList<>();
    long totalAllocated = 0;

    // 使用直接内存分配
    try {
      while (totalAllocated + allocationSize <= usableMemory) {
        // 分配直接内存
        ByteBuffer buffer = ByteBuffer.allocateDirect(allocationSize); // 直接内存
        memoryHogs.add(buffer);
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