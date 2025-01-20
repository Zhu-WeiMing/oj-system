import {RouteRecordRaw} from "vue-router";
import HomeView from "@/views/ExampleView.vue";
import NoAuthView from "@/views/NoAuthView.vue";
import ASSESS_ENUM from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UseLoginView from "@/views/user/UseLoginView.vue";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import UpdateQuestionView from "@/views/question/UpdateQuestionView.vue";
import QuestionView from "@/views/question/QuestionView.vue";
import ViewQuestionView from "@/views/question/ViewQuestionView.vue";
import UserInfoView from "@/views/user/UserInfoView.vue";
import QuestionInfoView from "@/views/question/QuestionInfoView.vue";
import SendAnswerView from "@/views/question/SendAnswerView.vue";
import UserProgressView from "@/views/user/UserProgressView.vue";
import PostView from "@/views/post/PostView.vue";
import AddPostView from "@/views/post/PostAddView.vue";
import UserPostView from "@/views/user/UserPostView.vue";
import SensitiveView from "@/views/sensitive/SensitiveView.vue";

export const routes: Array<RouteRecordRaw> = [
    {
        path: "/user",
        name: "用户",
        component: UserLayout,
        meta: {
            //给路由新增一个标志位，用于判断路由是否显隐
            hideInMenu: true
        },
        children: [
            {
                path: "/user/login",
                name: "用户登录",
                component: UseLoginView
            },
            {
                path: "/user/register",
                name: "用户注册",
                component: UserRegisterView
            },
            {
                path: "/user/info",
                name: "个人信息",
                component: UserInfoView
            },
            {
                path: "/user/progress",
                name: "进展分析",
                component: UserProgressView
            },
            {
                path: "/user/post",
                name: "我的讨论",
                component: UserPostView
            }
        ]
    },
    {
        path: "/",
        name: "主页",
        component: HomeView
    },
    {
        path: "/question",
        name: "浏览题目",
        component: QuestionView
    },
    {
        path: "/view/question/:id",
        name: "在线做题",
        component: ViewQuestionView,
        props: true, //开启props为true 可以在页面的props中直接获取到动态参数（题目id）
        meta: {
            hideInMenu: true,
            access: ASSESS_ENUM.USER
        }
    },
    {
        path: "/info/question/",
        name: "题目信息",
        component: QuestionInfoView,
        meta: {
            hideInMenu: true,
            access: ASSESS_ENUM.ADMIN
        }
    },
    {
        path: "/add/question",
        name: "创建题目",
        component: AddQuestionView,
        meta: {
            access: ASSESS_ENUM.ADMIN
        }
    },
    {
        path: "/answer/send/:id",
        name: "发布题解",
        component: SendAnswerView,
        props: true, //开启props为true 可以在页面的props中直接获取到动态参数（题目id）
        meta: {
            hideInMenu: true
        }
    },
    {
        path: "/update/question",
        name: "更新题目",
        component: UpdateQuestionView,
        meta: {
            hideInMenu: true,
            access: ASSESS_ENUM.USER
        }
    },

    {
        path: "/post/list",
        name: "讨论",
        component: PostView,

    },

    {
        path: "/post/add",
        name: "发布讨论",
        component: AddPostView,
        meta: {
            hideInMenu: true,
            access: ASSESS_ENUM.USER
        }
    },

    {
        path: "/post/admin",
        name: "审核管理",
        component: SensitiveView,
        meta: {
            access: ASSESS_ENUM.ADMIN
        }
    },


    {
        path: "/noAuth",
        name: "无权限",
        component: NoAuthView,
        meta: {
            //给路由新增一个标志位，用于判断路由是否显隐
            hideInMenu: true
        }
    }
];
