<template>
    <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
    >
        <template v-if="loginName == null">
            <el-menu-item index="login" @click="handleSelect">
                <template #title>
                    <el-icon>
                        <House/>
                    </el-icon>
                    <span>登陆</span>
                </template>
            </el-menu-item>
        </template>
        <template v-else>
            <el-sub-menu index="user">
                <template #title>
                    <el-icon>
                        <User/>
                    </el-icon>
                    用户:{{ loginName }}
                </template>
                <el-menu-item index="1">
                    <template #title>
                        <el-icon>
                            <Tools/>
                        </el-icon>
                        <span>个人中心</span>
                    </template>
                </el-menu-item>
                <el-menu-item index="2" @click="handleLogout">
                    <template #title>
                        <el-icon>
                            <CloseBold/>
                        </el-icon>
                        <span>退出</span>
                    </template>
                </el-menu-item>
            </el-sub-menu>
        </template>
        <el-menu-item index="admin" @click="handleSelect">
            <template #title>
                <el-icon>
                    <Guide/>
                </el-icon>
                <span>管理房间</span>
            </template>
        </el-menu-item>
        <el-menu-item index="chat" @click="handleSelect">
            <template #title>
                <el-icon>
                    <ChatLineSquare/>
                </el-icon>
                <span>进入房间</span>
            </template>
        </el-menu-item>
        <!--<div style="flex-grow: 1;"/>-->
    </el-menu>
</template>
<script>

import {logout, userInfo} from "@/api/api";
import {ChatLineSquare, CloseBold, Guide, House, Tools, User} from "@element-plus/icons-vue";

export default {
    name: "head-menu",
    components: {Tools, CloseBold, User, Guide, ChatLineSquare, House},
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
