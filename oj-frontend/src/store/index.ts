import { createStore } from "vuex";
import user from "@/store/user";
import code from "./code";

export default createStore({
  mutations: {},
  actions: {},
  modules: {
    user,
    code
  },
});
