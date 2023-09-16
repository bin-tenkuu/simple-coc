import axios from "axios";

const origin = process.env.NODE_ENV === 'development' ? "http://127.0.0.1:8088" : location.origin
const host = process.env.NODE_ENV === 'development' ? "127.0.0.1:8088" : location.host

/**
 *
 * @param res {axios.AxiosResponse<*>}
 * @returns {*}
 */
function handleWarning(res) {
    let header = res.headers['Warning'];
    if (header) {
        console.log(header)
    }
    return res.data
}

/**
 *
 * @returns {Promise<Array<{id: string, name: string}>>}
 */
export function getRooms() {
    return axios.request({
        url: "/api/rooms",
        method: "get",
        baseURL: origin
    }).then(handleWarning);
}

/**
 *
 * @param {string?} id
 * @returns {Promise<Array<{id: string, name: string}>>}
 */
export function getRoom(id) {
    return axios.request({
        url: "/api/room",
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
        url: "/api/room",
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


/**
 * @param {string} id
 * @returns {WebSocket}
 */
export function newWebSocket(id) {
    return new WebSocket(`ws://${host}/ws/${id}`);
}

/**
 *
 * @param username {string}
 * @param password {string}
 * @returns {Promise<boolean>}
 */
export function login(username, password) {
    return axios.request({
        url: "/login",
        method: "post",
        baseURL: origin,
        data: {
            username: username,
            password: password
        }
    }).then(handleWarning)
}
