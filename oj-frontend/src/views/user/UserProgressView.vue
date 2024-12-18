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
    <div class="question_submit_overview">
      <a-statistic title="解题总数" :value="50.52" :precision="2" :value-style="{ color: '#BB8FCE' }">
        <template #prefix>
          <icon-arrow-rise />
        </template>
      </a-statistic>
      <a-statistic title="提交总数" :value="50.52" :precision="2" :value-style="{ color: '#2980B9' }">
        <template #prefix>
          <icon-arrow-rise />
        </template>
      </a-statistic>
      <a-statistic title="通过率" :value="50.52" :precision="2" :value-style="{ color: '#0fbf60' }">
        <template #prefix>
          <icon-arrow-rise />
        </template>
      </a-statistic>
    </div>
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
import { log } from "three";

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


const loadData = async () => {
  console.log(store.state.user?.loginUser?.id);
  const res = await QuestionControllerService.listLatestByUserId(
    store.state.user?.loginUser?.id as number
  );
  console.log(res);

  if (res.code === 0) {
    dataList.value = res.data;
    const data = [] as any
    dataList.value.forEach(item => {
          data.push({
            createTime:item.updateTime,
            questionSubmitStatus:item.questionVO.questionSubmitStatus,
            title:item.questionVO.title})
    })
    dataList.value = data
  } else {
    message.error("加载失败" + res.message);
  }
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


.question_submit_list {
  max-width: 800px; /* 设置最大宽度 */
  width: 100%; /* 宽度自适应 */
  margin: 0 auto; /* 上下外边距为0，左右外边距自动，实现水平居中 */
}
</style>
