// initial state
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: () => ({
    loginUser: {
      id: "",
      userName: "",
      userAvatar:
        "https://bronny.oss-cn-hangzhou.aliyuncs.com/DEFAULT_PHOTO.jpg"
    }
  }),
  actions: {
    async getLoginUser({ commit, state }, payload) {
      // 从远程请求获取登录信息
      const res = await UserControllerService.getLoginUserUsingGet();
      if (res.code === 0) {
        console.log("getLoginUser:::if:::" + JSON.stringify(payload));
        commit("updateUser", res.data);
      } else {
        console.log("getLoginUser:::else");
        commit("updateUser", {
          ...state.loginUser,
          userRole: ACCESS_ENUM.NOT_LOGIN
        });
      }
    },
    async logout({ commit }) {
      const res = await UserControllerService.userLogoutUsingPost();
      if (res.code === 0) {
      }
    }
  },
  mutations: {
    updateUser(state, payload) {
      state.loginUser = payload;
    }
  }
} as StoreOptions<any>;
