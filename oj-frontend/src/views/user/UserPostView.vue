<template>
  <div class="container">
    <div class="menu-demo">
      <a-menu mode="popButton">
        <a-menu-item key="1" @click="toUserInfo">
          <template #icon>
            <icon-user></icon-user>
          </template>
          个人资料
        </a-menu-item>
        <a-menu-item key="2" @click="toUserProgress">
          <template #icon>
            <icon-bulb></icon-bulb>
          </template>
          进展分析
        </a-menu-item>
        <a-menu-item key="2" @click="toUserPost">
          <template #icon>
            <icon-message />
          </template>
          我的讨论
        </a-menu-item>
      </a-menu>
    </div>

    <a-scrollbar style="height:500px;width:1000px;overflow: auto;">
      <div class="new_post" v-if="dataList && dataList.length > 0">
        <a-comment
          v-for="data in dataList"
          :key="data.id"
          :content="data.content"
          :datetime="formattedDateTime(data.createTime as string) "
        >
          <div class="tag-container">
            <!-- 遍历 tagList 数组，为每个标签单独渲染一个 <a-tag> -->
            <div v-for="tag in data.tagList" :key="tag">
              <a-tag color="arcoblue">{{ tag }}</a-tag>
            </div>
          </div>
          <template #actions>
      <span class="action" key="heart">
          <IconHeartFill :style="{ color: '#f53f3f' }" />
        {{ data.thumbNum }}
      </span>
            <span class="action" key="star">
          <IconStarFill style="{ color: '#ffb400' }" />
        {{ data.favourNum }}
      </span>
            <span @click="deleteById(data.id)">
              <icon-delete />
            </span>
          </template>
          <template #avatar>
            <a-avatar>
              <img
                alt="avatar"
                :src=data.user.userAvatar
              />
            </a-avatar>
          </template>
        </a-comment>
      </div>
    </a-scrollbar>
  </div>
</template>

<script lang="ts" setup>
import { IconBulb, IconDelete, IconHeartFill, IconMessage, IconStarFill, IconUser } from "@arco-design/web-vue/es/icon";
import store from "@/store";
import { useRouter } from "vue-router";
import { onMounted, ref } from "vue";
import { DeleteRequest, PostControllerService, PostQueryRequest, PostVO } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";

const postQueryRequest = ref<PostQueryRequest>();
const dataList = ref<PostVO>();
const loadData = async () => {
  const res = await PostControllerService.listMyPostVoByPageUsingPost(postQueryRequest);
  if (res.code === 0) {
    dataList.value = res.data.records;
  } else {
    message.error("加载失败" + res.message);
  }
};

const deleteById = async (id: string) => {
  const deleteRequest: DeleteRequest = {
    id: id
  };
  const res = await PostControllerService.deletePostUsingPost(deleteRequest);
  if (res.code === 0) {
    dataList.value = res.data.records;
    loadData(); // 删除成功后重新加载数据
  } else {
    message.error("加载失败" + res.message);
  }
};
const userouter = useRouter();
const toUserInfo = () => {
  userouter.push({
    path: "/user/info",
    query: { id: store.state.user?.loginUser?.id }
  });
};
const toUserPost = () => {
  userouter.push({
    path: "/user/post",
    query: { id: store.state.user?.loginUser?.id }
  });
};

const toUserProgress = () => {
  userouter.push({
    path: "/user/progress",
    query: { id: store.state.user?.loginUser?.id }
  });
};

/**
 * 页面加载时 请求数据
 */
onMounted(() => {
  loadData();
});

// 格式化日期时间的函数
const formattedDateTime = (dateTime: string) => {
  return moment(dateTime).format("YYYY-MM-DD HH:mm:ss");
};
</script>

<style scoped>
.card {
  display: flex;
  justify-content: space-between; /* 控制卡片之间的间隔 */
  align-items: center; /* 垂直居中 */
}

.container {
  display: flex;
  padding: 1rem;
}


a-form {
  display: flex;
  flex-direction: column;
  align-items: center; /* 使表单内容居中 */
}

a-form-item {
  width: 100%; /* 使表单项宽度为100% */
}

.menu-demo {
  box-sizing: border-box;
  z-index: 99;
  margin-top: 3rem;
}

.menu-demo .arco-menu {
  width: 200px;
  height: 100%;
  box-shadow: 0 0 1px rgba(0, 0, 0, 0.3);
}

.menu-demo .arco-menu :deep(.arco-menu-collapse-button) {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.menu-demo .arco-menu:not(.arco-menu-collapsed) :deep(.arco-menu-collapse-button) {
  right: 0;
  bottom: 8px;
  transform: translateX(50%);
}

.menu-demo .arco-menu:not(.arco-menu-collapsed)::before {
  content: '';
  position: absolute;
  right: 0;
  bottom: 0;
  width: 48px;
  height: 48px;
  background-color: inherit;
  border-radius: 50%;
  box-shadow: -4px 0 2px var(--color-bg-2), 0 0 1px rgba(0, 0, 0, 0.3);
  transform: translateX(50%);
}

.menu-demo .arco-menu.arco-menu-collapsed {
  width: 48px;
  height: auto;
  padding-top: 24px;
  padding-bottom: 138px;
  border-radius: 22px;
}

.menu-demo .arco-menu.arco-menu-collapsed :deep(.arco-menu-collapse-button) {
  right: 8px;
  bottom: 8px;
}


.question_submit_list {
  max-width: 800px; /* 设置最大宽度 */
  width: 100%; /* 宽度自适应 */
  margin: 0 auto; /* 上下外边距为0，左右外边距自动，实现水平居中 */
}

.tag-container {
  display: flex; /* 使用 flex 布局 */
  flex-wrap: wrap; /* 如果标签太多超出屏幕宽度，它们将换行 */
}

.tag-container a-tag {
  margin-right: 8px; /* 标签之间的间距 */
}
</style>
