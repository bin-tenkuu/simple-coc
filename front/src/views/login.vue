<template>
    <head-menu activeIndex="login"/>
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
import {login, userInfo} from "@/api/api";
import HeadMenu from "@/component/headMenu.vue";

export default {
    name: "login-page",
    components: {HeadMenu},
    setup() {
        userInfo().then((name) => {
            this.loginName = name
        })
        return {
            loginName: null
        }
    },
    data() {
        return {
            username: "",
            password: ""
        }
    },
    methods: {
        postLogin() {
            login(this.username, this.password).then(() => {
                ElMessage.success("登录成功");
                location.href = "/admin.html"
            })
        }
    },
}
</script>

<style scoped>

</style>
