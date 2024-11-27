<template>
  <div class="viewQuestionView">
    <a-row :gutter="[24, 24]">
      <!--    左侧标签页面-->
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="question">
          <a-tab-pane key="question" title="题目详情">
            <a-card v-if="question" :title="question.title">
              <a-descriptions title="判题条件" :colum="{ xs: 1, md: 2, lg: 3 }">
                <a-descriptions-item label="时间限制">
                  {{ question.judgeConfig?.timeLimit }}
                </a-descriptions-item>
                <a-descriptions-item label="内存限制">
                  {{ question.judgeConfig?.memoryLimit }}
                </a-descriptions-item>
                <a-descriptions-item label="堆栈限制">
                  {{ question.judgeConfig?.stackLimit }}
                </a-descriptions-item>
              </a-descriptions>
            </a-card>
            <a-card v-if="question">
              <MdViewer :value="question.content || ''" />
            </a-card>
            <template #extra>
              <a-space wrap>
                <a-tag
                  v-for="(tag, index) of question?.tags"
                  :key="index"
                  color="green"
                >{{ tag }}
                </a-tag>
              </a-space>
            </template>
          </a-tab-pane>
          <a-tab-pane key="comment" title="题解">
            <CommentList :id="question?.id" />
          </a-tab-pane>
          <a-tab-pane key="answer" title="题解">
            <template #title>Tab 3</template>
            Content of Tab Panel 3
          </a-tab-pane>
        </a-tabs>
        <!--        右侧代码编辑器-->
      </a-col>
      <a-col :md="12" :xs="24">
        <a-form :model="form" layout="inline">
          <a-form-item field="title" label="编程语言" style="min-width: 240px">
            <a-select
              v-model="form.language"
              :style="{ width: '320px' }"
              placeholder="请选择编程语言"
            >
              <a-option>java</a-option>
              <a-option>cpp</a-option>
              <a-option>go</a-option>
            </a-select>
          </a-form-item>
        </a-form>
        <CodeEditor
          :value="form.code"
          :language="form.language"
          :handle-change="changeCode"
        />
        <a-button type="primary" style="min-width: 200px" @click="doSubmit"
        >提交代码
        </a-button>
        <a-button type="dashed" style="min-width: 200px" @click="router.go(-1)"
        >返回
        </a-button>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import { QuestionControllerService, QuestionSubmitAddRequest, QuestionVO } from "../../../generated";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import { useRouter } from "vue-router";
import CommentList from "@/components/AnswerList.vue";

const router = useRouter();

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => ""
});
const question = ref<QuestionVO>();

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
  } else {
    message.error("加载失败" + res.message);
  }
};
const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code: "public class Main {\n" +
    "    public static void main(String[] args) {\n" +
    "        //TODO 做题\n" +
    "    }\n" +
    "}"
});
/**
 * 提交代码
 */
const doSubmit = async () => {
  if (!question.value?.id) {
    return;
  }
  const res = await QuestionControllerService.doSubmitQuestionUsingPost({
    ...form.value,
    questionId: question.value.id
  });
  if (res.code === 0) {
    message.success("提交成功");
  } else {
    message.error("提交失败" + res.message);
  }
};
/**
 * 页面加载时 请求数据
 */
onMounted(() => {
  loadData();
});

const changeCode = (value: string) => {
  form.value.code = value;
};
</script>

<style scoped>
#managerQuestionView {
}
</style>
