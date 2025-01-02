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
      </a-menu>
    </div>

    <div class="question_submit_list">
      <a-table
        :columns="columns"
        :data="dataList"
        @row-click="openDrawer"
      >

        <template #questionSubmitStatus="{ record }">
          <a-space size="mini">
            <a-tooltip v-if="record.questionSubmitStatus===3" content="已解答" background-color="#3491FA" mini>
              <a-progress type="circle" :percent="100" size="mini" />
            </a-tooltip>
            <a-tooltip v-else-if="record.questionSubmitStatus===2 || record.questionSubmitStatus===1" content="尝试过"
                       background-color="#3491FA" mini>
              <a-progress type="circle" status='danger' :percent="100" size="mini" />
            </a-tooltip>

          </a-space>
        </template>

        <template #createTime="{ record }">
          {{ moment(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
        </template>
      </a-table>


    </div>
    <div class="card" :style="{ display: 'flex' }">
      <div class="top-card">
        <a-card :style="{ width: '360px', height: '100px'}">
          <a-statistic title="解题总数" :value=solveTotal show-group-separator>
            <template #suffix>
              <icon-arrow-rise />
            </template>
          </a-statistic>
        </a-card>
        <a-card :style="{ width: '360px', height: '100px'}">
          <a-statistic title="提交总数" :value=commitTotal :precision="2" :value-style="{ color: '#0fbf60' }">
            <template #prefix>
              <icon-arrow-rise />
            </template>
          </a-statistic>
          <a-statistic title="通过率" :value=passRate :precision="2" :value-style="{ color: '#4ba7cb' }">
            <template #prefix>
              <icon-arrow-rise />
            </template>
            <template #suffix>%</template>
          </a-statistic>
        </a-card>
      </div>
    </div>

    <a-drawer :width="600" :visible="visible" @ok="handleOk" @cancel="handleCancel" unmountOnClose>
      <template #title>
        详情
      </template>
      <CodeEditor :value="code ||''" :read="true" />
      <a-affix :offsetBottom="120" align="right">
        <a-button status="success" shape="round" @click="toSendAnswerView(code)">发布题解</a-button>
      </a-affix>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import { IconBulb, IconUser } from "@arco-design/web-vue/es/icon";
import store from "@/store";
import { useRouter } from "vue-router";
import moment from "moment/moment";
import { onMounted, ref } from "vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";

const visible = ref<boolean>(false);
const code = ref<string>("");
const rowQuestionId = "";
const openDrawer = (row: any) => {
  visible.value = true;
  code.value = row.code;
  rowQuestionId = row.questionId;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
const router = useRouter();
const toSendAnswerView = (paramsCode: string) => {
  router.push({
    path: `/answer/send/${rowQuestionId}`,
    query: { paramsCode }
  });
};
const userouter = useRouter();
const toUserInfo = () => {
  userouter.push({
    path: "/user/info",
    query: { id: store.state.user?.loginUser?.id }
  });
};

const toUserProgress = () => {
  userouter.push({
    path: "/user/progress",
    query: { id: store.state.user?.loginUser?.id }
  });
};

/**
 * 页面加载时 请求数据
 */
onMounted(() => {
  loadData();
});

let solveTotal = ref();
let commitTotal = ref();
let passRate = ref();

const loadData = async () => {
  console.log(store.state.user?.loginUser?.id);
  const res = await QuestionControllerService.listLatestByUserId(
    store.state.user?.loginUser?.id as number
  );
  console.log(res);

  if (res.code === 0) {
    dataList.value = res.data;
    console.log(dataList.value);
    const data = [] as any;
    dataList.value.forEach(item => {
      data.push({
        createTime: item.updateTime,
        questionSubmitStatus: item.questionVO.questionSubmitStatus,
        title: item.questionVO.title,
        code: item.code,
        questionId: item.questionId
      });
    });
    dataList.value = data;
  } else {
    message.error("加载失败" + res.message);
  }
  const resCommitData = await QuestionControllerService.myQuestionCommitData();
  console.log("resCommitData", resCommitData.data);
  commitTotal.value = resCommitData.data.commitTotal;
  solveTotal.value = resCommitData.data.solveTotal;
  passRate.value = resCommitData.data.passRate;
  console.log(commitTotal, solveTotal, passRate);
};

const columns = [
  {
    title: "状态",
    slotName: "questionSubmitStatus"
  },
  {
    title: "标题",
    dataIndex: "title"
  },
  {
    title: "最近提交",
    slotName: "createTime"
  }
];
const dataList = ref([]);
</script>

<style scoped>
.card {
  display: flex;
  justify-content: space-between; /* 控制卡片之间的间隔 */
  align-items: center; /* 垂直居中 */
}

.container {
  display: flex;
  padding: 1rem;
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


.question_submit_list {
  max-width: 800px; /* 设置最大宽度 */
  width: 100%; /* 宽度自适应 */
  margin: 0 auto; /* 上下外边距为0，左右外边距自动，实现水平居中 */
}
</style>
