package com.zwm.codesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.zwm.codesandbox.model.ExecuteCodeRequest;
import com.zwm.codesandbox.model.ExecuteCodeResponse;
import com.zwm.codesandbox.model.ExecuteMessage;
import com.zwm.codesandbox.model.JudgeInfo;
import com.zwm.codesandbox.utils.ProcessUtil;
import javafx.scene.paint.Stop;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class JavaDockerCodeSandbox implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final boolean FIRST_INIT = false;

    private static final long TIME_OUT = 5000L;


    public static void main(String[] args) {
        JavaDockerCodeSandbox javaNativeCodeSandbox = new JavaDockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/SleepError.java",StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        // 1、把用户的代码保存为文件
        String userDir = System.getProperty("user.dir");
        // File.separator是为了兼容不同操作系统
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        // 判断全局代码目录是否存在，不存在则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 用户存放的代码进行隔离
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        System.out.println(userCodeFile.getAbsolutePath());
        // 2、编译代码得到class文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        ExecuteMessage compileMessage = ProcessUtil.getProcessMessage(compileCmd, "编译");
        System.out.println(compileMessage);

        //3.把编译好的文件上传到容器环境内
        //获取默认的 Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        //拉取镜像
        String image = "openjdk:8-alpine";
        if (FIRST_INIT) {
            System.out.println("首次下载镜像");
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("下载镜像异常");
                throw new RuntimeException(e);
            }
            System.out.println("镜像下载完成");
        }
        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L)
                .withCpuCount(1L)
                .setBinds(new Bind(userCodeParentPath, new Volume(("/app"))));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        //启动容器
        System.out.println("启动容器");
        dockerClient.startContainerCmd(containerId).exec();

        //获取每个用例的输出结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        final long[] maxMemory = {0L};

        //输入 输入用例 获取结果
        //docker exec vigorous_joliot(容器名字) java -cp /app Main 1 3
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令" + execCreateCmdResponse);

            //收取执行后的结果集
            ExecuteMessage executeMessage = new ExecuteMessage();

            String execId = execCreateCmdResponse.getId();
            long time = 0L;
            final boolean[] timeout = {true};
            if (StrUtil.isNotBlank(execId)) {
                ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                    @Override
                    public void onComplete() {
                        //如果执行完成，则表示没超时
                        timeout[0] = false;
                        super.onComplete();
                    }

                    @Override
                    public void onNext(Frame frame) {
                        executeMessage.setSuccessMessage(new String(frame.getPayload()));
                        System.out.println("输出结果：" + executeMessage);
                        super.onNext(frame);
                    }
                };



                //获取占用内存
                StatsCmd statsCmd = dockerClient.statsCmd(containerId);
                ResultCallback<Statistics> statisticsResultCallBack = statsCmd.exec(new ResultCallback<Statistics>() {

                    @Override
                    public void onNext(Statistics statistics) {
                        System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                        maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                    }

                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void close() throws IOException {

                    }

                    @Override
                    public void onStart(Closeable closeable) {

                    }


                    @Override
                    public void onError(Throwable throwable) {

                    }

                });
                statsCmd.exec(statisticsResultCallBack);
                statsCmd.close();

                try {
                    stopWatch.start();
                    dockerClient.execStartCmd(execId)
                            .exec(execStartResultCallback)
                            .awaitCompletion(TIME_OUT, TimeUnit.MICROSECONDS);
                    stopWatch.stop();
                    time = stopWatch.getLastTaskTimeMillis();
                } catch (InterruptedException e) {
                    executeMessage.setErrorMessage(e.getMessage());
                    System.out.println("程序执行异常");
                    throw new RuntimeException(e);
                }
                executeMessage.setTime(time);
                executeMessage.setMemory(maxMemory[0]);
                executeMessageList.add(executeMessage);
            }

        }

        // 4、收集整理的输出结果
        // 判断运行时间是否超时
        long maxTime = 0;
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 表示运行错误，比如程序运行失败的信息
                executeCodeResponse.setStatus("3");
                break;
            }
            long time = executeMessage.getTime();
            if (time > maxTime) {
                maxTime = time;
            }
            // 正常运行完成
            executeCodeResponse.setStatus("1");
            // 6、错误处理，提成程序的健壮性
            if (executeMessage.getStatus() != null && executeMessage.getStatus() == 2) {
                // 沙箱错误，可能是编译出现错误
                executeCodeResponse.setStatus("2");
            }

            outputList.add(executeMessage.getSuccessMessage());
        }
        executeCodeResponse.setOutputList(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        // 获取最大运行时间
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        //最大内存
        judgeInfo.setMemory(maxMemory[0]);

        // 5、删除临时文件
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeFile.getParentFile());
            System.out.println("删除" + (del ? "成功" : "失败"));
        }

        return executeCodeResponse;

    }


}


