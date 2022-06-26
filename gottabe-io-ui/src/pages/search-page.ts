import {LitElement, html, customElement, css, property} from 'lit-element';
import {style} from '../styles';
import {packageService} from "../services/package-services";
import {PackageDataVO} from "../types";

@customElement("search-page")
class SearchPage extends LitElement {

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
          td.center {
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
          .buttons {
            display: flex;
          }
          .form-field + .form-field {
            margin-top: 8px;
          }
        `];
    }

    @property()
    private error?: string;

    @property({attribute: true})
    set query(q: string) {
        this._query = q;
        console.log('Search: ' + q);
        this._updateData();
    }

    _query?: string;

    private packages: PackageDataVO[];

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
                <div>
                    <h2>Matches for &quot;${this.query}&quot;</h2>
                </div>
            ${ this.packages.length > 0 ? html`
                <table>
                    <tr><th>Package</th><th>Latest Version</th><th>Date</th><th>Owner</th></tr>
                    ${this.packages.map((this._package).bind(this))}
                </table>
            ` : html`
                <h2>No results found!</h2>
            `}
            </div>
		`;
    }

    constructor() {
        super();
        this.packages = [];
    }

    connectedCallback() {
        super.connectedCallback();
        this._updateData().catch(((e:any) => this.error = e.message).bind(this));
    }

    private async _updateData() {
        if (this.query)
            this.packages = await packageService.groupPackages(this.query,undefined, 'LATEST_RELEASE');
        await this.performUpdate();
    }

    private _package(pack: PackageDataVO, _i: number) {
        if (!pack) return html``;
        return html`
            <tr>
                <td><a href="/packages/${pack.group?.name}/${pack.name}">${pack.group?.name}/${pack.name}</a></td>
                <td class="center">${(pack.releases && pack.releases[0]) ? html`<a href="/packages/${pack.group?.name}/${pack.name}/${(pack.releases && pack.releases[0])?.version}">${(pack.releases && pack.releases[0])?.version}</a>` : html`---`}</td>
                <td class="center">${(pack.releases && pack.releases[0]) ? this.formatDate((pack.releases && pack.releases[0])?.releaseDate) : html`---`}</td>
                <td class="center">${pack.group?.owner?.nickname}</td>
            </tr>
        `;
    }

    formatDate(dateStr?: string) {
        if (dateStr)
            return new Date(Date.parse(dateStr)).toLocaleString();
        return '';
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'search-page': SearchPage;
    }
}
