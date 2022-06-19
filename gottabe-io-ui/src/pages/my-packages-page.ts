import {LitElement, html, customElement, css, property} from 'lit-element';
import {PackageData, PackageGroup, UserData} from "../types";
import {style} from '../styles';
import packageService from "../services/package-services";
import '../components/my-dialog';
import {MyDialogElement} from "../components/my-dialog";
import {pubSubService, TopicCallbackFunction} from '../util/pupsub-services';
import * as userServices from "../services/user-services";

function setValue(base:any, path:string, value:any) {
    let ids = path.split(".");
    let last = '';
    ids.forEach((id:string,i:number) => {
        if (i < ids.length - 1)
            base = base[id];
        else
            last = id;
    });
    base[last] = value;
}

@customElement("my-packages-page")
class MyPackagesPage extends LitElement {

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
            .buttons {
                display: flex;
            }
            .form-field + .form-field {
              margin-top: 8px;
            }
		`];
    }

    @property()
    packages: Array<PackageData>;

    private error?: string;

    private group: PackageGroup;

    private errorFields: any;

    private __package: PackageData;

    private userData?: UserData;

    private userCallback: TopicCallbackFunction;

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
                ${this.userData ? html`
                <div class="buttons">
                    <button @click="${this._createGroup}">Create Group</button>
                    <button @click="${this._createPackage}">Create Package</button>
                </div>
                <table>
                    <tr><th>Package</th><th>Versions</th><th>Date</th><th>Actions</th></tr>
                    ${this.packages.map((this._package).bind(this))}
                </table>
                ` : html`
                    
                `}
            </div>
            <my-dialog id="groupDlg" showCancel="true">
                <div slot="caption">Dialog</div>
                <div>
                    <div class="form-field">
                        <label>Name:</label>
                        <span class="error">
                            <h3>${this.errorFields.name}</h3>
                        </span>
                        <input type="text" name="group.name" .value="${this.group.name}" @change="${this._handleChange}"/>
                    </div>
                    <div class="form-field">
                        <label>Description:</label>
                        <span class="error">
                            <h3>${this.errorFields.description}</h3>
                        </span>
                        <input type="text" name="group.description" .value="${this.group.description}" @change="${this._handleChange}"/>
                    </div>
                </div>
            </my-dialog>
            <my-dialog id="packageDlg" showCancel="true">
                <div slot="caption">Dialog</div>
                <div>
                    <div class="form-field">
                        <label>Group:</label>
                        <span class="error">
                            <h3>${this.errorFields['group.name']}</h3>
                        </span>
                        <input type="text" name="__package.group.name" .value="${this.__package.group.name}" @change="${this._handleChange}"/>
                    </div>
                    <div class="form-field">
                        <label>Name:</label>
                        <span class="error">
                            <h3>${this.errorFields.name}</h3>
                        </span>
                        <input type="text" name="__package.name" .value="${this.__package.name}" @change="${this._handleChange}"/>
                    </div>
                </div>
            </my-dialog>
		`;
    }

    constructor() {
        super();
        this.packages = [];
        this.errorFields = {};
        this.group = {name:'', description:''};
        this.__package = {name:'', group:{name:''}};
        this.userCallback = ((_topic:string, value:any) => {
            this.userData = value;
            this.performUpdate();
        }).bind(this);
    }

    connectedCallback() {
        super.connectedCallback();
        pubSubService.subscribe(userServices.TOPIC_NAME, this.userCallback);
        this._updateData().catch(((e:any) => this.error = e.message).bind(this));
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        pubSubService.unsubscribe(this.userCallback);
    }

    private _package(pack: PackageData, i: number) {
        if (!pack) return html``;
        const clickOpen = (_event: MouseEvent) => {
            console.log(this.packages[i]);
        };
        return html`
            <tr>
                <td rowspan="${pack.releases?.length}">${pack.group?.name}/${pack.name}</td>
                ${pack.releases?.map((release,j) => html`
                    <td>${release.version}</td>
                    <td>${new Date(Date.parse(release.releaseDate)).toLocaleString()}</td>
                    <td class="actions"><a href="#open" @click="${clickOpen.bind(this)}">Open</a></td>
                    ${j < (pack.releases?.length||0) - 1 ? html`</tr><tr>`: ''}`
                )}
                ${!(pack.releases?.length) ? html`<td colspan="3" style="text-align: center">No release done yet!</td>` : '' }
            </tr>
        `;
    }

    private async _updateData() {
        this.packages = await packageService.mine();
        await this.performUpdate();
    }

    private async _createPackage() {
        let packageDlg = <MyDialogElement>this.shadowRoot?.querySelector('my-dialog#packageDlg')
        this.__package = {name:'', group:{name:''}};
        let result = await packageDlg?.show(true);
        if (result == 'ok') {
            console.log(this.__package);
            await packageService.createPackage(this.__package);
            await this._updateData();
        }
    }

    private async _createGroup() {
        let groupDlg = <MyDialogElement>this.shadowRoot?.querySelector('my-dialog#groupDlg')
        this.group = {name:'', description:''};
        let result = await groupDlg?.show(true);
        if (result == 'ok') {
            await packageService.createGroup(this.group);
            await this._updateData();
        }
    }

    private _handleChange(e:any) {
        let target = e.target;
        if (target) {
            setValue(this,target.name,target.value);
            this.errorFields = {};
        }
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'my-packages-page': MyPackagesPage;
    }
}
