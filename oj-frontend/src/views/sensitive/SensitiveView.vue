<template>
  <div class="menu-demo">
    <!-- 外层布局 -->
    <div class="layout">
      <!-- 菜单 -->
      <a-menu
          mode="pop"
          showCollapseButton
          style="height: 500px; width: 150px; border: 1px solid var(--color-border)"
      >
        <a-menu-item key="1" @click="postExamine">
          <template #icon>
            <icon-storage/>
          </template>
          帖子审核
        </a-menu-item>
        <a-menu-item key="2" @click="commentsExamine">
          <template #icon>
            <icon-menu/>
          </template>
          评论审核
        </a-menu-item>
      </a-menu>

      <a-split
          v-if="hideStr === true"
          :style="{
            height: '500px',
            width: '1300px',
            flex: 1,
            border: '1px solid var(--color-border)'
          }"
          size="0.5"
          min="80px"
      >
        <template #first>
          <div class="search">
            <a-form :model="postQueryRequest" layout="inline">
              <a-form-item field="id" label="id" style="min-width: 200px">
                <a-input v-model="postQueryRequest.id" placeholder="请输入id"/>
              </a-form-item>
              <a-form-item field="section" label="审核状态">
                <a-select v-model="postQueryRequest.examineStatus" placeholder="审核状态" allow-clear>
                  <a-option value="0">待审核</a-option>
                  <a-option value="1">审核通过</a-option>
                  <a-option value="2">审核违规</a-option>
                </a-select>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="handleSearch">查询</a-button>
              </a-form-item>
            </a-form>
          </div>
          <a-scrollbar style="height: 400px; overflow: auto;">
            <div class="new_post" v-if="dataList && dataList.length > 0">
              <a-comment
                  :class="{ selected: isSelected(data) }"
                  v-for="data in dataList"
                  :key="data.id"
                  :author="data.user.userName"
                  :content="data.content"
                  :datetime="formattedDateTime(data.createTime as string)"
                  @click="getExamineInfo(data)"
              >
                <!-- 水印图标 -->
                <div class="status-icon" v-if=isSelected(data)>
                  <div v-if="data.examineStatus == 1">
                    <img src="../../assets/examineImg/通过.png" alt="通过" class="status-icon-image"/>
                  </div>
                  <div v-else-if="data.examineStatus == 2">
                    <img src="../../assets/examineImg/不通过.png" alt="不通过" class="status-icon-image"/>
                  </div>
                  <div v-else>
                    <img src="../../assets/examineImg/审核中.png" alt="待审核" class="status-icon-image"/>
                  </div>
                </div>

                <div class="tag-container">
                  <!-- 遍历 tagList 数组，为每个标签单独渲染一个 <a-tag> -->
                  <div v-for="tag in data.tagList" :key="tag">
                    <a-tag color="arcoblue">{{ tag }}</a-tag>
                  </div>
                </div>
                <template #actions>
                  <span class="action" key="heart">
                    <span v-if="data.hasThumb">
                      <IconHeartFill :style="{ color: '#f53f3f' }"/>
                    </span>
                    <span v-else>
                      <IconHeart/>
                    </span>
                    {{ data.thumbNum }}
                  </span>
                  <span class="action" key="star">
                    <span v-if="data.hasFavour">
                      <IconStarFill style="{ color: '#ffb400' }"/>
                    </span>
                    <span v-else>
                      <IconStar/>
                    </span>
                    {{ data.favourNum }}
                  </span>
                </template>
                <template #avatar>
                  <a-avatar>
                    <img
                        alt="avatar"
                        :src="data.user.userAvatar"
                    />
                  </a-avatar>
                </template>
              </a-comment>
            </div>
            <div v-else>
              <a-empty/>
            </div>

          </a-scrollbar>
        </template>
        <template #second>
          <div v-if="examine.id ==null">
            <a-empty/>
            点击左边帖子，查看审核信息。
          </div>
          <div v-else>

            <a-descriptions style="margin-top: 20px" :data="descriptionsData" size="large" title="审核信息"
                            :column="1"></a-descriptions>
            <div>
              <a-button type="primary" @click="updatePostUsingPost(examine.postId,1)">
                通过
              </a-button>
              <a-button type="outline" status="danger" @click="updatePostUsingPost(examine.postId,2)">不通过
              </a-button>
            </div>
          </div>
        </template>
      </a-split>


    </div>
  </div>
</template>

