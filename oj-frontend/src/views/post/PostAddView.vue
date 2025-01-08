<template>
  <a-form :model="form">
    <a-form-item field="title">
      <a-input v-model="form.title" placeholder="在此处输入标题" />
    </a-form-item>
    <a-form-item field="context">
      <a-textarea placeholder="在此处输入内容" v-model="form.content" :value="form.content" :style="{ height: '200px' }" />
    </a-form-item>
    <a-form-item field="tags">
      <a-input-tag
        v-model="form.tags"
        :value="form.tags"
        allow-clear
        placeholder="在此处输入标签"
      />
    </a-form-item>

    <a-form-item>
      <a-button type="primary" @click="doSubmit">发布</a-button>
    </a-form-item>
  </a-form>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { PostControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const router = useRouter();

const form = reactive({
  content: "",
  tags: [],
  title: ""
});

const doSubmit = async () => {
  const res = await PostControllerService.addPostUsingPost(form);
  if (res.code === 0) {
    message.success("发布成功");
    router.go(-1);
  } else {
    message.error("发布失败：" + res.message);
  }
};
</script>

<style scoped>
#addQuestionView {
}
</style>
