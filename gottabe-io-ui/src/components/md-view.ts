import {LitElement, html, customElement, css, property} from 'lit-element';
import {unsafeHTML} from 'lit-html/directives/unsafe-html.js';
import markdown from '@wcj/markdown-to-html';
import deepmerge from 'deepmerge';
import sanitize, {defaultSchema} from 'rehype-sanitize';

const schema = deepmerge(defaultSchema, {attributes: {'*': ['className']}});

@customElement("md-view")
export class MdViewElement extends LitElement {

    static get styles() {
        return [css`
            :host {
                display: block;
            }
		`];
    }

    @property({attribute: true})
    mdData?: string;

    render() {
        return html`
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.15.0/dist/katex.min.css">
            <link rel="stylesheet" href="https://unpkg.com/@wcj/markdown-to-html/dist/marked.css">
            <div class="markdown-body">
                ${unsafeHTML(markdown(this.mdData, {rehypePlugins: [[sanitize, schema]]}))}
            </div>
		`;
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'md-view': MdViewElement;
    }
}
