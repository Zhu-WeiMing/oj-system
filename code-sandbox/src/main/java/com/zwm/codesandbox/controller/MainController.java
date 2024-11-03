package com.zwm.codesandbox.controller;

import com.zwm.codesandbox.JavaNativeCodeSandbox;
import com.zwm.codesandbox.model.ExecuteCodeRequest;
import com.zwm.codesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("/")
public class MainController {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }


    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest){
        return javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }
}
