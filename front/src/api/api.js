import axios from "axios";

export const origin = process.env.NODE_ENV === 'development' ? "http://127.0.0.1:8088" : location.origin

/**
 *
 * @returns {Promise<Array<{id: string, name: string}>>}
 */
export function getRooms() {
    return axios.request({
        url: "/api/rooms",
        method: "get",
        baseURL: origin
    }).then(res => res.data);
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
    }).then(res => res.data);
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
    });
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
    });
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
