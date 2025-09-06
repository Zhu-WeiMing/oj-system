<template>
  <div class="questionAdd" style="background: #ffffff">
    <div class="form-container">
      <a-form :model="form">
        <a-form-item field="title" label="标题">
          <a-input v-model="form.title" placeholder="请输入标题"/>
        </a-form-item>
        <a-form-item field="context" label="题目内容">
          <MdEditor :handle-change="onContextChange" :value="form.content" style="width: 1000px"/>
        </a-form-item>

        <a-form-item field="answer" label="答案">
          <MdEditor :handle-change="onAnswerChange" :value="form.answer"/>
        </a-form-item>

        <a-form-item field="tags" label="标签">
          <a-input-tag
              v-model="form.tags"
              :value="form.tags"
              allow-clear
              placeholder="请选择标签"
          />
        </a-form-item>

        <a-form-item :content-flex="false" :merge-props="false" label="判题配置">
          <a-space direction="vertical">
            <a-form-item
                field="judgeConfig.memory"
                label="memory"
                style="min-width: 480px"
            >
              <a-input-number
                  v-model="form.judgeConfig.memory"
                  mode="button"
                  placeholder="请输入内存消耗"
                  size="large"
              />
            </a-form-item>

<!--            <a-form-item-->
<!--                field="judgeConfig.stackLimit"-->
<!--                label="stackLimit"-->
<!--                style="min-width: 480px"-->
<!--            >-->
<!--              <a-input-number-->
<!--                  v-model="form.judgeConfig.stackLimit"-->
<!--                  mode="button"-->
<!--                  placeholder="请输入堆栈消耗"-->
<!--                  size="large"-->
<!--              />-->
<!--            </a-form-item>-->

            <a-form-item
                field="judgeConfig.time"
                label="time"
                style="min-width: 480px"
            >
              <a-input-number
                  v-model="form.judgeConfig.time"
                  mode="button"
                  placeholder="请输入时间消耗"
                  size="large"
              />
            </a-form-item>
          </a-space>
        </a-form-item>

        <a-form-item :content-flex="false" :merge-props="false" label="测试用例" style="width: 1000px">
          <div
            v-for="(judgeCase, index) of form.judgeCase"
            :key="index"
            align="left"
            class="ceshi"
          >
            <a-form-item
                :key="index"
                :field="`form.judgeCase[${index}].input`"
                :label="`输入用例-${index}`"
                style="width: 500px"
            >
              <a-input v-model="judgeCase.input" placeholder="请输入测试输入用例"/>
            </a-form-item>

            <a-form-item
                :key="index"
                :field="`form.judgeCase[${index}].out`"
                :label="`输出用例-${index}`"
                style="width: 500px"
            >
              <a-input
                  v-model="judgeCase.output"
                  placeholder="请输入测试输出用例"
              />
            </a-form-item>

            <a-button
                :style="{ marginLeft: '10px' }"
                status="danger"
                @click="handleDelete(index)"
            >删除
            </a-button>
          </div>
          <div>
            <a-button status="success" type="outline" @click="handleAdd"
            >添加测试用例
            </a-button>
          </div>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" @click="doSubmit">提交</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const router = useRouter();

const form = reactive({
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    memory: 0,
    stackLimit: 0,
    time: 0,
  },
  tags: [],
  title: "",
});

const doSubmit = async () => {
  const res = await QuestionControllerService.addQuestionUsingPost(form);
  if (res.code === 0) {
    message.success("添加成功");
    router.go(-1);
  } else {
    message.error("创建失败：" + res.message);
  }
};

const handleAdd = () => {
  if (form.judgeCase) {
    form.judgeCase.push({
      input: "",
      output: "",
    });
  }
};

const handleDelete = (index: number) => {
  if (form.judgeCase) {
    form.judgeCase.splice(index, 1);
  }
};

const onAnswerChange = (v: string) => {
  form.answer = v;
};

const onContextChange = (v: string) => {
  form.content = v;
};
</script>

<style scoped>
.questionAdd {
  display: flex;
  justify-content: flex-start; /* 左对齐 */
  align-items: flex-start;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.form-container {
  width: 100%;
  max-width: 800px;
}

.ceshi{
  display: flex;
  justify-content: center;
  align-items: flex-start;
  //margin-left: -110px;
  width: 1000px;

}
</style>