import {LitElement, html, customElement, css, property} from 'lit-element';

@customElement("dropdown-menu")
export class DropdownMenuElement extends LitElement {

    @property()
    set options(value: any[] | string) {
        if (typeof value == 'string') {
            this._options = JSON.parse(value);
        } else if (value) {
            this._options = value;
        }
    }

    private _options: any[];

    @property()
    value: any;

    @property({type: String, attribute: true})
    labelProperty?: string;

    private closed: boolean;

    static get styles() {
        return [css`
            :host {
                font-size: 18px;
            }
            .dd-container {
                display: flex;
                flex-direction: column;
                background-color: transparent;
                user-select: none;
            }
            .dd-head {
                display: flex;
                flex-wrap: nowrap;
            }
            .dd-label {
                color: #eee;
                font-weight: bold;
                margin-top: 1.5em;
                margin-bottom: 0.5em;                
            }
            .dd-choice {
                width: calc(100% - 18px);
                padding: 4px;
                border: 1px solid #555;
                background: rgb(51, 51, 51);
                border-radius: 6px 0 0 6px;
            }
            .dd-toggle {
                width: 18px;
                padding: 4px;
                border-top: 1px solid #555;
                border-right: 1px solid #555;
                border-bottom: 1px solid #555;
                border-radius: 0 6px 6px 0;
                background-color: #444;
                color: #999;
            }
            .dd-toggle:hover, .dd-toggle.open {
                background-color: #555;
                color: #ccc;
            }
            .dd-toggle:before {
                content: '▼';
            }
            .dd-body {
                display: none;
                position: absolute;
                background-color: #444;
                border: 1px solid #555;
            }
            .dd-body.open {
                display: block;
            }
            .dd-option {
                color: #aaa;
            }
            .dd-option:hover {
                background-color: #555;
                color: #ccc;
            }
        `];
    }

    render() {
        return html`
            <div class="dd-container">
                <div class="dd-label">${this.title}</div>
                <div class="dd-head">
                    <div class="dd-choice">${this._getLabel(this.value)}</div>
                    <div class="dd-toggle ${this.closed ? '' : 'open'}" @click="${this._toggleDropdown}"></div>
                </div>
                <div>
                    <div class="dd-body ${this.closed ? '' : 'open'}" style="width: ${(this.offsetWidth - 29) + 'px'}">
                        ${this._options.map(option => html`<div class="dd-option" @click="${this._handleSelect}" .data="${option}">${this._getLabel(option)}</div>`)}
                    </div>
                </div>
            </div>
		`;
    }

    constructor() {
        super();
        this._options = [];
        this.closed = true;
    }

    private _getLabel(option: any) {
        return this.labelProperty ? option[this.labelProperty] : option;
    }

    connectedCallback() {
        super.connectedCallback();
    }

    _toggleDropdown() {
        this.closed = !this.closed;
        this.performUpdate();
    }

    _handleSelect(e:MouseEvent) {
        this.value = (<any>e.target).data;
        this.closed = true;
        this.performUpdate();
        let change = new CustomEvent('change', {
            detail: this.value,
            bubbles: true,
            composed: true });
        this.dispatchEvent(change);
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'dropdown-menu': DropdownMenuElement;
    }
}
