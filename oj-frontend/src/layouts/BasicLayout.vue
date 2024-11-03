<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://github.com/Zhu-WeiMing/OJ" target="_blank"
          >OJ判题系统 by zhuweiming</a
        >
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout {
}

#basicLayout .header {
  margin-bottom: 16px;
  box-shadow: #eee 1px 1px 5px;
}

#basicLayout .content {
  background: linear-gradient(to right, #efefef, #fff);
  margin-bottom: 16px;
  padding: 20px;
}

#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  position: sticky;

  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
<script>
import { defineComponent } from "vue";
import GlobalHeader from "@/components/GlobalHeader.vue";

export default defineComponent({
  components: { GlobalHeader },
});
</script>
