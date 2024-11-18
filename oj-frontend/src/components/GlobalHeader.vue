<template>
  <a-row id="globalHeader" :wrap="false" align="center">
    <a-col flex="auto">
      <a-menu
        :default-selected-keys="['1']"
        :selected-keys="selectedKeys"
        mode="horizontal"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" />
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <!-- 如果用户已登录，显示用户名和注销按钮 -->
      <div v-if="store.state.user?.loginUser?.userName" class="user-profile">
        <div @click="toUserInfo" class="user-avatar">
          <a-avatar
            :image-url="store.state.user?.loginUser?.userAvatar"
          ></a-avatar>
          <span>{{ store.state.user?.loginUser?.userName }}</span>
        </div>
        <a-button
          type="dashed"
          status="danger"
          @click="logOut"
          class="logout-button"
          >注销
        </a-button>
      </div>

      <!-- 如果用户未登录，显示登录按钮 -->
      <a-button v-else type="outline" @click="goToLogin">登录</a-button>
    </a-col>
  </a-row>
</template>

<script lang="ts" setup>
import { routes } from "@/router/router";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";
import { UserControllerService } from "../../generated";

const router = useRouter();
const store = useStore();

const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    if (
      !checkAccess(store.state.user.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    return true;
  });
});

const selectedKeys = ref(["/"]);

router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({ path: key });
};

const toUserInfo = () => {
  router.push({
    path: "/user/info",
    query: { id: store.state.user?.loginUser?.id },
  });
};

const goToLogin = () => {
  router.push({ path: "/user/login" });
};

const logOut = () => {
  UserControllerService.userLogoutUsingPost();
};
</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.logo {
  height: 48px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px; /* 设置头像和用户名、注销按钮之间的间距 */
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px; /* 设置头像和用户名的间距 */
}

.user-avatar a-avatar {
  margin-right: 8px; /* 设置头像和用户名之间的间距 */
}

.logout-button {
  margin-left: 8px; /* 设置注销按钮和用户名之间的间距 */
}
</style>
