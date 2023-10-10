import axios from "axios";
import {ElMessage} from "element-plus";
import {WsWrapper} from "@/api/WsWrapper";
// region base

const origin = process.env.NODE_ENV === 'development' ? "http://127.0.0.1:8088" : location.origin
const host = process.env.NODE_ENV === 'development' ? "127.0.0.1:8088" : location.host

!((() => {
    let authorization = localStorage.getItem("authorization");
    if (authorization != null && !Number.isNaN(Number(authorization))) {
        axios.defaults.headers.common['authorization'] = authorization
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
 * @returns {WsWrapper}
 */
export function newWebSocket() {
    let ws = new WsWrapper(`ws://${host}/ws`)
    /**
     * @this {WsWrapper}
     * @param ev {WebSocket.CloseEvent}
     */
    ws.onClose = function (ev) {
        this.ws = null;
        if (this.connected) {
            if (ev.code === 1000) {
                ElMessage({
                    message: `断开连接，重连中...`,
                    duration: 1000,
                    showClose: true,
                });
            } else {
                ElMessage({
                    message: `断开连接，重连中...(${ev.code}):${ev.reason}`,
                    type: 'error',
                    showClose: true,
                });
            }
            this.connect()
        } else {
            if (ev.code === 1000) {
                ElMessage({
                    message: `断开连接`,
                    duration: 1000,
                    showClose: true,
                });
            } else {
                ElMessage({
                    message: `断开连接(${ev.code}):${ev.reason}`,
                    type: 'error',
                    showClose: true,
                });
            }
        }
    }
    ws.onError = function (ev) {
        ElMessage({
            message: `连接出错:${ev.message}`,
            type: 'error',
            showClose: true,
        });
    }
    return ws;
}

// region room

/**
 *
 * @typedef {Object} Room
 * @property {string} id -
 * @property {string} name -
 * @property {Map<number,Role>} roles -
 * @property {string} userId -
 * @property {boolean} archive -
 */

/**
 * @typedef {Object} Role
 * @property {number} id -
 * @property {string} name -
 * @property {string} color -
 */
/**
 *
 * @returns {Promise<Array<Room>>}
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
 * @returns {Promise<Room>}
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
    }).then(handleWarning).then(res => {
        localStorage.setItem("authorization", res);
        axios.defaults.headers.common['authorization'] = res
    })
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
 * @returns {Promise<{id: string, username: string, nickname: string}>}
 */
export function userInfo() {
    return axios.request({
        url: "/api/userInfo",
        method: "get",
        baseURL: origin,
    }).then(handleWarning)
}

// endregion
