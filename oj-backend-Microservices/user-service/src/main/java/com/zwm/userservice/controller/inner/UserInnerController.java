package com.zwm.userservice.controller.inner;

import com.zwm.client.service.UserFeignClient;
import com.zwm.model.entity.User;
import com.zwm.userservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getById(Long userId) {
        return userService.getById(userId);
    }

    @Override
    @PostMapping("/get/ids")
    public List<User> listByIds(@RequestBody List<Long> userIds) {
        return userService.listByIds(userIds);
    }
}
