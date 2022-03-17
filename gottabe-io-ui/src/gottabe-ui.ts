import { LitElement, html, customElement, property, css } from 'lit-element';
import { router, navigator } from 'lit-element-router';
import PubSub from 'pubsub-js';
import http from './http-service';
import './login-form';
import './register';
import './activate';
import './home-not-logged';
import './home-logged';
import './profile-page';
import './tokens-page';
import {Md5} from 'ts-md5/dist/md5';
import { Match, UserData, MenuItem } from './types';

const TOPIC_NAME = 'gottabe.credentials';

const DF_OPTIONS: any = {
    year: 'numeric', month: 'numeric', day: 'numeric',
	hour: 'numeric', minute: 'numeric', second: 'numeric'
};

const dateFormat = new Intl.DateTimeFormat('default', DF_OPTIONS);

const USER_MENU : MenuItem[] = [
	{ label: 'Profile',       url: '/profile',  icon: ''},
	{ label: 'Packages',      url: '/packages', icon: ''},
	{ label: 'Manage Tokens', url: '/tokens',   icon: ''},
	{ label: 'Logout',        url: '/logout',   icon: ''}
];

@customElement("gottabe-app")
@router
@navigator
class GottabeApp extends LitElement {

	static get routes() {
		return [{
			name: 'home',
			pattern: ''
		}, {
			name: 'login',
			pattern: 'login'
		}, {
			name: 'register',
			pattern: 'register'
		}, {
			name: 'profile',
			pattern: 'profile'
		}, {
			name: 'activate',
			pattern: 'activate/:activationCode'
		}, {
			name: 'recover',
			pattern: 'recover/:recoveryCode'
		}, {
			name: 'search',
			pattern: 'search'
		}, {
			name: 'tokens',
			pattern: 'tokens'
		},  {
			name: 'packages',
			pattern: 'packages/:groupName'
		}, {
			name: 'package',
			pattern: 'package/:groupName/:packageName'
		}, {
			name: 'not-found',
			pattern: '*'
		}];
	}

	static get styles() {
		return css`
			:host {
				display: block;
				flex-direction: column;
				align-items: center;
				width: 100%;
			}

			.App {
				background-color: #282c34;
				width: 100%;
			}
			  
			.App-logo {
				height: 20vmin;
				pointer-events: none;
			}
			  
			.App-header {
				text-align: center;
				min-height: 20vh;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				font-size: calc(10px + 2vmin);
				color: white;
			}

			nav {
				display: flex;
			}

			nav * {
				margin: 10px;
			}

			nav div {
				padding-top: 20px;
				padding-bottom: 20px;
				font-size: 0.6em;
				color: white;
			}

			nav div a {
				text-decoration: none;
				color: white;
			}

			.search-input {
				border: #708090;
				border-radius: 0.5em 0px 0px 0.5em;
				padding: 1em;
				margin-right: 0px;
			}

			.search-button {
				border: #708090;
				background-color: #708090;
				color: white;
				border-radius: 0px 0.5em 0.5em 0px;
				padding: 1em;
				margin-left: -10px;
			}

			.App-link {
				color: #61dafb;
			}

			.right-panel {
				position:absolute;
				color: #aaa;
			}

			div.user-bar {
				padding: 0px;
			}

			div.user-bar * {
				padding: 0px;
				margin: 0px;
			}

			div.user-bar button {
				border: 1px solid #555;
				background: #444;
				margin: 10px;
				border-radius: 6px;
				padding-left: 10px;
			}

			div.user-bar button.showing {
				background: #555;
			}

			div.user-bar button img {
				float: left;
				width: 3em;
			}

			div.user-bar button div {
				display: block;
				float: right;
				padding: 15px;
			}
			
			.menu {
				position: absolute;
				display: none;
				font-size: 1.5em;
				border: 1px solid #ccc;
				background: #555;
				color: #ccc;
				text-align: left;
				border-radius: 6px;
				padding: 8px;
			}

			.menu div {
				position: relative;
			}

			.menu hr {
				border: none;
				border-top: 1px solid #777;
			}

			.menu div.menuitem {
				padding: 8px;
				cursor: pointer;
			}

			.menu div.menuitem:hover {
				background: #777;
			}

			.menu div.username {
				padding: 8px;
				font-weight: bold;
			}

			.showMenu {
				display: block;
			}
		`;
	}

	@property({
		type: Object,
		attribute: false
	})
	userData: UserData | undefined;

	@property({
		type: Object,
		attribute: false
	})
	match: Match | undefined;

	@property({ type: String })
	route: string;
	@property({ type: Object })
	params: any;
	@property({ type: Object })
	query: any;

