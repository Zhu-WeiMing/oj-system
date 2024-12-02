<template>
  <a-affix :offsetBottom="120" align="right">
    <a-button status="success" shape="round" @click="toSendAnswerView">发布题解</a-button>
  </a-affix>
  <a-divider />
  <div v-if="answers && answers.length > 0">
    <a-comment
      v-for="answer in answers"
      :key="answer.id"
      align="right"
      :author="answer.userName"
      :avatar="answer.userAvatar"
      :content="answer.content"
      :datetime="answer.createTime"
    >
      <!--     todo 使用 MdViewer 组件来显示 Markdown 内容 -->
      <template #content v-if="answers">
        <MdViewer :value="answer.content || ''" />
      </template>

    </a-comment>
  </div>
  <template v-else-if="answers.length === 0">
    <a-empty />
  </template>


</template>

<script lang="ts" setup>
import { defineProps, onMounted, ref } from "vue";
import { AnswerControllerService, AnswerGetVo } from "../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import MdViewer from "@/components/MdViewer.vue";

interface Props {
  id: number;
}


const props = defineProps<Props>();
const answers = ref<AnswerGetVo[]>([]); // 定义一个响应式数据属性来存储后端返回的数据

onMounted(() => {
  loadData();
});
const loadData = async () => {
  const res = await AnswerControllerService.getByQuestionIdUsingGet(props.id);
  if (res.code === 0) {
    answers.value = res.data;
  } else {
    message.error("加载失败" + res.message);
  }
};
const router = useRouter();
const toSendAnswerView = () => {
  router.push({
    path: `/answer/send/${props.id}`
  });
};

</script>

<style scoped>
.action {
  display: inline-block;
  padding: 0 4px;
  color: var(--color-text-1);
  line-height: 24px;
  background: transparent;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.1s ease;
}

.action:hover {
  background: var(--color-fill-3);
}
</style>
