<template>
    <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
    >
        <template v-if="userInfo.id == null">
            <el-sub-menu>
                <template #title>
                    选择登录/注册
                </template>
                <el-menu-item index="login" @click="handleSelect">
                    <template #title>
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>登陆</span>
                    </template>
                </el-menu-item>
                <!--<el-menu-item index="register" @click="handleSelect">
                    <template #title>
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>注册</span>
                    </template>
                </el-menu-item>-->
            </el-sub-menu>
        </template>
        <template v-else>
            <el-sub-menu>
                <template #title>
                    <el-icon>
                        <User/>
                    </el-icon>
                    用户:{{ userInfo.nickname }}
                </template>
                <el-menu-item index="userInfo" @click="handleSelect">
                    <template #title>
                        <el-icon>
                            <Tools/>
                        </el-icon>
                        <span>个人中心</span>
                    </template>
                </el-menu-item>
                <el-menu-item index="logout" @click="handleLogout">
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
        userInfo().then((userInfo) => {
            this.userInfo = userInfo
            console.log(this.userInfo, '|', userInfo)
        })
        return {
            userInfo: {
                id: null,
                username: null,
                nickname: null
            },
            route: {
                login: "/login.html",
                admin: "/admin.html",
                chat: "/chat.html",
                userInfo: "/userInfo.html",
                register: "/register.html",
            }
        }
    },
    methods: {
        handleSelect(key) {
            let routeElement = this.route[key.index];
            if (routeElement != null) {
                location.href = routeElement
            } else {
                location.href = "/"
            }
        },
        handleLogout() {
            logout().finally()
            location.href = this.route["login"]
        }
    }
}
</script>
