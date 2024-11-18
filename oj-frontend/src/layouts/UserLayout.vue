<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <!-- 使用 CSS 类来对按钮进行样式调整 -->
        <a-button
          type="primary"
          shape="round"
          class="go-back-button"
          @click="goBack"
          >返回
        </a-button>
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

<script lang="ts" setup>
import { useRouter } from "vue-router";

// 使用 useRouter 钩子
const router = useRouter();

// 定义返回上一页的方法
const goBack = () => {
  router.back();
};
</script>

<style scoped>
#userLayout {
  text-align: center;
  background: #ffffff;
}

#userLayout .header {
  margin-bottom: 16px;
  box-shadow: #eee 1px 1px 5px;
  display: flex;
  justify-content: flex-start; /* 使头部内容靠左对齐 */
  align-items: center; /* 垂直居中 */
}

#userLayout .content {
  background: #ffffff;
  margin-bottom: 16px;
  padding: 20px;
}

#userLayout .footer {
  background: #ffffff;
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

.go-back-button {
  margin-top: 10px;
  margin-left: 16px; /* 为返回按钮添加左边距 */
}
</style>
