<template>
  <el-form>
    <el-form-item>
      <el-input v-model="username" placeholder="请输入用户名" clearable>
        <template #prepend>用户名：</template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <el-input v-model="password" placeholder="请输入密码" clearable>
        <template #prepend>密码：</template>
      </el-input>
    </el-form-item>
  </el-form>
  <el-button @click="postLogin" size="large" type="primary">
    登录
  </el-button>
</template>
<script>
import {ElMessage} from "element-plus";
import {login} from "@/api/api";

export default {
  name: "login-page",
  setup() {

  },
  data() {
    return {
      username: "",
      password: ""
    }
  },
  methods: {
    postLogin() {
      login(this.username, this.password).then((res) => {
        if (res) {
          ElMessage.success("登录成功");
          location.href = "/admin.html"
        } else {
          ElMessage.error("登录失败");
        }
      }).catch(() => {
        ElMessage.error("登录失败");
      })
    }
  },
}
</script>

<style scoped>

</style>
