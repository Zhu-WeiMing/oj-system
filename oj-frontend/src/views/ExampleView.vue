<template>
  <div class="example">
    <MdEditor :handle-change="onMdChange" :value="mdValue" />
    {{ codeValue }}
    <CodEditor :handle-change="onCodeChange" :value="codeValue" />
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import CodEditor from "@/components/CodeEditor.vue";

const mdValue = ref();
const codeValue = ref();
const onMdChange = (v: string) => {
  mdValue.value = v;
};
const onCodeChange = (v: string) => {
  codeValue.value = v;
};
</script>
