import {LitElement, html, customElement, css, property} from 'lit-element';
import {style} from '../styles';
import packageService from "../services/package-services";
import {PackageData, PackageRelease} from "../types";
import {formatDateAgo} from '../util/utils';

@customElement("package-page")
class PackagePage extends LitElement {

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
    groupName?: string;

    @property({attribute: true})
    packageName?: string;

    private _package?: PackageData;

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
            ${ this._package ? html`
                <div>
                    <h2>${this.groupName}/${this.packageName}</h2>
                    <h3>${this.latestVersion 
                            ? ('Latest version ' + this.latestVersion.version + ' released ' + formatDateAgo(this.latestVersion.releaseDate)) 
                            : 'No versions released yet!'}</h3>
                </div>
                <pre>
                    ${JSON.stringify(this._package, null, 4)}     
                </pre>
            ` : html`
                <h2>Not found!</h2>
                <h3>The package you are looking for may not exist!</h3>
            `}
            </div>
		`;
    }

    constructor() {
        super();
    }

    connectedCallback() {
        super.connectedCallback();
        this._updateData().catch(((e:any) => this.error = e.message).bind(this));
    }

    private async _updateData() {
        this._package = await packageService.package(this.groupName, this.packageName, {fetch: 'ALL_RELEASES'});
        await this.performUpdate();
    }

    private get latestVersion() : PackageRelease | undefined {
        let releases = this._package?.releases;
        if (releases && releases.length) {
            return releases[releases.length - 1];
        }
        return undefined;
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'package-page': PackagePage;
    }
}
