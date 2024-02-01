import {Quill} from "@vueup/vue-quill";

const Embed = Quill.import('blots/embed');

export class UsrShake extends Embed {
    static blotName = "shake"
    static tagName = "span"
    // static className = "usr-shake"
    static allowedChildren = [Embed, UsrShake]

    constructor(node) {
        super(node);
        let contentNode = this.contentNode;
        contentNode.className = "usr-shake"
    }

    static create(value) {
        let node = super.create();
        node.setAttribute('contenteditable', 'false');
        if (typeof value === 'string') {
            node.innerHTML = this.transformValue(value);
        }
        return node;
    }

    static transformValue(value) {
        return value.replace(/(.) */g, "<span>$1</span>")
    }

    static formats() {
        return true;
    }

    static value(domNode) {
        return domNode.innerText.replace(/<\/?span[^>]*>/g, "");
    }

}
