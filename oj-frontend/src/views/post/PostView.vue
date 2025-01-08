<template>
  <a-affix :offsetBottom="120" align="right">
    <a-button status="success" shape="round" @click="SendPost">
      <icon-edit />
      发起讨论
    </a-button>
  </a-affix>
  <!--  <a-tabs default-active-key="1">-->
  <!--    <a-tab-pane key="1" title="最新">-->
  <a-scrollbar style="height:400px;overflow: auto;">
    <div class="new_post" v-if="dataList && dataList.length > 0">
      <a-comment
        v-for="data in dataList"
        :key="data.id"
        :author="data.user.userName"
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
      <span class="action" key="heart" @click="onLikeChange(data.id)">
        <span v-if="data.hasThumb">
          <IconHeartFill :style="{ color: '#f53f3f' }" />
        </span>
        <span v-else>
          <IconHeart />
        </span>
        {{ data.thumbNum + (like ? 1 : 0) }}
      </span>
          <span class="action" key="star" @click="onStarChange(data.id)">
        <span v-if="data.hasFavour">
          <IconStarFill style="{ color: '#ffb400' }" />
        </span>
        <span v-else>
          <IconStar />
        </span>
        {{ data.favourNum + (star ? 1 : 0) }}
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

    <!--    </a-tab-pane>-->
    <!--    <a-tab-pane key="2" title="最热">-->
    <!--      <div class="hot_post">-->
    <!--        hot-->
    <!--      </div>-->
    <!--    </a-tab-pane>-->
    <!--  </a-tabs>-->
  </a-scrollbar>

</template>
<script setup lang="ts">/**
 * 页面加载时 请求数据
 */
import { onMounted, ref } from "vue";
import {
  PostControllerService,
  PostFavourAddRequest,
  PostFavourControllerService,
  PostQueryRequest,
  type PostThumbAddRequest,
  PostThumbControllerService,
  PostVO
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { IconEdit, IconHeart, IconHeartFill, IconStar, IconStarFill } from "@arco-design/web-vue/es/icon";
import moment from "moment";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const store = useStore();
onMounted(() => {
  loadData();
});

const handleSearch = () => {
  // searchParams.value.current = 1; // 重置页码为1
  loadData();
};
const postQueryRequest = ref<PostQueryRequest>();

const dataList = ref<PostVO>();
const loadData = async () => {
  const res = await PostControllerService.listPostVoByPageUsingPost(postQueryRequest);
  if (res.code === 0) {
    dataList.value = res.data.records;
  } else {
    message.error("加载失败" + res.message);
  }
};
// 格式化日期时间的函数
const formattedDateTime = (dateTime: string) => {
  return moment(dateTime).format("YYYY-MM-DD HH:mm:ss");
};


const onLikeChange = (id: number) => {
  // 创建 PostFavourAddRequest 对象
  const postThumbAddRequest: PostThumbAddRequest = {
    postId: id
  };
  PostThumbControllerService.doThumbUsingPost(postThumbAddRequest).then(response => {
    if (response.code === 0) {
      // 更新点赞数和状态
      const index = dataList.value.findIndex(item => item.id === id);
      if (index !== -1) {
        dataList.value[index].hasThumb = !dataList.value[index].hasThumb;
        dataList.value[index].thumbNum += dataList.value[index].hasThumb ? 1 : -1;
      }
    } else {
      message.error("点赞失败：" + response.message);
    }
  })
    .catch(error => {
      console.error("点赞请求错误：", error);
      message.error("点赞请求错误");
    });
};

const onStarChange = (id: number) => {
  // 创建 PostFavourAddRequest 对象
  const postFavourAddRequest: PostFavourAddRequest = {
    postId: id
  };
  PostFavourControllerService.doPostFavourUsingPost(postFavourAddRequest).then(response => {
    if (response.code === 0) {
      // 更新收藏数和状态
      const index = dataList.value.findIndex(item => item.id === id);
      if (index !== -1) {
        dataList.value[index].hasFavour = !dataList.value[index].hasFavour;
        dataList.value[index].favourNum += dataList.value[index].hasFavour ? 1 : -1;
      }
    } else {
      message.error("收藏失败：" + response.message);
    }
  })
    .catch(error => {
      console.error("收藏请求错误：", error);
      message.error("收藏请求错误");
    });
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
const SendPost = () => {
  userouter.push({
    path: "/post/add"
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

.tag-container {
  display: flex; /* 使用 flex 布局 */
  flex-wrap: wrap; /* 如果标签太多超出屏幕宽度，它们将换行 */
}

.tag-container a-tag {
  margin-right: 8px; /* 标签之间的间距 */
}

</style>