<template>
  <div id="app">
    <BasicLayout />
  </div>
</template>

<style>
#app {
}
</style>

<script lang="ts" setup>
import BasicLayout from "@/layouts/BasicLayout.vue";

import { onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
/**
 * 全局初始化函数，有全局单词调用的代码，都可以写这里
 */
const doInit = () => {
  console.log("hello 欢迎来到我的项目");
};

onMounted(() => {
  doInit();
});
</script>
