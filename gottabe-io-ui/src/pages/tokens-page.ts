import {LitElement, html, customElement, css, property} from 'lit-element';
import {ManagedTokenVO} from "../types";
import {style} from '../styles';
import {userService} from "../services/user-services";

@customElement("tokens-page")
class TokensPage extends LitElement {

    static get styles() {
        return [style,css`
			:host {
				display: block;
				align-items: center;
				width: 100%;
                color: aliceblue;
			}
            table {
                width: 100%;
                border: none;
            }
            th {
                background: #585c64;
                border: none;
            }
            td {
                background: #383c44;
                border: none;
            } 
            td.actions {
                text-align: center;
            }
            a {
                color: white;
                text-decoration: none;
                font-weight: bold;
            }
            a:hover {
                color: yellow;
            }
		`];
    }

    @property()
    tokens: Array<ManagedTokenVO>;

    private error?: string;

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
                <button @click="${this._createToken}">Create Token</button>
                <table>
                    <tr><th>Token</th><th>Creation</th><th>Actions</th></tr>
                    ${this.tokens.map((this._token).bind(this))}
                </table>
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

    private _token(token: ManagedTokenVO, i: number) {
        if (!token) return html``;
        const clickRevoke = (_event: MouseEvent) => {
            this._revokeToken(this.tokens[i]);
        };
        return html`
            <tr>
                <td>${token.token}</td>
                <td>${token.creationDate ? new Date(Date.parse(token.creationDate)).toLocaleString() : ''}</td>
                <td class="actions"><a href="#delete" @click="${clickRevoke.bind(this)}">Revoke</a></td>
            </tr>
        `;
    }

    private async _revokeToken(token: ManagedTokenVO) {
        if (token.token) {
            await userService.removeToken(token.token);
        }
        await this._updateData();
    }

    private async _updateData() {
        this.tokens = await userService.listTokens();
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
