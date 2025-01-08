<template>

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
      <span class="action" key="heart" >

          <IconHeart />
        {{ data.thumbNum  }}
      </span>
          <span class="action" key="star">
          <IconStar />
        {{ data.favourNum  }}
      </span>
          <span v-if="store.state.user?.loginUser?.userRole === ASSESS_ENUM.ADMIN" @click="deleteById(data.id)">
              <button><icon-delete /></button>
            </span>
          <span v-if="store.state.user?.loginUser?.userRole === ASSESS_ENUM.ADMIN" @click="toUpdatePage(data)">
              <button><icon-edit /></button>
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
        <a-drawer :width="340" height="500px" :visible="visible" @ok="updateById(postUpdateRequest)"
                  @cancel="handleCancel"
                  placement="bottom"
                  unmountOnClose>
          <template #title>
            修改帖子
          </template>
          <div>
            <a-space direction="vertical" size="large">
              <a-input :style="{width:'320px'}" v-model="postUpdateRequest.id"
                       placeholder="在此输入标题" disabled />
              <a-input :style="{width:'320px'}" v-model="postUpdateRequest.title"
                       placeholder="在此输入标题" />
              <a-input :style="{width:'320px'}" v-model="postUpdateRequest.content"
                       placeholder="在此输入内容" />
              <div v-for="tag in data!.tagList" :key="tag">
                <a-input-tag :style="{width:'320px'}"
                             placeholder="在此输入标签，然后按enter"
                             v-model="postUpdateRequest.tags"
                             allow-clear
                />
              </div>
            </a-space>
          </div>
        </a-drawer>
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
  DeleteRequest,
  PostControllerService,
  PostFavourAddRequest,
  PostFavourControllerService,
  PostQueryRequest,
  type PostThumbAddRequest,
  PostThumbControllerService,
  type PostUpdateRequest,
  PostVO
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { IconDelete, IconEdit, IconHeart, IconHeartFill, IconStar, IconStarFill } from "@arco-design/web-vue/es/icon";
import moment from "moment";
import { useRouter } from "vue-router";
import ASSESS_ENUM from "@/access/accessEnum";
import { useStore } from "vuex";

const postUpdateRequest = ref<PostUpdateRequest>({ id: "", title: "", content: "", tags: [] });
const visible = ref(false);
const toUpdatePage = (data) => {
  Object.assign(postUpdateRequest.value, {
    id: data.id,
    title: data.title,
    content: data.content,
    tags: data.tagList
  });
  visible.value = true;
};

const handleCancel = () => {
  visible.value = false;
};
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

const updateById = async (postUpdateRequest: PostUpdateRequest) => {
  const res = await PostControllerService.updatePostUsingPost(postUpdateRequest);
  if (res.code === 0) {
    dataList.value = res.data.records;
    loadData(); // 删除成功后重新加载数据
    visible.value = false;
    message.success("修改成功");
  } else {
    message.error("修改失败" + res.message);
  }
};
const userouter = useRouter();
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