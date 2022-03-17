import {LitElement, html, customElement, css, property} from 'lit-element';
import {ManagedToken} from "./types";
import {style} from './styles';
import userService from "./user-services";

@customElement("tokens-page")
class TokensPage extends LitElement {

    static get styles() {
        return [style,css`
			:host {
				display: block;
				align-items: center;
				width: 100%;
			}
		`];
    }

    @property()
    tokens: Array<ManagedToken>;

    private error?: string;

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
                <button @click="${this._createToken}">Create Token</button>
                <div>
                    ${this.tokens.map(this._token)}                
                </div>
            </div>
		`;
    }

    constructor() {
        super();
        this.tokens = [];
    }

    connectedCallback() {
        super.connectedCallback();
        this._updateData().catch(((e:any) => this.error = e.message).bind(this));
    }

    private _token(token: ManagedToken) {
        return html`
            <div>
                <div>${token.token}</div>
                <div>${token.expireDate}</div>
                <div><a href="#delete" @click="${this._revokeToken}"></div>
            </div>
        `;
    }

    private _revokeToken(event: MouseEvent) {
        console.log(event);
    }

    private async _updateData() {
        this.tokens = await userService.tokens();
        await this.performUpdate();
    }

    private async _createToken() {
        await userService.createToken();
        await this._updateData();
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'tokens-page': TokensPage;
    }
}
