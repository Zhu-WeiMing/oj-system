package com.zwm.codesandbox.unsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 恶意代码：执行写入的木马程序
 */
public class RunOtherError {

    public static void main(String[] args) throws IOException, InterruptedException {

        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        Process process = Runtime.getRuntime().exec(filePath);
        process.waitFor();

        // 分批获取进程的输出
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // 逐行获取
        String compileOutputLine;
        while((compileOutputLine = bufferedReader.readLine()) != null){
            System.out.println(compileOutputLine);
        }
        System.out.println("执行木马程序成功");
    }
}