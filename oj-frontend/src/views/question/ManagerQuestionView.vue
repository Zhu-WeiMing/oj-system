<template>
  <div id="managerQuestionView">
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
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="doUpdate(record)">修改</a-button>
          <a-button status="danger" @click="doDelete(record)">删除</a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watchEffect } from "vue";
import { Question, QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const show = ref(true);

const dataList = ref([]);
const total = ref(0);
const searchParams = ref({
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listUserByPageUsingPost(
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
const doUpdate = (question: Question) => {
  router.push({
    path: "/update/question",
    query: {
      id: question.id,
    },
  });
};
const doDelete = async (question: Question) => {
  const res = await QuestionControllerService.deleteQuestionUsingPost({
    id: question.id,
  });
  if (res.code === 0) {
    message.success("删除成功");
    //更新数据
    loadData();
  } else {
    message.error("删除失败" + res.message);
  }
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
    title: "id",
    dataIndex: "id",
  },
  {
    title: "标题",
    dataIndex: "title",
  },
  {
    title: "题目",
    dataIndex: "content",
  },
  {
    title: "标签",
    dataIndex: "tags",
  },
  {
    title: "提交数量",
    dataIndex: "submitNum",
  },
  {
    title: "通过数量",
    dataIndex: "acceptedNum",
  },
  {
    title: "答案",
    dataIndex: "answer",
  },
  {
    title: "判题配置",
    dataIndex: "judgeConfig",
  },
  {
    title: "判题用例",
    dataIndex: "judgeConfig",
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
    title: "创建者",
    dataIndex: "userId",
  },

  {
    title: "创建时间",
    dataIndex: "createTime",
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
