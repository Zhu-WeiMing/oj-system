<template>
  <div id="questionView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="title" label="名称" style="min-width: 280px">
        <a-input v-model="searchParams.title" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 280px">
        <a-input-tag v-model="searchParams.tags" placeholder="请输入标签" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </a-form-item>
    </a-form>

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

      <template #questionSubmitStatus="{ record }">
        <a-space size="mini">
          <a-tooltip v-if="record.questionSubmitStatus===3" content="已解答" background-color="#3491FA" mini>
            <a-progress type="circle" :percent="100" size="mini" />
          </a-tooltip>
          <a-tooltip v-else-if="record.questionSubmitStatus===2" content="尝试过" background-color="#3491FA" mini>
            <a-progress type="circle" status='danger' :percent="100" size="mini" />
          </a-tooltip>

        </a-space>
      </template>

      <template #tags="{ record }">
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
          <a-button
            v-if="store.state.user?.loginUser?.userRole != ASSESS_ENUM.ADMIN"
            type="primary"
            @click="toQuestionPage(record)"
          >做题
          </a-button>

          <a-button
            v-if="store.state.user?.loginUser?.userRole === ASSESS_ENUM.ADMIN"
            type="primary"
            @click="doUpdate(record)"
          >修改
          </a-button>
          <a-button
            v-if="store.state.user?.loginUser?.userRole === ASSESS_ENUM.ADMIN"
            type="outline"
            @click="doLook(record)"
          >查看
          </a-button>
          <a-button
            v-if="store.state.user?.loginUser?.userRole === ASSESS_ENUM.ADMIN"
            status="danger"
            @click="doDelete(record)"
          >删除
          </a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { useStore } from "vuex";
import { onMounted, ref, watchEffect } from "vue";
import { Question, QuestionControllerService, QuestionQueryRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";
import ASSESS_ENUM from "@/access/accessEnum";

const store = useStore();

const router = useRouter();
const show = ref(true);

const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  pageSize: 10,
  current: 1
});

const handleSearch = () => {
  searchParams.value.current = 1; // 重置页码为1
  loadData();
};

const loadData = async () => {
  console.log("loadData0000");
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    console.log(res.data.records);
    total.value = res.data.total;
  } else {
    message.error("加载失败" + res.message);
  }
};

/**
 * 跳转到题目做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`
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
    current: page
  };
};
const doUpdate = (question: Question) => {
  router.push({
    path: "/update/question",
    query: {
      id: question.id
    }
  });
};

const doDelete = async (question: Question) => {
  const res = await QuestionControllerService.deleteQuestionUsingPost({
    id: question.id
  });
  if (res.code === 0) {
    message.success("删除成功");
    //更新数据
    loadData();
  } else {
    message.error("删除失败" + res.message);
  }
};

const doLook = async (question: Question) => {
  router.push({
    path: "/info/question",
    query: {
      id: question.id
    }
  });
};

/**
 * {id: "1821174644019994625", title: "标题测试", content: "题目测试", tags: [], submitNum: 0, acceptedNum: 0,…}
 */

const columns = [
  {
    title: "状态",
    slotName: "questionSubmitStatus"
  },
  {
    title: "题号",
    dataIndex: "id"
  },
  {
    title: "标题",
    dataIndex: "title"
  },
  {
    title: "标签",
    slotName: "tags"
  },
  {
    title: "通过率",
    slotName: "acceptedRate"
  },
  {
    title: "点赞数",
    dataIndex: "thumbNum"
  },
  {
    title: "收藏数",
    dataIndex: "favourNum"
  },

  {
    title: "创建时间",
    slotName: "createTime"
  },
  {
    title: "操作",
    slotName: "optional"
  }
];
</script>

<style scoped>
#managerQuestionView {
}
</style>
