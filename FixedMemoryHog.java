import java.util.ArrayList;
import java.util.List;

public class FixedMemoryHog {
    public static void main(String[] args) {
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

        // 防止程序退出，保持程序运行
        try {
            Thread.sleep(Long.MAX_VALUE); // 保持程序运行
        } catch (InterruptedException e) {
            System.err.println("程序被中断: " + e.getMessage());
        }
    }
}

