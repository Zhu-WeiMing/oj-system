<template>
  <div class="viewQuestionView" style="height: 500px">
    <div class="loading" v-if="isLoading ==true">
      <a-spin dot class="centered-spin"/>
      正在判题中，请稍后。您稍后也可以去个人提交记录中查看结果。
      <div>
        <a-button type="outline" @click="ev => isLoading = false">关闭</a-button>
      </div>

    </div>
    <a-row :gutter="[24, 24]">
      <!--    左侧标签页面-->
      <a-col :md="12" :xs="24">
        <a-scrollbar style="height: 500px; overflow: auto;">
          <a-tabs default-active-key="question">
            <a-tab-pane key="question" title="题目内容">
              <a-card v-if="question" :title="question.title">
                <a-descriptions title="判题条件" :colum="{ xs: 1, md: 2, lg: 3 }">
                  <a-descriptions-item label="时间限制(ms)">
                    {{ question.judgeConfig?.time }}
                  </a-descriptions-item>
                  <a-descriptions-item label="内存限制(b)">
                    {{ question.judgeConfig?.memory }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-card>
              <a-card v-if="question">
                <MdViewer :value="question.content || ''"/>
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
            <a-tab-pane key="answer" title="题解" v-if="question">
              <CommentList :id="question.id"/>
            </a-tab-pane>
            <a-tab-pane key="commitHistory" title="提交记录" v-if="question">
              <CommitHistory :id="question.id"/>
            </a-tab-pane>
          </a-tabs>
        </a-scrollbar>
      </a-col>

      <!--        右侧代码编辑器-->
      <a-col :md="12" :xs="24">
        <!--        <a-form :model="form" layout="inline">-->
        <!--          <a-form-item field="title" label="编程语言" style="min-width: 240px">-->
        <!--            <a-select-->
        <!--                v-model="form.language"-->
        <!--                :style="{ width: '320px' }"-->
        <!--                placeholder="请选择编程语言"-->
        <!--            >-->
        <!--              <a-option>java</a-option>-->
        <!--              <a-option>cpp</a-option>-->
        <!--              <a-option>go</a-option>-->
        <!--            </a-select>-->
        <!--          </a-form-item>-->
        <!--        </a-form>-->
        <CodeEditor
            :value="form.code"
            :language="form.language"
            :handle-change="changeCode"
            style="height: 500px"
        />
        <a-button type="primary" style="min-width: 200px" @click="doSubmit"
        >提交代码
        </a-button>
        <a-button type="dashed" style="min-width: 200px" @click="router.go(-1)"
        >返回
        </a-button>
      </a-col>
    </a-row>
    <a-drawer :width="1200" :visible="visible" @ok="handleOk" @cancel="handleCancel" unmountOnClose>
      <template #title>
        详情
      </template>
      <div class="content">
        <CodeEditor :value="codeStr || ''" :read="true" style="width: 600px"/>
        <div>
          <div class="statistics">
            <a-statistic
                title="时间消耗（ms）"
                :value="time"
                :value-from="0"
                :start="numStart"
                animation
                style="background: #c0d0c7; border-radius: 10px; padding: 15px; margin: 10px; width: 250px"
            />
            <a-statistic
                title="内存消耗（b）"
                :value="memory"
                :value-from="0"
                :start="numStart"
                animation
                style="background: #f1d8d8; border-radius: 10px; padding: 15px; margin: 10px;"
            />
          </div>
          <div class="result">
            <div v-if="submitStatus  == 2">
              <a-tag color="red" size="large">{{ "不通过：\n" + resultMeg }}</a-tag>
              <!--              <img src="../../assets/未通过.png" alt="不通过" class="status-icon-image"/>-->
            </div>
            <div v-if="submitStatus  == 3">
              <a-tag color="green" size="large">{{ "通过：\n" + resultMeg }}</a-tag>
              <!--              <img src="../../assets/通过.png" alt="通过" class="status-icon-image"/>-->
            </div>
          </div>
        </div>
      </div>

      <a-affix :offsetBottom="120" align="right">
        <a-button status="success" shape="round" @click="toSendAnswerView">发布题解</a-button>
      </a-affix>
    </a-drawer>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from "vue";
import message from "@arco-design/web-vue/es/message";
import {QuestionControllerService, QuestionSubmitAddRequest, QuestionVO} from "../../../generated";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import {useRouter} from "vue-router";
import CommentList from "@/components/AnswerList.vue";
import CommitHistory from "@/components/CommitHistory.vue";
import {QuestionSubmit} from "../../../generated/models/QuestionSubmit";

const router = useRouter();
const isLoading = ref<boolean>(false);

interface Props {
  id: string;
}

const toSendAnswerView = () => {
  const paramsCode = codeStr.value;
  router.push({
    path: `/answer/send/${props.id}`,
    query: {paramsCode}
  });
};
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
  code: "import java.util.Scanner;\n" +
      "\n" +
      "\n" +
      "public class Main {\n" +
      "\n" +
      "    public static void main(String[] args) \n" +
      "    {\n" +
      "        Scanner scanner = new Scanner(System.in);\n" +
      "        \n" +
      "        while (scanner.hasNextLine()) {\n" +
      "        \n" +
      "            String line = scanner.nextLine();\n" +
      "            \n" +
      "            String[] parts = line.trim().split(\" \");\n" +
      "            // todo 解题\n" +
      "    \n" +
      "            \n" +
      "        }\n" +
      "        \n" +
      "        scanner.close();\n" +
      "        \n" +
      "    }\n" +
      "\n" +
      "\n" +
      "}"
});
/**
 * 提交代码
 */
const submitId = ref<number>(null)
const doSubmit = async () => {
  if (!question.value?.id) {
    return;
  }
  if (form.value.code === "") {
    message.error("代码内容不能为空！")
    return;
  }
  const res = await QuestionControllerService.doSubmitQuestionUsingPost({
    ...form.value,
    questionId: question.value.id
  });
  submitId.value = res.data
  if (res.code === 0) {
    isLoading.value = true;
    message.success("提交成功");
    const res2 = await QuestionControllerService.getfinishQuestionDetail(submitId.value)
    submitStatus.value = res2.data?.status;

    const data = JSON.parse(res2.data?.judgeInfo)
    resultMeg.value = data.message
    console.log("resultMeg.value:::" + resultMeg.value)
    getCode(res2.data)
  } else {
    message.error("提交失败" + res.message);
  }
};

const handleCancel = () => {
  visible.value = false;
};
const handleOk = () => {
  visible.value = false;
};
const visible = ref<boolean>(false);
const codeStr = ref<string>("");
const numStart = ref<boolean>(false)
const time = ref<number>(0)
const memory = ref<number>(0)
const submitStatus = ref<number>(null);
const resultMeg = ref<string>(null);
const getCode = (commit: QuestionSubmit) => {
  isLoading.value = false
  visible.value = true;
  numStart.value = true;
  if (commit.code != null) {
    codeStr.value = commit.code;
  }
  if (commit.status === 2 || commit.status === 3) {
    const judgeInfoData = JSON.parse(commit.judgeInfo)
    time.value = judgeInfoData.time
    memory.value = judgeInfoData.memory
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

.loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.centered-spin {
  display: flex;
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
