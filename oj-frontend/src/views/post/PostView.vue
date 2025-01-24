<template>
  <a-affix :offsetBottom="120" align="right">
    <a-button status="success" shape="round" @click="SendPost">
      <icon-edit/>
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
          <IconHeartFill :style="{ color: '#f53f3f' }"/>
        </span>
        <span v-else>
          <IconHeart/>
        </span>
        {{ data.thumbNum + (like ? 1 : 0) }}
      </span>
          <span class="action" key="star" @click="onStarChange(data.id)">
        <span v-if="data.hasFavour">
          <IconStarFill style="{ color: '#ffb400' }"/>
        </span>
        <span v-else>
          <IconStar/>
        </span>
        {{ data.favourNum + (star ? 1 : 0) }}
      </span>
          <span class="action" key="reply" @click="openComments(data)">
        <IconMessage/>
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
        <template>
          <a-drawer :width="500" :visible="visible" @ok="handleOk" @cancel="handleCancel" :footer="false"
                    unmountOnClose>
            <div>
              <a-comment
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
          <IconHeartFill :style="{ color: '#f53f3f' }"/>
        </span>
        <span v-else>
          <IconHeart/>
        </span>
        {{ data.thumbNum + (like ? 1 : 0) }}
      </span>
                  <span class="action" key="star" @click="onStarChange(data.id)">
        <span v-if="data.hasFavour">
          <IconStarFill style="{ color: '#ffb400' }"/>
        </span>
        <span v-else>
          <IconStar/>
        </span>
        {{ data.favourNum + (star ? 1 : 0) }}
      </span>
                  <span class="action" key="reply" @click="openComments(data)">
        <IconMessage/>
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
            <div class="comments">


              <a-divider orientation="center">评论区</a-divider>
              <a-comment
                  v-for="commentInfo in commentList"
                  :key="commentInfo.id"
                  :author="commentInfo.user.userName"
                  :content="commentInfo.content"
                  :datetime="formattedDateTime(commentInfo.createTime as string) "
              >
                <template #avatar>
                  <a-avatar>
                    <img
                        alt="avatar"
                        :src=commentInfo.user.userAvatar
                    />
                  </a-avatar>
                </template>
                <template v-if="commentInfo.hasChild == true">
                  <div @click="openChildComments(commentInfo.postId,commentInfo.id)">点击查看更多回复</div>
                </template>
              </a-comment>

              <!--回复框-->
              <a-affix
                  :offsetBottom="30"
              >
                <a-comment
                    align="right"
                >
                  <template #content>
                    <a-input style="width: 390px" :model-value="commentContent"
                             @update:model-value="commentContent = $event" placeholder="发起评论"/>
                    <a-button key="1" type="primary" @click="sendComments(data.id,null)"> 发送</a-button>
                  </template>
                </a-comment>
              </a-affix>
            </div>
          </a-drawer>
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
import {onMounted, ref} from "vue";
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
import {IconEdit, IconHeart, IconHeartFill, IconMessage, IconStar, IconStarFill} from "@arco-design/web-vue/es/icon";
import moment from "moment";
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {CommentsControllerService} from "../../../generated/services/CommentsControllerService";


/**
 * 打开子级评论
 * @param postId
 * @param parentId
 */
const openChildComments = async (postId: number, parentId: number) => {
  const res = await CommentsControllerService.listChildCommentsPage(postId, parentId, 1, 10);
};


const commentContent = ref("");

const sendComments = async (id: number, parentId: number) => {
  const res = await CommentsControllerService.saveComment(id, parentId, commentContent.value);
  if (res.code === 0) {

    message.success('发布成功！')
    commentContent.value = ""
  } else {
    message.error("发布失败 " + res.message);
  }
}

const store = useStore();
onMounted(() => {
  loadData();
});


const visible = ref(false);
const commentList = ref([{
  id: null,
  parentId: null,
  userId: null,
  postId: null,
  user: {
    userAvatar: "",
    userName: "",
  },
  content: "",
  createTime: "",
  hasChild: false
}])
const openComments = async (data: PostVO) => {
  const res = await CommentsControllerService.listParentCommentsPage(data.id, 1, 10);
  commentList.value = res.data.records
  visible.value = true;
};


const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
}
const handleSearch = () => {
  // searchParams.value.current = 1; // 重置页码为1
  loadData();
};
const postQueryRequest = ref<PostQueryRequest>({
  examineStatus: 1 //审核通过
});

const dataList = ref<PostVO>();
const loadData = async () => {
  const res = await PostControllerService.listPostVoByPageUsingPost(postQueryRequest.value);
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