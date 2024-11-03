package com.zwm.oj.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.zwm.oj.judge.codeSandbox.CodeSandbox;
import com.zwm.oj.judge.codeSandbox.CodeSandboxFactory;
import com.zwm.oj.judge.codeSandbox.CodeSandboxProxy;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeResponse;
import com.zwm.oj.model.enums.QuestionSubmitEnum;
import com.zwm.oj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel 测试
 */
@SpringBootTest
public class EasyExcelTest {
    @Test
    void executeCodeByProxy(){
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance("remote");
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果：\" + (a+b));\n" +
                "    }\n" +
                "}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);

    }
}