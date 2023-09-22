<template>
    <head-menu activeIndex="userInfo"/>
    <el-divider>
        <el-icon>
            <star-filled/>
        </el-icon>
        操作
        <el-icon>
            <star-filled/>
        </el-icon>
    </el-divider>
    <el-divider>
        <el-icon>
            <star-filled/>
        </el-icon>
        用户信息修改
        <el-icon>
            <star-filled/>
        </el-icon>
    </el-divider>
    <el-form>

    </el-form>

</template>
<script>
import HeadMenu from "@/component/headMenu.vue";
import {StarFilled} from "@element-plus/icons-vue";

export default {
    name: 'user-info',
    components: {StarFilled, HeadMenu},
    data() {
        return {}
    },
    methods: {}
}
</script>

