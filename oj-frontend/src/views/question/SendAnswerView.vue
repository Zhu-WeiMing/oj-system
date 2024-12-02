<template>
  <a-form :model="form">
    <a-form-item field="title" label="标题">
      <a-input v-model="form.title" placeholder="请输入标题" />
    </a-form-item>
    <a-form-item field="context" label="内容">
      <MdEditor :handle-change="onContextChange" :value="form.content" />
    </a-form-item>


    <a-form-item>
      <a-button type="primary" @click="doSubmit">提交</a-button>
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { AnswerControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const router = useRouter();

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => ""
});
const form = reactive({
  questionId: props.id,
  content: "# 思路\n" +
    "\n" +
    "> 你选用何种方法解题？\n" +
    "\n" +
    "# 解题过程\n" +
    "\n" +
    "> 这些方法具体怎么运用？\n" +
    "\n" +
    "# Code\n",
  title: ""
});

const doSubmit = async () => {
  const res = await AnswerControllerService.postAnswerUsingPost(form);
  if (res.code === 0) {
    message.success("添加成功");
    router.go(-1);
  } else {
    message.error("创建失败：" + res.message);
  }
};
const onContextChange = (v: string) => {
  form.content = v;
};
</script>

<style scoped>
#addQuestionView {
}
</style>
