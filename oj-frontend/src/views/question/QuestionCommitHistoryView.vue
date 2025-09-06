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
      <template #optional="{ record }">
        <div class="button-container">
          <a-button @click="handleInfo()" shape="round" type="primary">详情</a-button>
          <a-modal v-model:visible="modalVisible" @ok="ev =>modalVisible = false" fullscreen>
            <template #title>
              提交记录详情
            </template>
            <div class="container">
              <div class="left-panel">
                <a-scrollbar style="height:500px;overflow: auto;">
                  <div class="questionInfo">
                    <h1>{{ record.questionVO.title }}</h1>
                    <MdViewer :value="record.questionVO.content"/>
                  </div>
                  <div class="result-status" v-if="record.status == 2">
                    <!--                  <a-tag color="red" size="large">{{ record.judgeInfo }}</a-tag>-->
                    <img src="../../assets/未通过.png" alt="不通过" height="150" width="150" class="status-icon-image"/>
                  </div>
                  <div class="result-status" v-if="record.status == 3">
                    <!--                  <a-tag color="green" size="large">{{ record.judgeInfo }}</a-tag>-->
                    <img src="../../assets/通过.png" alt="通过" height="150" width="150" class="status-icon-image"/>
                  </div>
                </a-scrollbar>
              </div>
              <div class="right-panel">
                <div class="result">
                  <div class="statistics">
                    <a-statistic
                        title="时间消耗（ms）"
                        :value="JSON.parse(record.judgeInfo.time)"
                        :value-from="0"
                        :start="numStart"
                        animation
                        style="background: #c0d0c7; border-radius: 10px; padding: 15px; margin: 10px;"
                    />
                    <a-statistic
                        title="内存消耗（b）"
                        :value="JSON.parse(record.judgeInfo.memory)"
                        :value-from="0"
                        :start="numStart"
                        animation
                        style="background: #f1d8d8; border-radius: 10px; padding: 15px; margin: 10px;"
                    />
                  </div>
                </div>
                <div class="code">
                  <CodeEditor :value="record.code" :read="true"/>
                </div>
              </div>
            </div>
          </a-modal>
        </div>
      </template>
      <template #userAvatar="{ record }">
        <img
            :src="record.userVO.userAvatar"
            alt="avatar"
            style="width: 40px; height: 40px; border-radius: 50%;"
        />
      </template>
      <template #updateTime="{ record }">
        {{ formatDate(record.updateTime) }}
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
import MdViewer from "@/components/MdViewer.vue";
import CodeEditor from "@/components/CodeEditor.vue";

const numStart = ref<boolean>(false)
const visible = ref(false);
const modalVisible = ref(false);
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

const handleInfo = () => {
  modalVisible.value = !modalVisible.value;
  numStart.value = true;
}

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
    userAvatar: "",
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
  width: 90%; /* 设置表格宽度 */
  margin: 0 auto; /* 水平居中 */
  white-space: nowrap;
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

.container {
  display: flex;
  height: 100vh;
}

.left-panel {
  position: relative; /* 添加相对定位 */
  width: 40%;
  padding: 20px;
}

.right-panel {
  width: 60%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;

}

.questionInfo {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.result {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.statistics {
  display: flex;
  gap: 20px;
}

.code {
  flex: 1;
  padding-top: 20px;
}

.result-status {
  position: absolute; /* 使用绝对定位 */
  top: 10px; /* 距离顶部10px */
  right: 10px; /* 距离右侧10px */
  display: flex;
  align-items: center;
  gap: 10px;
}

.statistics {
  display: flex;
  gap: 10px;
  flex: 1;
}

.statistics > * {
  flex: 1;
}
</style>