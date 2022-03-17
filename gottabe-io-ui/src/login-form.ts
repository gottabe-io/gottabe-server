import { LitElement, html, customElement, property } from 'lit-element';
import { navigator } from 'lit-element-router';
import http from './http-service';
import {style} from './styles';

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

	@property()
	user ?: any;

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

	handleLogin(e: any) {
		e.preventDefault();
		let formEl = this.shadowRoot?this.shadowRoot.getElementById('loginForm'):null;
		if (!formEl) return;
		let form = new FormData(<HTMLFormElement>formEl);
		http.login(form)
			.then((_res: any) => {
				(<any>this).navigate("/");
			})
			.catch(((e:any) => {
				e.response.json().then(((v:any) => {
					this.error = v.error_description;
					console.log(v);
				}).bind(this));
			}).bind(this));
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
