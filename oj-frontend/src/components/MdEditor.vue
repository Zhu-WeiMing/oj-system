<template>
  <Editor
    :mode="mode"
    :plugins="plugins"
    :value="value"
    @change="handleChange"
  />
</template>

<script lang="ts" setup>
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import { Editor } from "@bytemd/vue-next";
import { defineProps, withDefaults } from "vue";

// 父组件传入value
interface Props {
  value: string;
  mode?: string;
  handleChange: (value: string) => void;
}

const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
];

//如果父组件没传value 默认值
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  mode: () => "split",
  // handleChange: (v: string) => {},
});
</script>

<style scoped></style>
