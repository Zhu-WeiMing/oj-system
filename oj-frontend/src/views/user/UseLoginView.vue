<template>
  <div :class="['container', { 'sign-up-mode': isLogin }]">
    <div class="form-container">
      <div class="signin-signup">
        <form class="sign-in-form">
          <h2 class="title">登录</h2>
          <div class="input-field">
            <i>
              <font-awesome-icon icon="fa-solid fa-user"/>
            </i>
            <input type="text" placeholder="账号" v-model="Loginform.userAccount"/>
          </div>
          <div class="input-field">
            <i>
              <font-awesome-icon icon="fa-solid fa-lock"/>
            </i>
            <input type="password" placeholder="密码" v-model="Loginform.userPassword"/>
          </div>
          <div v-if="!isLogin">
            <button class="btn solid" @click="handleLogin">立即登录</button>
          </div>
        </form>


        <form class="sign-up-form">
          <h2 class="title">注册</h2>
          <a-space size="large" class="avatar-container" align="center">
            <a-avatar @click="uploadImage" :image-url="registerform.userAvatar" :size="100">
              <template #trigger-icon>
                <IconEdit/>
              </template>
            </a-avatar>
          </a-space>
          <div class="input-field">
            <i>
              <font-awesome-icon icon="fa-solid fa-user"/>
            </i>
            <input type="text" placeholder="用户名" v-model="registerform.userName"/>
          </div>
          <div class="input-field">
            <i>
              <font-awesome-icon icon="fa-solid fa-user"/>
            </i>
            <input type="text" placeholder="账号" v-model="registerform.userAccount"/>
          </div>
          <div class="input-field">
            <i>
              <font-awesome-icon icon="fa-solid fa-envelope"/>
            </i>
            <input type="password" placeholder="密码" v-model="registerform.userPassword"/>
          </div>
          <div class="input-field">
            <i>
              <font-awesome-icon icon="fa-solid fa-envelope"/>
            </i>
            <input type="password" placeholder="确认密码" v-model="registerform.checkPassword"/>
          </div>
          <div v-if="isLogin">
            <button class="btn" @click="handleRegister">立即注册</button>
          </div>
        </form>

      </div>
    </div>

    <div class="panels-container">
      <div class="panel left-panel">
        <div class="content">
          <h3>加入我们</h3>
          <p>加入我们，成为本站的一份子。</p>
          <button class="btn transparent" @click="isLogin = !isLogin">
            去注册
          </button>
        </div>
        <img src="@/assets/images/signin.svg" class="image" alt=""/>
      </div>
      <div class="panel right-panel">
        <div class="content">
          <h3>已有帐号？</h3>
          <p>立即登录已有帐号，享受独家权益。</p>
          <button class="btn transparent" @click="isLogin = !isLogin">
            去登录
          </button>
        </div>
        <img src="@/assets/images/signup.svg" class="image" alt=""/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {FileControllerService, UserControllerService, UserLoginRequest, UserRegisterRequest} from "../../../generated";
import {reactive, ref} from "vue";
import {useStore} from "vuex";
import {useRouter} from "vue-router";
import message from "@arco-design/web-vue/es/message";
import {IconEdit} from "@arco-design/web-vue/es/icon";

const isLogin = ref(false);

const Loginform = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const store = useStore();
const router = useRouter();

//登录接口
const handleLogin = async () => {
  // 调用后端接口
  const res = await UserControllerService.userLoginUsingPost(Loginform);
  console.log(res, 'res')
  if (res.code === 0) {
    // 登陆成功
    await store.dispatch("user/getLoginUser");
    //返回上一页
    router.back();
  } else {
    // 登录失败
    message.error("登陆失败" + JSON.stringify(res.message));
  }
};


const registerform = reactive({
  userName: "",
  userAccount: "",
  userPassword: "",
  checkPassword: "",
  userAvatar: "https://bronny.oss-cn-hangzhou.aliyuncs.com/DEFAULT_PHOTO.jpg", //默认头像
} as UserRegisterRequest);


