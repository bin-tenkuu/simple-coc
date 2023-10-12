<template>
    <div class="chatLogs">
        <div>
            <span>&lt;名字&gt;：</span>
            <span v-html="message"></span>
        </div>
    </div>
    <el-divider>
        <el-icon>
            <StarFilled/>
        </el-icon>
    </el-divider>
    <TEditor ref="editor" v-model="message" @input="updateContent"/>
</template>

<script>
import {Edit, Key, Plus, StarFilled} from '@element-plus/icons-vue'
import TEditor from "@/component/TEditor.vue";

export default {
    name: 'chat2-page',
    components: {TEditor, Key, Edit, Plus, StarFilled},
    data() {
        return {
            message: "123",
        }
    },
    methods: {
        updateContent(message) {
            console.log(message)
            this.message = message
        },
    }
}
</script>
<!--suppress CssUnusedSymbol -->
<style>
html, body {
    --color: black;
    height: 99%;
    width: 100%;
    margin: 0;
    padding: 0;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
}

#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: left;
    color: #2c3e50;
    width: 100%;
}

.chatLogs > div > :last-child > :first-child {
    display: inline;
}
</style>
