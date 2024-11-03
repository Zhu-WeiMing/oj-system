package com.zwm.codesandbox.utils;


import com.zwm.codesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
public class ProcessUtil {

    private static final long TIME_OUT = 50000000L; // 默认超时时间 5s

    /**
     * 执行获取进程信息（args输入版本）
     *
     * @param runCmd
     * @param opName
     * @return
     */
    public static ExecuteMessage getProcessMessage(String runCmd, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        StopWatch stopWatch = new StopWatch(); //spring的计时器
        StringBuilder compileOutPutStringBuilder = new StringBuilder();
        // 收集运行指令后的信息
        try {
            stopWatch.start();
            Runtime runtime = Runtime.getRuntime();
            Process runProcess = runtime.exec(runCmd);

            // 开启新线程监控程序运行线程，超时直接杀掉线程
            new Thread(() -> {
                try {
                    Thread.sleep(TIME_OUT);
                    runProcess.destroy();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            // 指令正常执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            // 正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");

                // 分批获取进程的输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                // 逐行获取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setSuccessMessage(StringUtils.join(outputStrList,"\n"));
                executeMessage.setStatus(1);
            } else {
                System.out.println(opName + "失败, 错误码" + exitValue);
                // 分批获取进程的输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                // 逐行获取
                List<String> outputStrList = new ArrayList<>();
                // 逐行获取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setErrorMessage(StringUtils.join(outputStrList,"\n"));
                //todo 下面的代码 需理解后判断是否可注解
                // 分批获取进程的错误输出
                // 注意这里获取的是new InputStreamReader(runProcess.getErrorStream()
//                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
//                List<String> errorOutputStrList = new ArrayList<>();
//                // 逐行获取
//                String errorCompileOutputLine;
//                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
//                    errorOutputStrList.add(errorOutputStrList);
//                }
//                executeMessage.setErrorMessage(StringUtils.join(outputStrList,"\n"));
            }

            // 获取最终内存使用情况
            long totalMemory = runtime.totalMemory(); // JVM 的总内存量
            long freeMemory = runtime.freeMemory();   // JVM 的空闲内存量
            long usedMemory = (totalMemory - freeMemory) / 1024 / 8; // JVM 已使用的内存量
            executeMessage.setMemory(usedMemory);

            stopWatch.stop();
            executeMessage.setTime(stopWatch.getTotalTimeMillis());
        } catch (IOException | InterruptedException e) {
            return getErrorResponse(e);
        }
        return executeMessage;
    }

    private static ExecuteMessage getErrorResponse(Throwable e) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        executeMessage.setErrorMessage("沙箱错误（编译错误）");
        // 沙箱错误，可能是编译出现错误
        executeMessage.setStatus(2);
        return executeMessage;

    }

}
