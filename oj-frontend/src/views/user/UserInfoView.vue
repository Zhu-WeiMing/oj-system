<template>
  <a-form
    ref="formRef"
    :model="form"
    :style="{ width: '600px', margin: 'auto' }"
  >
    <a-space size="large" class="avatar-container">
      <a-avatar
        @click="uploadImage"
        :image-url="store.state.user?.loginUser?.userAvatar"
        :size="100"
      >
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
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRoute } from "vue-router";
import { IconEdit } from "@arco-design/web-vue/es/icon";
import store from "@/store";
import { UserControllerService } from "../../../generated";

const form = ref({
  createTime: "",
  id: "",
  userAvatar: "",
  userName: "",
  userProfile: "",
});

const router = useRoute();

const loadData = async () => {
  const id = router.query.id;
  console.log("id::::" + id);
  if (!id) {
    return;
  }
  const res = await UserControllerService.getUserVoByIdUsingGet(id as any);
  if (res.code === 0) {
    form.value = res.data;
  } else {
    console.log("getUserVoByIdUsingGet::::加载失败");
    message.error("加载失败" + res.message);
  }
};

// todo 上传头像
const uploadImage = () => {
  return;
};

const updateUserInfo = async () => {
  const res = await UserControllerService.updateMyUserUsingPost(form.value);
  if (res.code === 0) {
    loadData();
    console.log("修改成功！");
    message.info("修改成功！");
  } else {
    console.log("更新失败！");
  }
};

onMounted(() => {
  loadData();
  console.log("onMounted");
});
</script>

<style scoped>
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
</style>
