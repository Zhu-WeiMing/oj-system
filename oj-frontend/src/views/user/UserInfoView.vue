<template>
  <div class="container">
    <div class="menu-demo">
      <a-menu mode="popButton">
        <a-menu-item key="1" @click="toUserInfo">
          <template #icon>
            <icon-user></icon-user>
          </template>
          个人资料
        </a-menu-item>
        <a-menu-item key="2" @click="toUserProgress">
          <template #icon>
            <icon-bulb></icon-bulb>
          </template>
          进展分析
        </a-menu-item>
        <a-menu-item key="2" @click="toUserPost">
          <template #icon>
            <icon-message />
          </template>
          我的讨论
        </a-menu-item>
      </a-menu>
    </div>
    <a-form
      ref="formRef"
      :model="form"
      :style="{ width: '600px', marginLeft: '20rem' }"
    >
      <a-space size="large" class="avatar-container">
        <a-avatar @click="uploadImage" :image-url="form.userAvatar" :size="100">
          <template #trigger-icon>
            <IconEdit />
          </template>
        </a-avatar>
      </a-space>
      <a-form-item field="name" label="姓名" validate-trigger="blur">
        <a-input
          v-model="form.userName"
          placeholder="please enter your username..."
        />
      </a-form-item>
      <a-form-item field="profile" label="简介">
        <a-input
          v-model="form.userProfile"
          placeholder="please enter your profile..."
        />
      </a-form-item>
      <a-form-item field="createTime" label="注册时间">
        <a-input
          disabled
          v-model="form.createTime"
          placeholder="please enter your createTime..."
        />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button html-type="submit" @click="updateUserInfo">修改</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>

</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRoute, useRouter } from "vue-router";
import { IconBulb, IconEdit, IconMessage, IconUser } from "@arco-design/web-vue/es/icon";
import { FileControllerService, UserControllerService } from "../../../generated";
import store from "@/store";

const form = ref({
  createTime: "",
  id: "",
  userAvatar: "",
  userName: "",
  userProfile: ""
});

const router = useRoute();

const loadData = async () => {
  const id = router.query.id;
  if (!id) {
    return;
  }
  const res = await UserControllerService.getUserVoByIdUsingGet(id as any);
  if (res.code === 0) {
    form.value = res.data;
  } else {
    message.error("加载失败" + res.message);
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

    // 调用上传文件的接口
    const res = await FileControllerService.uploadFileUsingPost(
      "user_avatar",
      file
    );

    if (res.code === 0) {
      form.value.userAvatar = res.data;
      message.info("上传成功！");
    } else {
      message.error("上传失败：" + res.message);
    }
  };

  // 触发文件输入元素
  fileInput.click();
};

const updateUserInfo = async () => {
  const res = await UserControllerService.updateMyUserUsingPost(form.value);
  if (res.code === 0) {
    loadData();
    message.info("修改成功！");
  } else {
    message.error("修改失败");
  }
};

onMounted(() => {
  loadData();
});
const userouter = useRouter();
const toUserInfo = () => {
  userouter.push({
    path: "/user/info",
    query: { id: store.state.user?.loginUser?.id }
  });
};
const toUserPost = () => {
  userouter.push({
    path: "/user/post",
    query: { id: store.state.user?.loginUser?.id }
  });
};
const toUserProgress = () => {
  userouter.push({
    path: "/user/progress",
    query: { id: store.state.user?.loginUser?.id }
  });
};

</script>

<style scoped>
.container {
  display: flex;
  padding: 1rem;
}

.avatar-container {
  display: flex;
  justify-content: center; /* 使头像居中 */
  margin-bottom: 20px; /* 添加一些底部边距 */
}

a-form {
  display: flex;
  flex-direction: column;
  align-items: center; /* 使表单内容居中 */
}

a-form-item {
  width: 100%; /* 使表单项宽度为100% */
}

.menu-demo {
  box-sizing: border-box;
  z-index: 99;
  margin-top: 3rem;
}

.menu-demo .arco-menu {
  width: 200px;
  height: 100%;
  box-shadow: 0 0 1px rgba(0, 0, 0, 0.3);
}

.menu-demo .arco-menu :deep(.arco-menu-collapse-button) {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.menu-demo .arco-menu:not(.arco-menu-collapsed) :deep(.arco-menu-collapse-button) {
  right: 0;
  bottom: 8px;
  transform: translateX(50%);
}

.menu-demo .arco-menu:not(.arco-menu-collapsed)::before {
  content: '';
  position: absolute;
  right: 0;
  bottom: 0;
  width: 48px;
  height: 48px;
  background-color: inherit;
  border-radius: 50%;
  box-shadow: -4px 0 2px var(--color-bg-2), 0 0 1px rgba(0, 0, 0, 0.3);
  transform: translateX(50%);
}

.menu-demo .arco-menu.arco-menu-collapsed {
  width: 48px;
  height: auto;
  padding-top: 24px;
  padding-bottom: 138px;
  border-radius: 22px;
}

.menu-demo .arco-menu.arco-menu-collapsed :deep(.arco-menu-collapse-button) {
  right: 8px;
  bottom: 8px;
}
</style>
