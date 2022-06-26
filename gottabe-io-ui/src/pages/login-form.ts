import { LitElement, html, customElement, property } from 'lit-element';
import { navigator } from 'lit-element-router';
import {login} from '../services/http-services';
import {style} from '../styles';

@customElement("login-form")
@navigator
class LoginForm extends LitElement {

	static get styles() {
		return style;
	}

	@property()
	error ?: string;

	@property()
	newUser : boolean;

	render() {
		return html`
			<form id="loginForm">
				<input type="hidden" name="grant_type" value="password" />
				<div class="container">
					<div class="error">
						<h3>${this.error}</h3>
					</div>
					<div>
						<label>Username:</label>
						<input type="text" name="username"/>
					</div>
					<div>
						<label>Password:</label>
						<input type="password" name="password"/>
					</div>
					<div>
						<button id="loginbutton" class="primary" @click="${this.handleLogin}">Login</button>
					</div>
					<div>
						<button id="registerbutton" class="secondary" @click="${this.handleNewUser}">New user</button>
					</div>
				</div>
			</form>
  		`;
	}

	constructor() {
		super();
		this.newUser = false;
	}

	async handleLogin(e: any) {
		e.preventDefault();
		let formEl = this.shadowRoot?this.shadowRoot.getElementById('loginForm'):null;
		if (!formEl) return;
		let formData = new FormData(<HTMLFormElement>formEl);
		try {
			await login(formData);
			(<any>this).navigate("/");
		} catch(e:any) {
			if (e.response) {
				let v : any = await e.response.json();
				this.error = v.message;
				console.log(v);
				this.performUpdate();
			} else
				console.error(e);
		}
	}

	handleNewUser(e: any) {
		this.error = undefined;
		e.preventDefault();
		(<any>this).navigate("/register");
	}

}

declare global {
	interface HTMLElementTagNameMap {
		'login-form': LoginForm;
	}
}
