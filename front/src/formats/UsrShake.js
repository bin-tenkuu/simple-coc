import {Quill} from "@vueup/vue-quill";

const Embed = Quill.import('blots/embed');

export class UsrShake extends Embed {
    static blotName = "shake"
    static tagName = "span"
    static className = "usr-shake-parent"
    // static allowedChildren = [Embed]

    constructor(node) {
        super(node);
        let contentNode = this.contentNode;
        contentNode.className = "usr-shake"
    }

    static create(value) {
        /**
         * @type {HTMLSpanElement}
         */
        let node = super.create();
        // node.setAttribute('contenteditable', 'false')
        // node.setAttribute("class","usr-shake")
        if (typeof value === 'string') {
            node.innerHTML = value;
        }
        return node;
    }

    static formats() {
        return true;
    }

    /**
     *
     * @param {HTMLSpanElement} domNode
     */
    static value(domNode) {
        return domNode.children.item(0).innerHTML;
    }

}
