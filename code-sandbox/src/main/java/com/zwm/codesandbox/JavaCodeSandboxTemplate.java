package com.zwm.codesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zwm.codesandbox.model.ExecuteCodeRequest;
import com.zwm.codesandbox.model.ExecuteCodeResponse;
import com.zwm.codesandbox.model.ExecuteMessage;
import com.zwm.codesandbox.model.JudgeInfo;
import com.zwm.codesandbox.utils.ProcessUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        // File.separator是为了兼容不同操作系统
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();

        // 判断全局代码目录是否存在，不存在则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 1、把用户的代码保存为文件
        File userCodeFile = saveCodeToDile(code,userCodeParentPath);

        System.out.println(userCodeFile.getAbsolutePath());
        // 2、编译代码得到class文件
        ExecuteMessage compileMessage = compileFile(userCodeFile);
        System.out.println(compileMessage);

        // 3、运行代码得到输出结果
        List<ExecuteMessage> executeMessages = runFile(inputList, userCodeParentPath);

        // 4、收集整理的输出结果
        ExecuteCodeResponse executeCodeResponse = getOutputResponse(executeMessages);

        // 5、删除临时文件
        deleteFile(userCodeFile);

        return executeCodeResponse;
    }

    /**
     * 1.把用户的代码保存为文件
     * @param code 用户代码
     * @return
     */
    public File saveCodeToDile(String code,String userCodeParentPath) {

        // 1、把用户的代码保存为文件
        
        // 用户存放的代码进行隔离
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;

    }

    /**
     * 2.编译代码得到class文件
     * @param userCodeFile 用户代码文件
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile){
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        ExecuteMessage compileMessage = ProcessUtil.getProcessMessage(compileCmd, "编译");
        return compileMessage;
    }

    /**
     * 3、运行代码得到输出结果
     * @param inputList 输入代码
     * @return
     */
    public List<ExecuteMessage> runFile(List<String> inputList,String userCodeParentPath){
        List<ExecuteMessage> executeMessages = new ArrayList<>();
        for (String inputArgs : inputList) {
            /**
             * -Xmx256m是一个JVM（Java虚拟机）启动参数，用于设置JVM堆内存的最大大小。具体来说：
             *
             * -Xmx: 这个参数用于指定JVM堆内存的最大值。Xmx后面跟着的值表示堆内存的最大限制。
             * 256m: 表示堆内存的最大限制是256兆字节（MB）。m是兆字节的单位，通常用于表示内存大小。
             */
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            ExecuteMessage runMessage = ProcessUtil.getProcessMessage(runCmd, "运行");
            System.out.println(runMessage);
            executeMessages.add(runMessage);
        }
        return executeMessages;
    }

    /**
     * 4.收集整理的输出结果
     * @return executeCodeResponse 输出结果
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessages){

        // 判断运行时间是否超时
        long maxTime = 0;
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        for (ExecuteMessage executeMessage : executeMessages) {
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
        return executeCodeResponse;
    }

    /**
     * 删除文件
     * @param userCodeFile 要删除的文件
     */
    public void deleteFile(File userCodeFile){
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeFile.getParentFile());
            System.out.println("删除" + (del ? "成功" : "失败"));
        }

    }


}