<script setup lang="ts">
import {IconHeart, IconHeartFill, IconMenu, IconStar, IconStarFill, IconStorage} from "@arco-design/web-vue/es/icon";
import {computed, ref} from "vue";
import {PostControllerService, PostQueryRequest, type PostUpdateRequest, PostVO} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";
import {ExamineControllerService} from "../../../generated/services/ExamineControllerService";
import {ExamineVO} from "../../../generated/models/ExamineVO";
import {ThirdApiBanList} from "../../../generated/models/BankList";
import {CommentsControllerService} from "../../../generated/services/CommentsControllerService";

const hideStr = ref<boolean>(true);


const examine = ref<ExamineVO>({
  id: null,
  examineUserId: null,
  postId: null,
  banList: [
    {
      word: "",
      category: "",
      explanation: "",
      index: null,
    }
  ],
  examineUserName: "",
  createTime: "",
  updateTime: "",
});

const selectedPostId = ref<number | null>(null);

const isSelected = (data: PostVO) => {
  return data.id === selectedPostId.value;
};

const getExamineInfo = async (data: any) => {
  try {
    selectedPostId.value = data.id; // 设置选中项的 ID
    const res = await ExamineControllerService.getByPostIdGet(data.id);
    examine.value = res.data;
    post.value = data;
  } catch (error) {
    console.error("获取审核信息出错：", error);
  }
};
/**
 * 审核接口
 * @param postId
 */
const updatePostUsingPost = async (postId: number, examineStatus: number) => {
  const postUpdateRequest = ref<PostUpdateRequest>({
    id: postId,
    examineStatus: examineStatus,
  });
  const res = await PostControllerService.updatePostUsingPost(postUpdateRequest.value);
  if (res.code === 0) {
    getExamineInfo(postId)
  } else {
    message.error("加载失败: " + res.message);
  }
};


// 使用 computed 属性将 examine 转换为 descriptionsData 格式
const descriptionsData = computed(() => [
  {label: 'ID', value: examine.value.id},
  {label: '审批人ID', value: examine.value.examineUserId},
  {label: '审批人', value: examine.value.examineUserName},
  {label: '帖子ID', value: examine.value.postId},
  {
    label: '违禁词',
    value: Array.isArray(examine.value.banList)
        ? examine.value.banList.map((item: ThirdApiBanList) => item.word).join(', ')
        : '无'
  },
  {label: '创建时间', value: examine.value.createTime},
  {label: '更新时间', value: examine.value.updateTime}
]);


const dataList = ref<PostVO[]>([]);
const post = ref<PostVO>(
    {
      content: "",
      id: null,
      tagList: [],
      title: "",
      createTime: "",
      user: {
        userAvatar: "",
        userName: ""
      },
      userId: null,
    }
);
const postQueryRequest = ref<PostQueryRequest>({
  id: null,
  examineStatus: null
});

const postExamine = async () => {
  const res = await PostControllerService.listPostVoByPageUsingPost(postQueryRequest.value);
  if (res.code === 0) {
    dataList.value = res.data.records;
  } else {
    message.error("加载失败: " + res.message);
  }
};

const commentsExamine = async () => {
  const res = await CommentsControllerService.listComment(postQueryRequest.value);
  console.log("res:::" + res)
  if (res.code === 0) {
    dataList.value = res.data;
  } else {
    message.error("加载失败: " + res.message);
  }
};


// 格式化日期时间的函数
const formattedDateTime = (dateTime: string) => {
  return moment(dateTime).format("YYYY-MM-DD HH:mm:ss");
};


</script>

<style scoped>
.layout {
  display: flex;
  height: 500px;
  gap: 30px; /* 设置menu和split之间的间距 */
}

.menu-demo {
  display: flex;
  flex-direction: row;
}

/* 选中状态的样式 */
.selected {
  transform: translateX(20px); /* 向右偏移 */
  background-color: #e0f7e9; /* 变浅绿色 */
  position: relative;
  transition: transform 0.5s ease, background-color 0.5s ease;
  overflow: hidden;
}

/* 移动轨迹效果 */
.selected::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(224, 247, 233, 0.5);
  transform: translateX(-100%);
  transition: transform 0.5s ease;
  z-index: -1; /* 置于组件后面 */
}

.selected::before {
  transform: translateX(0); /* 显示移动轨迹 */
}


/* 确保水印图标不会覆盖内容 */
.status-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1; /* 确保它不会遮盖其他内容 */
}

/* 水印图标图片 */
.status-icon-image {
  width: 70px; /* 设置图标宽度 */
  height: 50px; /* 设置图标高度 */
  opacity: 0.7; /* 设置透明度 */
}

/* 增加评论内容的右侧内边距，以留出空间 */
.a-comment {
  position: relative;
  padding-right: 90px; /* 根据水印图标的宽度调整 */
}
</style>