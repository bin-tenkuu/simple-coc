<template>
    <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            :ellipsis="false"
            mode="horizontal"
    >
        <el-menu-item index="login" @click="handleSelect">登陆</el-menu-item>
        <el-menu-item index="admin" @click="handleSelect">管理房间</el-menu-item>
        <el-menu-item index="chat" @click="handleSelect">进入房间</el-menu-item>
        <!--<div style="flex-grow: 1;"/>-->
        <el-sub-menu index="user">
            <template #title>
                <template v-if="loginName == null">
                    未登录
                </template>
                <template v-else>
                    用户:{{ loginName }}
                </template>
            </template>
            <template v-if="loginName != null">
                <el-menu-item index="1">个人中心</el-menu-item>
                <el-menu-item index="2" @click="handleLogout">退出</el-menu-item>
            </template>
        </el-sub-menu>
    </el-menu>
</template>
<script>

import {logout, userInfo} from "@/api/api";

export default {
    name: "head-menu",
    props: {
        activeIndex: {
            type: String,
            default: "login"
        }
    },
    data() {
        userInfo().then((name) => {
            this.loginName = name
        })
        return {
            loginName: null,
            route: {
                login: "/login.html",
                admin: "/admin.html",
                chat: "/chat.html",
            }
        }
    },
    methods: {
        handleSelect(key) {
            location.href = this.route[key.index]
        },
        handleLogout() {
            logout().finally()
        }
    }
}
</script>
