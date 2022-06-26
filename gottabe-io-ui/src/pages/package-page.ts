import {LitElement, html, customElement, css, property, unsafeCSS} from 'lit-element';
import {style} from '../styles';
import {packageService} from "../services/package-services";
import {reviewsService} from "../services/review-services";
import {BuildDescriptor, PackageDataVO, PackageReleaseVO, StarsVO} from "../types";
import {formatDateAgo, formatDate} from '../util/utils';
import '../components/md-view';
import '../components/my-dropdown-menu';
import sourceIcon from 'url:~/resources/source_icon.svg';
import docsIcon from 'url:~/resources/docs_icon_18x18.png';
import issuesIcon from 'url:~/resources/issues_icon.png';
import copyIcon from 'url:~/resources/copy_icon.png';
import starsOn from 'url:~/resources/stars_on.png';
import starsOff from 'url:~/resources/stars_off.png';

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

            div.centered {
                display: flex;
                flex-wrap: nowrap;
                flex-direction: row;
                margin-left: auto;
                margin-right: auto;
                width: 75em;
            }

            #package_head {
                width: 50em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            dropdown-menu {
                width: 25em;
                margin-left: 1rem;
                margin-right: 1rem;
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
                font-weight: bold;
                color: #eee;
                height: 18px;
            }
            
            #package_view h5 {
                margin-top: 0px;
                margin-bottom: 0px;
                padding-top: 1em;
                padding-left: 0.5em;
                padding-bottom: 1em;
                background-color: #282c34;
                border: 1px solid #383c44;
                border-radius: 6px;
            }

            #package_view h5 img.copyIt {
                float: right;
                display: none;
                width: 18px;
                height: 18px;
            }

            #package_view h5:hover {
                background-color: #383c48;
                border: 1px solid #585c74;
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
            ul.stars {
                display: flex;
                flex-wrap: nowrap;
                padding: 0px;
                margin: 0px;
            }
            ul.stars li {
                text-decoration: none;
                list-style-type: none;
            }
            ul.stars li + li {
                margin-left: 12px;
            }
            ul.stars li.star {
                background-image: url(${unsafeCSS(starsOff)});
                width: 90px;
                height: 18px;
            }
            ul.stars li.star span {
                height: 18px;
                background-image: url(${unsafeCSS(starsOn)});
                display: block;
            }
            .review {
                border-radius: 6px;
                background-color: bisque;
                padding: 8px;
                margin: 4px;
            }
            .unformatted {
                max-height: 48px;
                overflow: hidden;
                text-overflow: ellipsis;
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

    private _stars: StarsVO | undefined;

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
            ${ this._package ? html`
                <div>
                    <div class="centered">
                        <div id="package_head">
                            <h2>${this.groupName}/${this.packageName}</h2>
                            <h3>${this.latestVersion 
                                    ? ('Latest version ' + this.latestVersion.version + ' released ' + formatDateAgo(this.latestVersion.releaseDate) + ' ago') 
                                    : 'No versions released yet!'}</h3>
                        </div>
                        <dropdown-menu title="Version" .options="${this._package?.releases}" .value="${this._selectedVersion}" labelProperty="version" @change="${this._handleChangeVersion}"></dropdown-menu>
                    </div>
                </div>
                ${this._renderPackageView()}
            ` : html`
                <h2>Not found!</h2>
                <h3>The package you are looking for may not exist!</h3>
            `}
            </div>
		`;
    }

    private _renderPackageView() {
        return html`
            <div id="package_view">
                <div class="centered">
                    <md-view mdData="${this._readme}"></md-view>
                    <div class="details">
                        <div>
                            <label>Install</label>
                            <h5>
                                <a href="#" @click="${this._copyInstall}"> &gt; gottabe i ${this.groupName}/${this.packageName} <img src="${copyIcon}" class="copyIt"/></a>
                            </h5>
                        </div>
                        <div class="row">
                            <div class="col-2">
                                <label>Owner</label>
                                <h6>
                                    <a href="/owner/${this._package?.group?.owner?.nickname}">${this._package?.group?.owner?.nickname}</a>
                                </h6>
                            </div>
                            <div class="col-2-end">
                                <label>License</label>
                                <h6>
                                    ${this._build?.license}
                                </h6>
                            </div>
                        </div>
                        <div>
                            <label>Release Date</label>
                            <h6>
                                ${formatDate(this._selectedVersion?.releaseDate)}
                            </h6>
                        </div>
                        <div>
                            <label>Stars</label>
                            <h6>
                                ${this._getStars()}
                            </h6>
                        </div>
                        <div>
                            <label>Repository</label>
                            <h6 class="repository">
                                <a href="${this._selectedVersion?.sourceUrl}" target="_blank">${this._selectedVersion?.sourceUrl}</a>
                            </h6>
                        </div>
                        <div>
                            <label>Homepage</label>
                            <h6 class="documentation">
                                <a href="${this._selectedVersion?.documentationUrl}" target="_blank">${this._selectedVersion?.documentationUrl}</a>
                            </h6>
                        </div>
                        <div>
                            <label>Issues</label>
                            <h6 class="issues">
                                <a href="${this._selectedVersion?.issuesUrl}" target="_blank">${this._selectedVersion?.issuesUrl}</a>
                            </h6>
                        </div>
                        <div>
                            ${ (this._build?.dependencies?.length || 0) > 0 ? html`<label>Dependencies</label>
                            <ul>
                                ${this._build?.dependencies?.map(dep => html`<li><a href="/packages/${_removeVersion(dep)}">${dep}</a></li>`)}
                            </ul>` : '' }
                        </div>
                    </div>
                </div>
            </div> `;
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
            this._package = await packageService.packageInfo(this.groupName, this.packageName, undefined, 'ALL_RELEASES');
            this._selectedVersion = this.latestVersion;
            if (this._selectedVersion && this._selectedVersion.version) {
                try {
                    let readmeData = await packageService.getPackageFile(this.groupName, this.packageName, this._selectedVersion.version, 'README.md');
                    this._readme = await readmeData.text();
                    let buildData = await packageService.getPackageFile(this.groupName, this.packageName, this._selectedVersion.version, 'build.json');
                    this._build = JSON.parse(await buildData.text());
                    this._stars = await reviewsService.getStars(this.groupName, this.packageName, this._selectedVersion.version);
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

    private _handleChangeVersion(e: CustomEvent) {
        this._selectedVersion = e.detail;
        this.performUpdate();
    }

    private _getStars() {
        let st = Math.round((this._stars?.stars || 0) * 10) / 10;
        let sw = Math.round(st * 90 / 5);
        return html`
            <ul class="stars">
                <li class="star" title="${st} stars"><span style="width: ${sw}px"> </span></li>
                <li><a href="/reviews/${this.groupName}/${this.packageName}/${this._selectedVersion?.version}">${this._stars?.totalReviews || 0} reviews</a></li>
                <li><a href="/reviews/${this.groupName}/${this.packageName}/${this._selectedVersion?.version}?vulnerabilities=true">${this._stars?.totalVulnerabilities || 0} vulnerabilities</a></li>
            </ul>
        `;
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'package-page': PackagePage;
    }
}
