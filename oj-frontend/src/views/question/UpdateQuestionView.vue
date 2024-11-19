<template>
  <div id="addQuestionView">创建题目</div>
  <a-form :model="form">
    <a-form-item field="answer" label="答案">
      <MdEditor :handle-change="onAnswerChange" :value="form.answer" />
    </a-form-item>
    <a-form-item field="context" label="题目内容">
      <MdEditor :handle-change="onContextChange" :value="form.content" />
    </a-form-item>
    <a-form-item field="title" label="标题">
      <a-input v-model="form.title" placeholder="请输入标题" />
    </a-form-item>
    <a-form-item field="tags" label="标签">
      <a-input-tag
        v-model="form.tags"
        :value="form.tags"
        allow-clear
        placeholder="请选择标签"
      />
    </a-form-item>

    <a-form-item :content-flex="false" :merge-props="false" label="判题配置">
      <a-space direction="vertical">
        <a-form-item
          field="judgeConfig.memoryLimit"
          label="memoryLimit"
          style="min-width: 480px"
        >
          <a-input-number
            v-model="form.judgeConfig.memoryLimit"
            mode="button"
            placeholder="请输入内存消耗"
            size="large"
          />
        </a-form-item>

        <a-form-item
          field="judgeConfig.stackLimit"
          label="stackLimit"
          style="min-width: 480px"
        >
          <a-input-number
            v-model="form.judgeConfig.stackLimit"
            mode="button"
            placeholder="请输入堆栈消耗"
            size="large"
          />
        </a-form-item>

        <a-form-item
          field="judgeConfig.timeLimit"
          label="timeLimit"
          style="min-width: 480px"
        >
          <a-input-number
            v-model="form.judgeConfig.timeLimit"
            mode="button"
            placeholder="请输入时间消耗"
            size="large"
          />
        </a-form-item>
      </a-space>
    </a-form-item>

    <a-form-item :content-flex="false" :merge-props="false" label="测试用例">
      <a-form-item
        v-for="(judgeCase, index) of form.judgeCase"
        :key="index"
        align="left"
      >
        <a-form-item
          :key="index"
          :field="`form.judgeCase[${index}].input`"
          :label="`输入用例-${index}`"
        >
          <a-input v-model="judgeCase.input" placeholder="请输入测试输入用例" />
        </a-form-item>

        <a-form-item
          :key="index"
          :field="`form.judgeCase[${index}].out`"
          :label="`输出用例-${index}`"
        >
          <a-input
            v-model="judgeCase.output"
            placeholder="请输入测试输出用例"
          />
        </a-form-item>

        <a-button
          :style="{ marginLeft: '10px' }"
          status="danger"
          @click="handleDelete(index)"
          >删除
        </a-button>
      </a-form-item>
      <div>
        <a-button status="success" type="outline" @click="handleAdd"
          >添加测试用例
        </a-button>
      </div>
    </a-form-item>

    <a-form-item>
      <a-button type="primary" @click="doSubmit">提交</a-button>
      <a-button type="dashed" @click="router.go(-1)">返回</a-button>
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService, QuestionVO } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute } from "vue-router";
import { useRouter } from "vue-router";

const router = useRouter();

const form = ref({
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    memoryLimit: 0,
    stackLimit: 0,
    timeLimit: 0,
  },
  tags: [],
  title: "",
} as any);

/**
 * 根据题目id获取老的数据
 */
const userrouter = useRoute();
const loadDate = async () => {
  const id = userrouter.query.id;
  if (!id) {
    return;
  }
  const res = (await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  )) as any;
  if (res.code === 0) {
    form.value = res.data;
    form.value.judgeCase = JSON.parse(res.data.judgeCase);
    form.value.judgeConfig = JSON.parse(res.data.judgeConfig);
    form.value.tags = JSON.parse(res.data.tags);
  } else {
    message.error("加载失败" + res.message);
  }
};
/**
 * 加载页面
 */
onMounted(() => {
  loadDate();
});

const doSubmit = async () => {
  const res = await QuestionControllerService.updateQuestionUsingPost(
    form.value
  );
  if (res.code === 0) {
    message.success("更新成功");
  } else {
    message.error("更新失败：" + res.message);
  }
};
/**
 * 新增测试用例
 */
const handleAdd = () => {
  if (form.value.judgeCase) {
    form.value.judgeCase.push({
      input: "",
      output: "",
    });
  }
};
/**
 * 删除测试用例
 */
const handleDelete = (index: number) => {
  if (form.value.judgeCase) {
    form.value.judgeCase.splice(index, 1);
  }
};
const onAnswerChange = (v: string) => {
  form.value.answer = v;
};
const onContextChange = (v: string) => {
  form.value.content = v;
};
</script>

<style scoped>
#addQuestionView {
}
</style>
