<!--todo 菜单-->
<template>
  <div class="userAdmin">
    <a-form :model="userQueryRequest" layout="inline" style="justify-content: center;">
      <a-form-item field="title" label="昵称" style="min-width: 280px">
        <a-input v-model="userQueryRequest.userName" placeholder="请输入昵称"/>
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 280px">
        <a-input v-model="userQueryRequest.id" placeholder="请输入id"/>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </a-form-item>
    </a-form>

    <a-table :columns="columns" :data="data" class="center-table">
      <template #optional="{ record }">
        <a-button @click="handleClick(record)">view</a-button>
        <a-modal v-model:visible="visible" @ok="handleOk(record)" @cancel="handleCancel" width="auto">
          <template #title>
            详情信息
          </template>
          <div>
            <a-form
                ref="formRef"
                :model="user"
                :style="{ width: '600px', marginLeft: '20rem' }"
            >
              <a-space size="large" class="avatar-container">
                <a-avatar @click="uploadImage" :image-url="user.userAvatar" :size="100">
                  <template #trigger-icon>
                    <IconEdit/>
                  </template>
                </a-avatar>
              </a-space>
              <a-form-item field="name" label="姓名">
                <a-input
                    v-model="user.userName"
                    placeholder="please enter your username..."
                />
              </a-form-item>
              <a-form-item field="profile" label="简介">
                <a-input
                    v-model="user.userProfile"
                    placeholder="please enter your profile..."
                />
              </a-form-item>
              <!--              todo 有bug-->
              <a-form-item field="userRole" label="角色">
                <a-select :v-model="user.userRole" :style="{width:'360px'}" placeholder="请选择角色">
                  <a-option>admin</a-option>
                  <a-option>user</a-option>
                </a-select>
              </a-form-item>

              <a-form-item field="createTime" label="注册时间">
                <a-input
                    disabled
                    v-model="user.createTime"
                    placeholder="please enter your createTime..."
                />
              </a-form-item>
            </a-form>
          </div>
        </a-modal>
      </template>
      <template #userAvatar="{ record }">
        <img
            :src="record.userAvatar"
            alt="avatar"
            style="width: 40px; height: 40px; border-radius: 50%;"
        />
      </template>
      <template #createTime="{ record }">
        {{ formatDate(record.createTime) }}
      </template>
      <template #updateTime="{ record }">
        {{ formatDate(record.updateTime) }}
      </template>
      <template #isDelete="{ record }">
        <div v-if="record.isDelete == 0">否</div>
        <div v-else>是</div>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {FileControllerService, User, UserControllerService, UserQueryRequest} from "../../../generated";
import moment from "moment";
import {IconEdit} from "@arco-design/web-vue/es/icon";
import message from "@arco-design/web-vue/es/message";

const visible = ref(false);
const user = ref<User | null>({
  createTime: "",
  id: null,
  isDelete: null,
  mpOpenId: "",
  unionId: "",
  updateTime: "",
  userAccount: "",
  userAvatar: "",
  userName: "",
  userPassword: "",
  userProfile: "",
  userRole: "",
});

const handleClick = (data: any) => {
  visible.value = true;
  user.value = data;
};
const handleOk = async (record: any) => {
  const res = await UserControllerService.updateUserUsingPost({
    id: record.id,
    userAvatar: record.userAvatar,
    userName: record.userName,
    userRole: record.userRole,
    userProfile: record.userProfile,
  })
  if (res.code === 0) {
    message.success("更新成功！")
    loadData()
    visible.value = false;
  } else {
    message.error("更新失败：" + res.message);
  }

};
const handleCancel = () => {
  visible.value = false;
};

onMounted(() => {
  loadData();
});

const userQueryRequest = ref<UserQueryRequest>({
  current: 1,
  id: null,
  mpOpenId: "",
  pageSize: 10,
  sortField: "",
  sortOrder: "",
  unionId: "",
  userName: "",
  userProfile: "",
  userRole: "",
});
const data = ref<User[]>([]);
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '昵称',
    dataIndex: 'userName',
  },
  {
    title: '角色',
    dataIndex: 'userRole',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    slotName: 'userAvatar'
  },
  {
    title: '账号',
    dataIndex: 'userAccount'
  },
  {
    title: '简介',
    dataIndex: 'userProfile'
  },
  {
    title: '是否删除',
    dataIndex: 'isDelete',
    slotName: 'isDelete'
  },
  {
    title: '注册时间',
    dataIndex: 'createTime',
    slotName: 'createTime'
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    slotName: 'updateTime'
  },
  {
    title: '操作',
    slotName: 'optional'
  }
];

const loadData = async () => {
  const res = await UserControllerService.listUserByPageUsingPost(userQueryRequest.value);
  if (res.code === 0) {
    data.value = res.data.records;
  }
};
const formatDate = (date: string) => {
  return moment(date).format("YYYY-MM-DD HH:mm:ss");
};

const uploadImage = async () => {
  const fileInput = document.createElement("input");
  fileInput.type = "file";
  fileInput.accept = "image/*";

  fileInput.onchange = async (event) => {
    const files = event.target!.files;
    if (!files || files.length === 0) {
      message.error("未选择文件");
      return;
    }

    const file = files[0];
    const res = await FileControllerService.uploadFileUsingPost("user_avatar", file);

    if (res.code === 0) {
      user.value.userAvatar = res.data;
      message.info("上传成功！");
    } else {
      message.error("上传失败：" + res.message);
    }
  };

  fileInput.click();
};

const handleSearch = () => {
  userQueryRequest.value.current = 1; // 重置页码为1
  loadData();
};
</script>

<style scoped>
.userAdmin {
  display: flex;
  justify-content: flex-start; /* 使表格居中 */
  align-items: center; /* 垂直居中 */
  flex-direction: column; /* 垂直排列子元素 */
  height: 100vh; /* 使父容器占满整个视口高度 */
}

.center-table {
  width: 80%; /* 设置表格宽度 */
  margin: 0 auto; /* 水平居中 */
}

.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}

.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}

.avatar-container {
  display: flex;
  justify-content: center; /* 使头像居中 */
  margin-bottom: 20px; /* 添加一些底部边距 */
}
</style>