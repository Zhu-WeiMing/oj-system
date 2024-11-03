package com.zwm.oj.judge.codeSandbox;

import com.zwm.oj.judge.codeSandbox.impl.ExampleCodeSandbox;
import com.zwm.oj.judge.codeSandbox.impl.RemoteCodeSandbox;
import com.zwm.oj.judge.codeSandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 * 使用方法：静态工厂(可以根据字符串动态生成实例，提高通用性)
 */
public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) {
        switch (type){
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
