<template>
  <a-timeline>
    <template v-for="commit in commitHistory" :key="commit.id">
      <a-timeline-item
        v-if="commit.status === 3"
        dotColor="#00B42A"
        :label="moment(commit.updateTime).format('YYYY-MM-DD HH:mm:ss')"
        @click="getCode(commit.code)"
      >
        <span class="timeline-content success">通过</span>
      </a-timeline-item>
      <a-timeline-item
        v-else-if="commit.status === 2"
        dotColor="#F53F3F"
        :label="moment(commit.updateTime).format('YYYY-MM-DD HH:mm:ss')"
        @click="getCode(commit.code)"
      >
        <span class="timeline-content failure">失败</span>
      </a-timeline-item>
    </template>
  </a-timeline>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import {
  QuestionControllerService,
  QuestionSubmitQueryRequest,
  QuestionSubmitVO,
} from "../../generated";
import store from "@/store";
import moment from "moment";

interface Props {
  id: number;
}

const props = defineProps<Props>();
const commitHistory = ref<QuestionSubmitVO[]>([]);

onMounted(() => {
  loadData();
});

const loadData = async () => {
  const from: QuestionSubmitQueryRequest = {
    questionId: props.id,
    userId: store.state.user.loginUser.userId,
  };
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    from
  );
  if (res.code === 0) {
    commitHistory.value = res.data.records;
  } else {
    message.error("加载失败" + res.message);
  }
};
const getCode = (code: string) => {
  store.state.code.codeList = code
  store.state.code.isCode = true
}
</script>

<style scoped>
.timeline-content {
  display: inline-block;
}

.success {
  color: #00b42a; /* 绿色字体 */
}

.failure {
  color: #f53f3f; /* 红色字体 */
}
</style>
