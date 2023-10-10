<template>
    <head-menu activeIndex="userInfo"/>
    <el-collapse v-model="collapse.activeName">
        <el-collapse-item name="action">
            <template #title>
                <el-icon>
                    <star-filled/>
                </el-icon>
                操作
                <el-icon>
                    <star-filled/>
                </el-icon>
            </template>
        </el-collapse-item>
        <el-collapse-item name="password">
            <template #title>
                <el-icon>
                    <star-filled/>
                </el-icon>
                修改密码
                <el-icon>
                    <star-filled/>
                </el-icon>
            </template>
            <el-form>
                <el-form-item>
                    <el-input
                            type="text"
                            v-model="password.oldPassword"
                            placeholder="旧密码(1-20字符)"
                            minlength="1"
                            maxlength="20"
                            show-password
                            autofocus
                            clearable
                    >
                        <template #prepend>
                            旧密码
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-input
                            type="text"
                            v-model="password.newPassword"
                            placeholder="新密码(1-20字符)"
                            minlength="1"
                            maxlength="20"
                            show-password
                            autofocus
                            clearable
                    >
                        <template #prepend>
                            新密码
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-input
                            type="text"
                            v-model="password.confirmPassword"
                            placeholder="重复新密码(1-20字符)"
                            minlength="1"
                            maxlength="20"
                            show-password
                            autofocus
                            clearable
                    >
                        <template #prepend>
                            重复新密码
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
        </el-collapse-item>
    </el-collapse>
</template>
<script>
import HeadMenu from "@/component/headMenu.vue";
import {StarFilled} from "@element-plus/icons-vue";

export default {
    name: 'user-info',
    components: {StarFilled, HeadMenu},
    data() {
        return {
            collapse: {
                activeName: ["action", "password"]
            },
            password: {
                oldPassword: "",
                newPassword: "",
                confirmPassword: ""
            }
        }
    },
    methods: {}
}
</script>

