<template>
  <div id="questionView">
    <!--    TODO 点击查询按钮后才查询 让页码等于1-->
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="title" label="名称" style="min-width: 280px">
        <a-input v-model="searchParams.title" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 280px">
        <a-input-tag v-model="searchParams.tags" placeholder="请输入标签" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary">查询</a-button>
      </a-form-item>
    </a-form>

    <!--    分割线-->
    <a-divider size="0" />

    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
        showTotal: true,
      }"
      @page-change="onPageChange"
    >
      <template #tag="{ record }">
        <a-space wrap>
          <a-tag v-for="(tag, index) in record.tags" :key="index" color="green"
            >{{ tag }}
          </a-tag>
        </a-space>
      </template>

      <template #acceptedRate="{ record }">
        {{
          `${
            record.submitNum ? record.acceptedNum / record.submitNum : "0"
          }% (${record.acceptedNum / record.submitNum})`
        }}
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>

      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="toQuestionPage(record)"
            >做题
          </a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

const show = ref(true);

const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败" + res.message);
  }
};
const router = useRouter();
/**
 * 跳转到题目做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

watchEffect(() => {
  loadData();
});

/**
 * 页面加载时 请求数据
 */
onMounted(() => {
  loadData();
});

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * {id: "1821174644019994625", title: "标题测试", content: "题目测试", tags: [], submitNum: 0, acceptedNum: 0,…}
 */

const columns = [
  {
    title: "题号",
    dataIndex: "id",
  },
  {
    title: "标题",
    dataIndex: "title",
  },
  {
    title: "标签",
    slotName: "tags",
  },
  {
    title: "通过率",
    slotName: "acceptedRate",
  },
  {
    title: "点赞数",
    dataIndex: "thumbNum",
  },
  {
    title: "收藏数",
    dataIndex: "favourNum",
  },

  {
    title: "创建时间",
    slotName: "createTime",
  },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>

<style scoped>
#managerQuestionView {
}
</style>