	constructor() {
		super();
		this.route = '';
		const assignUser = ((user: any) => {
			this.userData = user;
		}).bind(this);
		PubSub.subscribe(TOPIC_NAME, ((_msg: any, value: any) => {
			console.log(_msg,value);
			if (!value.authToken) {
				this.userData = undefined;
			} else {
				http.defaultHttpClient.get('/api/user/current', {})
					.then(resp => resp.json().then(assignUser));
				// http.get('/api/hiscores/user/10', {}).then(resp => resp.json().then(((v:any) => this.lastGames = v).bind(this)));
			}
		}).bind(this));
		// http.get('/api/hiscores/10', {}).then(resp => resp.json().then(((v:any) => this.hiScores = v).bind(this)));
	}

	router(route: string, params: any, query: any, data: any) {
		this.route = route;
		this.params = params;
		this.query = query;
		console.log(route, params, query, data);
	}

	render() {
		return html`
			<div class="App">
				<header class="App-header">
					<nav>
						<h2>
							gottabe.io
						</h2>
						<form>
							<input type="text" placeholder="Search" class="search-input"/>
							<input type="submit" value="Search" class="search-button"/>
						</form>
						${this.getUserBar()} 
					</nav>
				</header>
				
				${this.mapRoutes({
					'home' : (!this.userData ? html`<home-not-logged></home-not-logged>` : html`<home-logged user="${this.userData}"></home-logged>`),
					'login' : html`<login-form></login-form>`,
					'register' : html`<register-form></register-form>`,
					'activate' : html`<activate-form code="${this.params.activationCode}"></activate-form>`,
					'profile' : html`<profile-page></profile-page>`,
					'packages' : html`<packages code="${this.userData}"></packages>`,
					'tokens' : html`<tokens-page code="${this.userData}"></tokens-page>`,
					'logout' : (() => this._logout()).bind(this)
					}, html`<h1>Not found</h1>`)
                }
			</div>
		`;
	}

	mapRoutes(map: any, def: any) {
		let ret = map[this.route];
		if (ret) {
			if (typeof ret === 'function')
				return ret();
			else
				return ret;
		} else
			return def;
	}

	formatDate(dt:string) {
		let d = new Date(dt);
		return dateFormat.format(d);
	}

	getUserBar() {
		//this.userData = <any>{};
		return !this.userData
			? html`<div><a href="/login">Login</a> | <a href="/register">Signup</a></div>`
			: html`<div class="user-bar">
						<button @click="${this._showMenu}">
							<img alt="Avatar" src="${this.userData.image || 'https://www.gravatar.com/avatar/' + Md5.hashStr('avatar-email:' + this.userData.email) + '?s=40&d=identicon&r=PG'}" />
							<div>
								<svg viewBox="0 0 11.64 5.82" height="6px" fill="#ccc"><g id="57ddc589-5982-4ec7-bbcc-279bb4699589"><polygon points="10 0 5 5 0 0 10 0"></polygon></g></svg>
							</div>
						</button>
						<div class="menu">
							<div class="username">${this.userData.name || 'No-user'}</div>
							<hr/>
							${USER_MENU.map(item => html`<div class="menuitem" href="${item.url}" @click="${this.handleMenuItem}">${item.label}</div>`)}
						</div>
					</div>`;
	}

	connectedCallback() {
		super.connectedCallback();
	}

	handleMenuItem(e: MouseEvent) {
		let href = (<any>e.target || <any>e.currentTarget || {}).attributes['href'].value;
		this._hideMenu();
		e.preventDefault();
		if (href == '/logout')
			this._logout();
		else
			(<any>this).navigate(href);
	}

	_showMenu() {
		if (this.shadowRoot) {
			let target = this.shadowRoot.querySelector('.menu');
			if (target) target.classList.toggle('showMenu');
			let btn = this.shadowRoot.querySelector('.user-bar button');
			if (btn) btn.classList.toggle('showing');
		}
	}

	_hideMenu() {
		if (this.shadowRoot) {
			let target = this.shadowRoot.querySelector('.menu');
			if (target) target.classList.remove('showMenu');
			let btn = this.shadowRoot.querySelector('.user-bar button');
			if (btn) btn.classList.remove('showing');
		}
	}

	_logout() {
		http.revokeToken()
			 .catch(((e:any) => {
			 	e.response.json().then(((v:any) => console.error(v)).bind(this));
			 }).bind(this));
		// this.userData = undefined;
	}

}

declare global {
	interface HTMLElementTagNameMap {
		'gottabe-app': GottabeApp;
	}
}
