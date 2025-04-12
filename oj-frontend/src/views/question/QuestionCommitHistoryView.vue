<!--todo 菜单-->
<template>
  <div class="userAdmin">
    <a-form :model="questionSubmitQueryRequest" layout="inline" style="justify-content: center;">
      <a-form-item field="title" label="昵称" style="min-width: 280px">
        <a-input v-model="questionSubmitQueryRequest.status" placeholder="请输入昵称"/>
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 280px">
        <a-input v-model="questionSubmitQueryRequest.userId" placeholder="请输入id"/>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </a-form-item>
    </a-form>
    <a-table :columns="columns" :data="data" class="center-table">
      <template #userAvatar="{ record }">
        <img
            :src="record.userVO.userAvatar"
            alt="avatar"
            style="width: 40px; height: 40px; border-radius: 50%;"
        />
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {
  QuestionControllerService,
  QuestionSubmitQueryRequest,
  type QuestionSubmitVO,
  User,
  UserControllerService
} from "../../../generated";
import moment from "moment";
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

const questionSubmitQueryRequest = ref<QuestionSubmitQueryRequest>({
  code: "",
  current: null,
  language: "",
  pageSize: null,
  questionId: null,
  sortField: "",
  sortOrder: "",
  status: null,
  userId: null,
  updateTime: "",
  judgeInfo: {
    time: "",
    memory: "",
  },
  userVO: {
    id: null,
    userName: "",
    userAvatar:"",
  },
});
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '题目id',
    dataIndex: 'questionId',
  },
  {
    title: '头像',
    dataIndex: 'userVO.userAvatar',
    slotName: 'userAvatar'
  },
  {
    title: '提交者',
    dataIndex: 'userVO.userName',
  },
  {
    title: 'userId',
    dataIndex: 'userVO.id',
  },
  {
    title: '编程语言',
    dataIndex: 'language',
  },
  {
    title: '运行时间(ms)',
    dataIndex: 'judgeInfo.time'
  },
  {
    title: '运行内存(b)',
    dataIndex: 'judgeInfo.memory'
  },
  {
    title: '提交时间',
    dataIndex: 'updateTime',
    slotName: 'updateTime'
  },
  {
    title: '操作',
    slotName: 'optional'
  }
];
const data = ref<QuestionSubmitVO[]>([]);
const loadData = async () => {
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(questionSubmitQueryRequest.value);
  console.log(res)
  if (res.code === 0) {
    data.value = res.data.records;
  }
};
const formatDate = (date: string) => {
  return moment(date).format("YYYY-MM-DD HH:mm:ss");
};


const handleSearch = () => {
  questionSubmitQueryRequest.value.current = 1; // 重置页码为1
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