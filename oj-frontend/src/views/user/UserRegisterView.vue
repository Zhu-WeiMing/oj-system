<template>
  <a-form
    ref="formRef"
    :model="form"
    :style="{ width: '600px', margin: '0 auto' }"
    @submit="handleSubmit"
  >
    <a-space size="large" class="avatar-container" align="center">
      <a-avatar @click="uploadImage" :image-url="form.userAvatar" :size="100">
        <template #trigger-icon>
          <IconEdit />
        </template>
      </a-avatar>
    </a-space>
    <a-form-item field="name" label="用户名" validate-trigger="blur">
      <a-input
        v-model="form.userName"
        placeholder="please enter your username..."
      />
    </a-form-item>
    <a-form-item field="name" label="账号" validate-trigger="blur">
      <a-input
        v-model="form.userAccount"
        placeholder="please enter your username..."
      />
    </a-form-item>
    <a-form-item field="password" label="密码" validate-trigger="blur">
      <a-input-password
        v-model="form.userPassword"
        placeholder="please enter your password..."
      />
    </a-form-item>
    <a-form-item field="password2" label="确认密码" validate-trigger="blur">
      <a-input-password
        v-model="form.checkPassword"
        placeholder="please confirm your password..."
      />
    </a-form-item>
    <a-form-item>
      <a-space justify="center" align="center">
        <a-button html-type="submit">Submit</a-button>
        <a-button @click="$refs.formRef.resetFields()">Reset</a-button>
      </a-space>
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { IconEdit } from "@arco-design/web-vue/es/icon";
import {
  FileControllerService,
  UserControllerService,
  UserRegisterRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const form = reactive({
  userName: "",
  userAccount: "",
  userPassword: "",
  checkPassword: "",
  userAvatar: "https://bronny.oss-cn-hangzhou.aliyuncs.com/DEFAULT_PHOTO.jpg", //默认头像
} as UserRegisterRequest);

// 使用 useRouter 钩子
const router = useRouter();
// todo 注册
const handleSubmit = async () => {
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code === 0) {
    message.success("添加成功");
    router.back();
  } else {
    message.error("创建失败：" + res.message);
  }
};

const uploadImage = async () => {
  // 创建一个文件输入元素
  const fileInput = document.createElement("input");
  fileInput.type = "file";
  fileInput.accept = "image/*"; // 限制只能选择图片文件

  // 监听文件选择事件
  fileInput.onchange = async (event) => {
    const files = event.target!.files[0];
    if (files.length === 0) {
      message.error("未选择文件");
      return;
    }

    // 获取选择的文件
    const file = files;
    console.log("file:::" + file);

    // 调用上传文件的接口
    const res = await FileControllerService.uploadFileUsingPost(
      "user_avatar",
      file
    );

    if (res.code === 0) {
      form.userAvatar = res.data;
      console.log("form.value.userAvatar:::" + form.userAvatar);
      message.info("上传成功！");
    } else {
      message.error("上传失败：" + res.message);
    }
  };

  // 触发文件输入元素
  fileInput.click();
};
</script>

<style scoped>
.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
