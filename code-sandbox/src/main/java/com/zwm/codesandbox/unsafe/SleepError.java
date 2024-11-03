package com.zwm.codesandbox.unsafe;

/**
 * 无限休眠（阻塞程序执行）
 */
public class SleepError {
    public static void main(String[] args) throws InterruptedException {
        long ONE_HOUR = 1000 * 60 * 60L;
        Thread.sleep(ONE_HOUR);
        System.out.println("睡死了");
    }
}
