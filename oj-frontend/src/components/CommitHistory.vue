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
                @click="getCode(commit)"
            >
              <span class="timeline-content success">通过</span>
              <a-tag color="arcoblue">{{ commit.language }}</a-tag>
            </a-timeline-item>
            <a-timeline-item
                v-else-if="commit.status === 2 ||commit.status === 1"
                dotColor="#F53F3F"
                :label="moment(commit.updateTime).format('YYYY-MM-DD HH:mm:ss')"
                @click="getCode(commit)"
            >
              <span class="timeline-content failure">失败</span>
              <a-tag color="arcoblue">{{ commit.language }}</a-tag>
            </a-timeline-item>
          </template>

        </div>
      </div>
    </a-scrollbar>

    <a-drawer :width="1200" :visible="visible" @ok="handleOk" @cancel="handleCancel" unmountOnClose>
      <template #title>
        判题结果
      </template>
      <div class="content">
        <CodeEditor :value="codeStr || ''" :read="true" style="width: 600px" />
        <div class="statistics">
          <a-statistic
              title="时间消耗（ms）"
              :value="JSON.parse(time)"
              :value-from="0"
              :start="numStart"
              animation
              style="background: #c0d0c7; border-radius: 10px; padding: 15px; margin: 10px;"
          />
          <a-statistic
              title="内存消耗（b）"
              :value="JSON.parse(memory)"
              :precision="2"
              :value-from="0"
              :start="numStart"
              animation
              style="background: #f1d8d8; border-radius: 10px; padding: 15px; margin: 10px;"
          />
        </div>
      </div>
      <a-affix :offsetBottom="120" align="right">
        <a-button status="success" shape="round" @click="toSendAnswerView">发布题解</a-button>
      </a-affix>
    </a-drawer>
  </a-timeline>
</template>

<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import message from "@arco-design/web-vue/es/message";
import {QuestionControllerService, QuestionSubmitQueryRequest, QuestionSubmitVO} from "../../generated";
import store from "@/store";
import moment from "moment";
import CodeEditor from "@/components/CodeEditor.vue";
import {useRouter} from "vue-router";

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
const numStart = ref<boolean>(false)
const time = ref<number>(0)
const memory = ref<number>(0)
const getCode = (commit: QuestionSubmitVO) => {
  visible.value = true;
  if (commit.code != null) {
    codeStr.value = commit.code;
  }
  if (commit.status == 3) {
    time.value = commit.judgeInfo?.time
    memory.value = commit.judgeInfo?.memory
  }
  console.log("time.value::", time.value);
  console.log("memory.value::", memory.value);
  numStart.value = true;
};
const router = useRouter();
const toSendAnswerView = () => {
  const paramsCode = codeStr.value;
  router.push({
    path: `/answer/send/${props.id}`,
    query: {paramsCode}
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



.content {
  display: flex;
  gap: 20px;
  align-items: flex-start;
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