const handleRegister = async () => {
  const res = await UserControllerService.userRegisterUsingPost(registerform);
  if (res.code === 0) {
    message.success("添加成功！请登录。");
    isLogin.value = false;
  } else {
    message.error("创建失败：" + res.message);
  }
};
const uploadImage = async () => {
  // 创建一个文件输入元素
  const fileInput = document.createElement("input");
  fileInput.type = "file";
  fileInput.accept = "image/*"; // 限制只能选择图片文件

  // 监听文件选择事件
  fileInput.onchange = async (event) => {
    const files = event.target!.files[0];
    if (files.length === 0) {
      message.error("未选择文件");
      return;
    }

    // 获取选择的文件
    const file = files;

    // 调用上传文件的接口
    const res = await FileControllerService.uploadFileUsingPost(
        "user_avatar",
        file
    );

    if (res.code === 0) {
      form.userAvatar = res.data;
      message.info("上传成功！");
    } else {
      message.error("上传失败：" + res.message);
    }
  };

  // 触发文件输入元素
  fileInput.click();
};

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400;500;600;700;800&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body,
input {
  font-family: 'Poppins', sans-serif;
}

.container {
  position: relative;
  width: 100%;
  background-color: #fff;
  min-height: 100vh;
  overflow: hidden;
}


.signin-signup {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  left: 75%;
  width: 50%;
  transition: 1s 0.7s ease-in-out;
  display: grid;
  grid-template-columns: 1fr;
  z-index: 5;
}

form {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0rem 5rem;
  transition: all 0.2s 0.7s;
  overflow: hidden;
  grid-column: 1 / 2;
  grid-row: 1 / 2;
}

form.sign-up-form {
  opacity: 0;
  z-index: 1;
}

form.sign-in-form {
  z-index: 2;
}

.title {
  font-size: 2.2rem;
  color: #444;
  margin-bottom: 10px;
}

.input-field {
  max-width: 380px;
  width: 100%;
  background-color: #f0f0f0;
  margin: 10px 0;
  height: 55px;
  border-radius: 55px;
  display: grid;
  grid-template-columns: 15% 85%;
  padding: 0 0.4rem;
  position: relative;
}

.input-field i {
  text-align: center;
  line-height: 55px;
  color: #acacac;
  transition: 0.5s;
  font-size: 1.1rem;
}

.input-field input {
  background: none;
  outline: none;
  border: none;
  line-height: 1;
  font-weight: 600;
  font-size: 1.1rem;
  color: #333;
}

.input-field input::placeholder {
  color: #aaa;
  font-weight: 500;
}

.social-text {
  padding: 0.7rem 0;
  font-size: 1rem;
}

.social-media {
  display: flex;
  justify-content: center;
}

.social-icon {
  height: 46px;
  width: 46px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 0.45rem;
  color: #333;
  border-radius: 50%;
  border: 1px solid #333;
  text-decoration: none;
  font-size: 1.1rem;
  transition: 0.3s;
}

.social-icon:hover {
  color: #4481eb;
  border-color: #4481eb;
}

.btn {
  width: 150px;
  background-color: #5995fd;
  border: none;
  outline: none;
  height: 49px;
  border-radius: 49px;
  color: #fff;
  text-transform: uppercase;
  font-weight: 600;
  margin: 10px 0;
  cursor: pointer;
  transition: 0.5s;
}

.btn:hover {
  background-color: #4d84e2;
}

.panels-container {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
}

