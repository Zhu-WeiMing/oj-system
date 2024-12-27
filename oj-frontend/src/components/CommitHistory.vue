<template>

  <a-timeline>
    <a-scrollbar style="height:400px;overflow: auto;">
      <div style="height:auto;width: auto">
        <div v-if="commitHistory && commitHistory.length > 0">
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

        </div>
      </div>
    </a-scrollbar>

    <a-drawer :width="600" :visible="visible" @ok="handleOk" @cancel="handleCancel" unmountOnClose>
      <template #title>
        详情
      </template>
      <CodeEditor :value="codeStr ||''" :read="true" />
      <a-affix :offsetBottom="120" align="right">
        <a-button status="success" shape="round" @click="toSendAnswerView">发布题解</a-button>
      </a-affix>
    </a-drawer>
  </a-timeline>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import { QuestionControllerService, QuestionSubmitQueryRequest, QuestionSubmitVO } from "../../generated";
import store from "@/store";
import moment from "moment";
import CodeEditor from "@/components/CodeEditor.vue";
import { useRouter } from "vue-router";

interface Props {
  id: number;
}

const props = defineProps<Props>();
const commitHistory = ref<QuestionSubmitVO[]>([]);

onMounted(() => {
  loadData();
});
const handleCancel = () => {
  visible.value = false;
};
const handleOk = () => {
  visible.value = false;
};
const loadData = async () => {
  const from: QuestionSubmitQueryRequest = {
    questionId: props.id,
    userId: store.state.user.loginUser.userId
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
const visible = ref<boolean>(false);
const codeStr = ref<string>("");
const getCode = (code: string) => {
  visible.value = true;
  codeStr.value = code;
};
const router = useRouter();
const toSendAnswerView = () => {
  const paramsCode = codeStr.value;
  router.push({
    path: `/answer/send/${props.id}`,
    query: { paramsCode }
  });
};
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
