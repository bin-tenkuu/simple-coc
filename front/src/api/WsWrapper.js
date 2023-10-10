export class WsWrapper {
    /**
     * @type {WebSocket}
     */
    ws;
    /**
     * @type {string}
     */
    url;
    /**
     * @type {boolean}
     */
    connected = false;

    /**
     *
     * @param url {string}
     */
    constructor(url) {
        this.url = url;
    }

    connect() {
        this.connected = true
        let ws = this.ws = new WebSocket(this.url);
        ws.addEventListener('open', ev => {
            return this.onOpen(ev);
        })
        ws.addEventListener('message', ev => {
            return this.onMessage(JSON.parse(ev.data));
        })
        ws.addEventListener('close', ev => {
            return this.onClose(ev);
        })
        ws.addEventListener('error', ev => {
            return this.onError(ev);
        })
    }

    /**
     *
     * @param json {Object}
     * @return {boolean}
     */
    send(json) {
        let ws = this.ws;
        if (ws == null || ws.readyState !== WebSocket.OPEN) {
            return false
        }
        ws.send(JSON.stringify(json));
        return true
    }

    disconnect() {
        this.ws?.close();
        this.connected = false;
    }

    reconnect() {
        this.ws?.close();
        this.connect();
    }

    /**
     * @this {WsWrapper}
     * @param ev {WebSocket.Event}
     */
    onOpen(ev) {
    }

    /**
     * @this {WsWrapper}
     * @param json {*}
     */
    onMessage(json) {
    }

    /**
     * @this {WsWrapper}
     * @param ev {WebSocket.CloseEvent}
     */
    onClose(ev) {
    }

    /**
     * @this {WsWrapper}
     * @param ev {WebSocket.ErrorEvent}
     */
    onError(ev) {
    }

}
