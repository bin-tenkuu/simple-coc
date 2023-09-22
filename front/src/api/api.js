import axios from "axios";
import {ElMessage} from "element-plus";
// region base

const origin = process.env.NODE_ENV === 'development' ? "http://127.0.0.1:8088" : location.origin
const host = process.env.NODE_ENV === 'development' ? "127.0.0.1:8088" : location.host

!((() => {
    let authorization = localStorage.getItem("authorization");
    if (authorization != null && !Number.isNaN(Number(authorization))) {
        axios.defaults.headers.common['authorization'] = authorization
    } else {
        getId().then(res => {
            localStorage.setItem("authorization", res);
            axios.defaults.headers.common['authorization'] = res
        })
    }
})());

/**
 *
 * @param res {axios.AxiosResponse<*>}
 * @returns {*}
 */
function handleWarning(res) {
    let data = res.data;
    if (!data.code) {
        return data.data
    } else if (data.code === 1) {
        ElMessage({
            message: `失败:${data.msg}`,
            type: 'error',
            showClose: true
        });
    }
    return Promise.reject(data.msg)
}

// endregion

/**
 * @param {string} id
 * @returns {WebSocket}
 */
export function newWebSocket(id) {
    return new WebSocket(`ws://${host}/ws/${id}`);
}

// region room

/**
 *
 * @returns {Promise<Array<{id: string, name: string}>>}
 */
export function getRooms() {
    return axios.request({
        url: "/api/room/list",
        method: "get",
        baseURL: origin
    }).then(handleWarning);
}

/**
 *
 * @param {string?} id
 * @returns {Promise<{id: string, name: string, roles:any, archive: boolean}>}
 */
export function getRoom(id) {
    return axios.request({
        url: "/api/room/info",
        method: "get",
        baseURL: origin,
        params: {
            id: id,
        }
    }).then(handleWarning);
}

/**
 *
 * @param {{id:string,name:string}} room
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export function saveRoom(room) {
    return axios.request({
        url: "/api/room/info",
        method: "post",
        baseURL: origin,
        data: room
    }).then(handleWarning);
}

/**
 * @param {string} id
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export function deleteRoom(id) {
    return axios.request({
        url: "/api/room/del",
        method: "get",
        baseURL: origin,
        params: {
            id: id,
        },
    }).then(handleWarning);
}

/**
 * @param {string} id
 */
export function downloadLog(id) {
    window.open(
        `${origin}/api/room/logs?id=${id}`,
        '_self'
    );
}

// endregion
// region user

/**
 *
 * @param username {string}
 * @param password {string}
 * @returns {Promise<boolean>}
 */
export function login(username, password) {
    return axios.request({
        url: "/api/login",
        method: "post",
        baseURL: origin,
        data: {
            username: username,
            password: password
        }
    }).then(handleWarning)
}

export function logout() {
    return axios.request({
        url: "/api/logout",
        method: "get",
        baseURL: origin,
    }).then(handleWarning)
}

/**
 *
 * @returns {Promise<string>}
 */
export function userInfo() {
    return axios.request({
        url: "/api/userInfo",
        method: "get",
        baseURL: origin,
    }).then(handleWarning)
}

/**
 *
 * @returns {Promise<string>}
 */
function getId() {
    return axios.request({
        url: "/api/id",
        method: "get",
        baseURL: origin
    }).then(handleWarning);
}

// endregion
// region chat

/**
 *
 * @param roomId {string}
 * @param msgId {number}
 * @returns {Promise<Array<{type:string, msg:string, role:string}>>}
 */
export function getHistoryMsg(roomId, msgId) {
    return axios.request({
        url: "/api/chat/historyMsg",
        method: "get",
        baseURL: origin,
        params: {
            roomId: roomId,
            msgId: msgId
        }
    }).then(handleWarning)
}

// endregion
