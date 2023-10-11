<template>
    <div style="width: 100%;" class="chatLogs">
        <div v-html="message"></div>
    </div>
</template>
<script>
export default {
    name: "editor-test",
    data() {
        return {
            message: ''
        }
    },
    methods: {}
}
</script>
<style>

.chatLogs > div:empty {
    display: none;
}

.chatLogs > div {
    color: var(--color);
    padding: 0;
    display: flex;
    margin: 5px;
}

.chatLogs > div.edit:hover {
    border: 1px solid #a0cfff;
    cursor: pointer;
}

.chatLogs > div.edit {
    border: 0;
    outline: 0;
}

.chatLogs > div:hover > .el-icon {
    display: inline;
    color: #a0cfff;
}

.chatLogs > div > .el-icon {
    display: none;
}

.chatLogs > div > :nth-child(1) {
    font-weight: bold;
    margin-right: 10px;
}

.chatLogs > div > :nth-child(2) {
    white-space: normal;
    word-wrap: break-word;
    overflow-wrap: break-word;
}

img {
    max-width: 70%;
    max-height: 70%;
    vertical-align: top;
}

</style>
