import {LitElement, html, customElement, css, property} from 'lit-element';
import {UserPrivacyVO, UserVO} from "../types";
import {userService} from '../services/user-services';
import {style} from '../styles';
import {Timer} from '../util/mutex';

@customElement("profile-page")
class ProfilePage extends LitElement {

	static get styles() {
		return [style,css`
			:host {
				display: block;
				align-items: center;
				width: 100%;
			}
			#menu {
				float: left;
				text-align: right;
			}
			#menu button {
				background: transparent;
				color: aliceblue;
				border: none;
				width: 200px;
				text-align: left;
			}
			section {
				display: block;
				padding:4em;
				text-align: center;
			}
			section h1 {
				font-size: 2em;
			}
			section.s1 {
				color: white;
				background-color: #ee3344;
			}
			section.s2 {
				color: black;
				background-color: white;
			}
			section.s3 {
				color: white;
				background-color: #ee3344;
			}
		`];
	}

	@property({ type: Object })
	private profile: UserVO;
	@property({ type: Object })
	private privacy: UserPrivacyVO;
	@property()
	error ?: string;
	@property()
	mode ?: string;
	private timer: Timer;

	render() {
		return html`
			<div>
				<!--
				<div id="menu">
					<div>
						<button>Personal data</button>
					</div>
					<div>
						<button>Privacy</button>
					</div>
				</div>
				-->
				<div>
					<form id="loginForm">
						<input type="hidden" name="grant_type" value="password" />
						<div class="container">
							<div class="error">
								<h3>${this.error}</h3>
							</div>
							<div>
								<label>Name:</label>
								<input type="text" id="name" name="name" .value="${this.profile.name}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<label>Last name:</label>
								<input type="text" id="lastName" name="lastName" .value="${this.profile.lastName}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<label>E-Mail:</label>
								<input type="text" id="email" name="email" .value="${this.profile.email}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<label>Nickname:</label>
								<input type="text" id="nickname" name="nickname" .value="${this.profile.nickname}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<label>Description:</label>
								<input type="text" id="description" name="description" .value="${this.profile.description}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<label>Github Account:</label>
								<input type="text" id="githubAccount" name="githubAccount" .value="${this.profile.githubAccount}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<label>Twitter Account:</label>
								<input type="text" id="twitterAccount" name="twitterAccount" .value="${this.profile.twitterAccount}" @change="${this.handleChangeData}"/>
							</div>
							<div>
								<input style="float:left" type="checkbox" id="showEmail" name="showEmail" .checked="${this.privacy.showEmail}" @change="${this.handleChangePrivacy}"/>
								<label>Show E-Mail</label>
							</div>
							<div>
								<input style="float:left" type="checkbox" id="showTwitter" name="showTwitter" .checked="${this.privacy.showTwitter}" @change="${this.handleChangePrivacy}"/>
								<label>Show Twitter Account</label>
							</div>
							<div>
								<input style="float:left" type="checkbox" id="showGithub" name="showGithub" .checked="${this.privacy.showGithub}" @change="${this.handleChangePrivacy}"/>
								<label>Show Github Account</label>
							</div>
							<div>
								<input style="float:left" type="checkbox" id="showName" name="showName" .checked="${this.privacy.showName}" @change="${this.handleChangePrivacy}"/>
								<label>Show Name</label>
							</div>
						</div>
					</form>
				</div>
			</div>
		`;
	}

	constructor() {
		super();
		this.profile = <any>{};
		this.privacy = <any>{};
		this.timer = new Timer();
	}

	connectedCallback() {
		super.connectedCallback();
		this._updateData().catch(((e:any) => {
			this.error = e.message;
		}).bind(this));
		this.mode = 'data';
	}

	private async _updateData() {
		this.error = '';
		this.profile = await userService.currentUserProfile();
		this.privacy = await userService.currentUserPrivacy();
	}

	handleChangeData(event: any) {
		let profile : any = this.profile;
		profile[event.target.id] = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
		this.profile = profile;
		this.triggerUpdateData();
	}

	handleChangePrivacy(event: any) {
		let privacy : any = this.privacy;
		privacy[event.target.id] = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
		this.privacy = privacy;
		this.triggerUpdatePrivacy();
	}

	triggerUpdateData() {
		this.timer.cancel();
		this.timer.start(3000)
			.then((() => userService.updateCurrentUserProfile(this.profile)).bind(this));
	}

	triggerUpdatePrivacy() {
		this.timer.cancel();
		this.timer.start(3000)
			.then((() => userService.updateCurrentUserPrivacy(this.privacy)).bind(this));
	}

}

declare global {
	interface HTMLElementTagNameMap {
		'profile-page': ProfilePage;
	}
}
