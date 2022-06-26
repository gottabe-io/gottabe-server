import {LitElement, html, customElement, css, property, unsafeCSS} from 'lit-element';
import {style} from '../styles';
import {packageService} from "../services/package-services";
import {BuildDescriptor, PackageDataVO, PackageReleaseVO} from "../types";
import {formatDateAgo} from '../util/utils';
import '../components/md-view';
import sourceIcon from 'url:~/resources/source_icon.png';
import docsIcon from 'url:~/resources/docs_icon.png';
import issuesIcon from 'url:~/resources/issues_icon.png';
import copyIcon from 'url:~/resources/copy_icon.png';

function _removeVersion(dep: string) {
    let _r = /^(([a-z][a-z0-9_-]+)([.][a-z][a-z0-9_-]+)+\/[a-z][a-z0-9_-]+)(@[0-9a-z_.-]+)*$/i.exec(dep);
    return _r ? _r[1] : null;
}

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

            #package_view {
                display: block;
                background: #fff;
                color: #24292f;
            }

            #package_view div.centered {
                display: flex;
                flex-wrap: nowrap;
                flex-direction: row;
                margin-left: auto;
                margin-right: auto;
                width: 75em;
            }

            #package_view div.centered md-view {
                width: 50em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            #package_view div.centered .details {
                width: 25em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            #package_view label {
                color: #555;
                font-weight: bold;
                margin-top: 1.5em;
                margin-bottom: 0.5em;
            }
            
            #package_view a {
                color: #222;
                font-weight: bold;
            }

            #package_view h5, #package_view h6 {
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                font-size: 1em;
                margin-top: 1em;
                margin-bottom: 1em;
            }

            #package_view h5, #package_view h5 a {
                font-family: monospace;
                color: #262a32;
                height: 18px;
            }

            #package_view h5 img.copyIt {
                float: right;
                display: none;
                width: 18px;
                height: 18px;
            }

            #package_view h5:hover img.copyIt {
                display: block;
            }

            #package_view h6 {
                color: #262a32;
            }
            .repository, .documentation, .issues {
                padding-left: 18px;
                background-size: 18px 18px;
                background-repeat: no-repeat;
                background-position: 0px 0px;
            }
            .repository {
                background-image: url(${unsafeCSS(sourceIcon)});
            }
            .documentation {
                background-image: url(${unsafeCSS(docsIcon)});
            }
            .issues {
                background-image: url(${unsafeCSS(issuesIcon)});
            }
        `];
    }

    @property()
    private error?: string;

    @property({attribute: true})
    groupName?: string;

    @property({attribute: true})
    packageName?: string;

    private _package?: PackageDataVO;

    private _readme?: string;

    private _selectedVersion : PackageReleaseVO | undefined;

    private _build: BuildDescriptor | undefined;

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
                            ? ('Latest version ' + this.latestVersion.version + ' released ' + formatDateAgo(this.latestVersion.releaseDate) + ' ago') 
                            : 'No versions released yet!'}</h3>
                </div>
                <div id="package_view">
                    <div class="centered">
                        <md-view mdData="${this._readme}"></md-view>
                        <div class="details">
                            <label>Install</label>
                            <h5>
                                <a href="#" @click="${this._copyInstall}">&gt; gottabe i ${this.groupName}/${this.packageName} <img src="${copyIcon}" class="copyIt"/></a>
                            </h5>
                            <label>Repository</label>
                            <h6 class="repository">
                                <a href="${this._selectedVersion?.sourceUrl}" target="_blank">${this._selectedVersion?.sourceUrl}</a>
                            </h6>
                            <label>Homepage</label>
                            <h6 class="documentation">
                                <a href="${this._selectedVersion?.documentationUrl}" target="_blank">${this._selectedVersion?.documentationUrl}</a>
                            </h6>
                            <label>Issues</label>
                            <h6 class="issues">
                                <a href="${this._selectedVersion?.issuesUrl}" target="_blank">${this._selectedVersion?.issuesUrl}</a>
                            </h6>
                            ${ (this._build?.dependencies?.length || 0) > 0 ? html`<label>Dependencies</label>
                            <ul>
                                ${this._build?.dependencies?.map(dep => html`<li><a href="/packages/${_removeVersion(dep)}">${dep}</a></li>`)}
                            </ul>` : '' }
                        </div>
                    </div>
                </div>
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

    private _copyInstall(e:MouseEvent) {
        e.preventDefault();
        navigator.clipboard.writeText('gottabe i ' + this.groupName+'/'+this.packageName);
    }

    private async _updateData() {
        if (this.groupName && this.packageName) {
            this._package = await packageService.packageInfo(this.groupName, this.packageName, 'ALL_RELEASES');
            this._selectedVersion = this.latestVersion;
            if (this._selectedVersion && this._selectedVersion.version) {
                try {
                    let readmeData = await packageService.getPackageFile(this.groupName, this.packageName, this._selectedVersion.version, 'README.md');
                    this._readme = await readmeData.text();
                    let buildData = await packageService.getPackageFile(this.groupName, this.packageName, this._selectedVersion.version, 'build.json');
                    this._build = JSON.parse(await buildData.text());
                } catch(e:any) {
                    console.error(e.message);
                }
            }
        }
        await this.performUpdate();
    }

    private get latestVersion() : PackageReleaseVO | undefined {
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