.container:before {
  content: '';
  position: absolute;
  height: 2000px;
  width: 2000px;
  top: -10%;
  right: 48%;
  transform: translateY(-50%);
  background-image: linear-gradient(-45deg, #4481eb 0%, #04befe 100%);
  transition: 1.8s ease-in-out;
  border-radius: 50%;
  z-index: 6;
}

.image {
  width: 100%;
  transition: transform 1.1s ease-in-out;
  transition-delay: 0.4s;
}

.panel {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  text-align: center;
  z-index: 6;
}

.left-panel {
  pointer-events: all;
  padding: 3rem 17% 2rem 12%;
}

.right-panel {
  pointer-events: none;
  padding: 3rem 12% 2rem 17%;
}

.panel .content {
  color: #fff;
  transition: transform 0.9s ease-in-out;
  transition-delay: 0.6s;
}

.panel h3 {
  font-weight: 600;
  line-height: 1;
  font-size: 1.5rem;
}

.panel p {
  font-size: 0.95rem;
  padding: 0.7rem 0;
}

.btn.transparent {
  margin: 0;
  background: none;
  border: 2px solid #fff;
  width: 130px;
  height: 41px;
  font-weight: 600;
  font-size: 0.8rem;
}

.right-panel .image,
.right-panel .content {
  transform: translateX(800px);
}

/* ANIMATION */

.container.sign-up-mode:before {
  transform: translate(100%, -50%);
  right: 52%;
}

.container.sign-up-mode .left-panel .image,
.container.sign-up-mode .left-panel .content {
  transform: translateX(-800px);
}

.container.sign-up-mode .signin-signup {
  left: 25%;
}

.container.sign-up-mode form.sign-up-form {
  opacity: 1;
  z-index: 2;
}

.container.sign-up-mode form.sign-in-form {
  opacity: 0;
  z-index: 1;
}

.container.sign-up-mode .right-panel .image,
.container.sign-up-mode .right-panel .content {
  transform: translateX(0%);
}

.container.sign-up-mode .left-panel {
  pointer-events: none;
}

.container.sign-up-mode .right-panel {
  pointer-events: all;
}

@media (max-width: 870px) {
  .container {
    min-height: 800px;
    height: 100vh;
  }

  .signin-signup {
    width: 100%;
    top: 95%;
    transform: translate(-50%, -100%);
    transition: 1s 0.8s ease-in-out;
  }

  .signin-signup,
  .container.sign-up-mode .signin-signup {
    left: 50%;
  }

  .panels-container {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 2fr 1fr;
  }

  .panel {
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    padding: 2.5rem 8%;
    grid-column: 1 / 2;
  }

  .right-panel {
    grid-row: 3 / 4;
  }

  .left-panel {
    grid-row: 1 / 2;
  }

  .image {
    width: 200px;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;
  }

  .panel .content {
    padding-right: 15%;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.8s;
  }

  .panel h3 {
    font-size: 1.2rem;
  }

  .panel p {
    font-size: 0.7rem;
    padding: 0.5rem 0;
  }

  .btn.transparent {
    width: 110px;
    height: 35px;
    font-size: 0.7rem;
  }

  .container:before {
    width: 1500px;
    height: 1500px;
    transform: translateX(-50%);
    left: 30%;
    bottom: 68%;
    right: initial;
    top: initial;
    transition: 2s ease-in-out;
  }

  .container.sign-up-mode:before {
    transform: translate(-50%, 100%);
    bottom: 32%;
    right: initial;
  }

  .container.sign-up-mode .left-panel .image,
  .container.sign-up-mode .left-panel .content {
    transform: translateY(-300px);
  }

  .container.sign-up-mode .right-panel .image,
  .container.sign-up-mode .right-panel .content {
    transform: translateY(0px);
  }

  .right-panel .image,
  .right-panel .content {
    transform: translateY(300px);
  }

  .container.sign-up-mode .signin-signup {
    top: 5%;
    transform: translate(-50%, 0);
  }
}

@media (max-width: 570px) {
  form {
    padding: 0 1.5rem;
  }

  .image {
    display: none;
  }

  .panel .content {
    padding: 0.5rem 1rem;
  }

  .container {
    padding: 1.5rem;
  }

  .container:before {
    bottom: 72%;
    left: 50%;
  }

  .container.sign-up-mode:before {
    bottom: 28%;
    left: 50%;
  }
}
</style>