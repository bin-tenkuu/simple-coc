<template>
    <head-menu activeIndex="admin"/>
    <el-form>
        <el-form-item label="房间Id:">
            <el-select v-model="room.id"
                       allow-create default-first-option
                       filterable clearable
            >
                <el-option v-for="it in rooms" :key="it.id" :label="it.id" :value="it.id" @click="getRoom(it.id)">
                    <span style="float: left">{{ it.id }}</span>
                    <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">
                        {{ it.name }}
                    </span>
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="房间名:" style="width: 25%;">
            <el-input v-model="room.name" :disabled="!room.enable"></el-input>
        </el-form-item>
        <el-tooltip
                class="box-item"
                effect="dark"
                placement="bottom-start"
        >
            <template #content>
                归档后，房间将不再接收任何消息，但是可以继续查看历史消息
            </template>
            <el-form-item label="归档:">
                <el-switch v-model="room.archive" :disabled="!room.enable"></el-switch>
            </el-form-item>
        </el-tooltip>
        <span class="dialog-footer">
            <el-button type="primary" @click="setRoom" :disabled="!room.enable">保存房间</el-button>
            <el-button type="info" @click="downloadLog">导出日志</el-button>
            <el-button type="danger" @click="deleteRoom" :disabled="!room.enable">删除房间</el-button>
            <el-button type="info" @click="downloadRawLog">导出原始日志</el-button>
        </span>
    </el-form>
    <el-table
            :data="Object.values(room.roles)"
            style="width: 100%;"
            table-layout="auto"
            border stripe
    >
        <el-table-column fixed prop="id" label="id" style="min-width: 50px"/>
        <el-table-column prop="name" label="name" style="min-width: 100px">
            <template #default="{ row }">
                <span v-bind:style="{ color: row.color }">{{ row.name }}</span>
            </template>
        </el-table-column>
        <el-table-column label="color">
            <template #default="{ row }">
                <el-color-picker v-model="row.color" show-alpha></el-color-picker>
            </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="240" v-if="room.enable">
            <template #default="{row}">
                <el-button
                        type="info"
                        @click="editRole(row.id)"
                        :disabled="!room.enable"
                >
                    编辑
                </el-button>
                <el-button
                        type="danger"
                        @click="deleteRole(row.id)"
                        :disabled="!room.enable"
                >
                    删除
                </el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-button style="width: 100%" @click="roleDialog.visible=true" :disabled="!room.enable">
        Add Item
    </el-button>
    <el-dialog v-model="roleDialog.visible">
        <el-form>
            <el-form-item label="id" required>
                <el-tooltip
                        class="box-item"
                        effect="dark"
                        placement="bottom-start"
                >
                    <template #content>
                        bot 使用 -10，未设置时不接入 bot<br>
                        默认角色 使用 -1，未设置时丢弃未知角色消息，否则使用-1属性创建角色并发送
                    </template>
                    <el-input-number
                            v-model="roleDialog.id"
                            :min="-9999"
                            :max="9999"
                            :step="1"
                            :precision="0"
                            :step-strictly="true"
                    />
                </el-tooltip>
            </el-form-item>
            <el-form-item label="name" required>
                <el-input v-model="roleDialog.name"/>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
              <el-button @click="roleDialog.visible = false">取消</el-button>
              <el-button type="primary" @click="addRole">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
import {ElNotification} from "element-plus";
import {deleteRoom, downloadLog, downloadRawLog, getRoom, getRooms, saveRoom} from "@/api/api";
import HeadMenu from "@/component/headMenu.vue";

export default {
    name: 'Admin-page',
    components: {HeadMenu},
    data() {
        getRooms().then(data => {
            this.rooms = data
        })
        return {
            /**
             * @type {Array<{id: string, name: string}>}
             */
            rooms: [],
            loading: true,
            room: {
                id: "default",
                name: "",
                /**
                 * @type {Record<number, Role>}
                 */
                roles: {},
                archive: false,
                enable: true
            },
            roleDialog: {
                visible: false,
                id: 1,
                name: "",
            },
            dialogVisible: false,
        }
    },
    methods: {
        getRoom(id) {
            this.dialogVisible = false
            this.room.id = id
            getRoom(id).then(data => {
                this.room = data
            })
        },
        setRoom() {
            saveRoom(this.room).then(() => {
                ElNotification({
                    title: '成功',
                    message: '保存成功',
                    type: 'success',
                    position: 'top-left',
                });
            })
        },
        downloadLog() {
            downloadLog(this.room.id)
        },
        downloadRawLog() {
            downloadRawLog(this.room.id)
        },
        deleteRoom() {
            deleteRoom(this.room.id).then(() => {
                ElNotification({
                    title: '成功',
                    message: '删除成功',
                    type: 'success',
                    position: 'top-left',
                });
            })
        },
        editRole(id) {
            let roleDialog = this.roleDialog;
            roleDialog.id = id;
            roleDialog.name = this.room.roles[id].name;
            this.roleDialog.visible = true
        },
        addRole() {
            let roleDialog = this.roleDialog;
            const {id, name} = roleDialog;
            /**@type {{id: number, name: string, color: string}}*/
            let role = this.room.roles[id]
            if (role == null) {
                role = this.room.roles[id] = {
                    id: 1,
                    name: "",
                    color: "",
                }
            }
            role.id = id;
            role.name = name;
            roleDialog.visible = false
        },
        deleteRole(id) {
            delete this.room.roles[id]
        },
    }
}
</script>
<style>

</style>
