import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/ExampleView.vue";
import NoAuthView from "@/views/NoAuthView.vue";
import ASSESS_ENUM from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UseLoginView from "@/views/user/UseLoginView.vue";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManagerQuestionView from "@/views/question/ManagerQuestionView.vue";
import UpdateQuestionView from "@/views/question/UpdateQuestionView.vue";
import QuestionView from "@/views/question/QuestionView.vue";
import ViewQuestionView from "@/views/question/ViewQuestionView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    meta: {
      //给路由新增一个标志位，用于判断路由是否显隐
      hideInMenu: true,
    },
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UseLoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
      },
    ],
  },
  {
    path: "/",
    name: "主页",
    component: HomeView,
  },
  {
    path: "/question",
    name: "浏览题目",
    component: QuestionView,
  },
  {
    path: "/view/question/:id",
    name: "在线做题",
    component: ViewQuestionView,
    props: true, //开启props为true 可以在页面的props中直接获取到动态参数（题目id）
    meta: {
      hideInMenu: true,
      access: ASSESS_ENUM.USER,
    },
  },
  {
    path: "/add/question",
    name: "创建题目",
    component: AddQuestionView,
    meta: {
      access: ASSESS_ENUM.USER,
    },
  },
  {
    path: "/update/question",
    name: "更新题目",
    component: UpdateQuestionView,
    meta: {
      hideInMenu: true,
      access: ASSESS_ENUM.USER,
    },
  },
  {
    path: "/manager/question",
    name: "管理题目",
    component: ManagerQuestionView,
    meta: {
      access: ASSESS_ENUM.USER,
    },
  },

  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuthView,
    meta: {
      //给路由新增一个标志位，用于判断路由是否显隐
      hideInMenu: true,
    },
  },
];
