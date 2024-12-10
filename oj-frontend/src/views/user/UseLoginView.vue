<template>
  <div class="userLoginView">
    <a-form
      :model="form"
      :style="{ width: '600px' }"
      auto-label-width
      label-align="left"
      style="max-width: 480px; margin: 0 auto"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号" tooltip="请输入账号">
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item field="post" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item>
        <!-- 使用a-space来布局按钮 -->
        <a-space>
          <a-button html-type="submit">登录</a-button>
          <a-button type="text" @click="toRegister">注册</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const toRegister = () => {
  router.push({
    path: "/user/register",
  });
};

/**
 * 提交表单
 */
const store = useStore();
const router = useRouter();

const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);
const handleSubmit = async () => {
  // 调用后端接口
  const res = await UserControllerService.userLoginUsingPost(form);

  if (res.code === 0) {
    // 登陆成功
    await store.dispatch("user/getLoginUser");
    //跳转到主页
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    // 登录失败
    message.error("登陆失败" + JSON.stringify(res.message));
  }
};
</script>
