import {LitElement, html, customElement, css, property} from 'lit-element';
import {style} from '../styles';

const $q = function(shadowRoot:any, selector:string, consumer: Function) {
	if (shadowRoot) {
		let el = shadowRoot.querySelector(selector);
		if (el)
			consumer(el);
	}
}

@customElement("my-dialog")
export class MyDialogElement extends LitElement {

	static get styles() {
		return [style,css`
			:host {
				display: none;
				position: fixed;
			}
			.showing {
				display: block;
			}
			.overlay {
				display: none;
				position: fixed;
				margin: 0;
				left: 0px;
				top: 0px;
				width: 100%;
				height: 100%;
				background-color: #282c34;
				opacity: 0.5;
				z-index: 999;
			}
			.container-dialog {
				border-radius: 6px;
				position: fixed;
				border: 1px solid #282c34;
				background-color: #444;
				color: white;
				width: 50%;
				margin-left: 25%;
				margin-right: 25%;
				margin-top: 25%;
				z-index: 1001;
				padding: 8px;
				top: 0px;
				left: 0px;
			}
			.buttons {
				display: flex;
				margin-top: 8px;
			}
			.buttons button {
				display: block;
				width: 100%
			}
			.buttons button + button {
				margin-left: 8px;
			}
			h3 {
				margin-left: 0px;
				margin-right: 0px;
				margin-top: 8px;
				margin-bottom: 8px;
				padding: 0px;
			}
			button[hidden] {
				display: none;
			}
			hr {
				border: none;
				border-bottom: 1px solid #555;
			}
		`];
	}

	@property({attribute: true})
	showOk?: boolean = true;

	@property({attribute: true})
	showCancel?: boolean;

	@property({attribute: true})
	showYes?: boolean;

	@property({attribute: true})
	showNo?: boolean;

	render() {
		return html`
		<form id="aform">
			<div class="overlay"> </div>
			<div class="container-dialog">
				<div class="caption">
					<h3><slot name="caption"></slot></h3>
					<hr />
				</div>
				<div>
					<slot></slot>
					<hr />
				</div>
				<div class="buttons">
					<button id="okBtn" @click="${this._handleOk}" ?hidden="${!this.showOk}">Ok</button>
					<button id="yesBtn" @click="${this._handleYes}" ?hidden="${!this.showYes}">Yes</button>
					<button id="noBtn" @click="${this._handleNo}" ?hidden="${!this.showNo}">No</button>
					<button id="cancelBtn" @click="${this._handleCancel}" ?hidden="${!this.showCancel}">Cancel</button>
				</div>
			</div>
		</form>
		`;
	}

	private _resolve: any;

	async show(modal: boolean): Promise<string> {
		if (modal)
			$q(this.shadowRoot,'.overlay', (el:any) => el.style.display = 'block');
		this.style.display = 'block';
		return new Promise((resolve, _reject) => {
			this._resolve = resolve;
		});
	}

	constructor() {
		super();
	}

	private _handleClick(e:any, result:any) {
		e.preventDefault();
		$q(this.shadowRoot,'.overlay', (el:any) => el.style.display = 'none');
		this.style.display = 'none';
		if (this._resolve) this._resolve(result);
	}

	private _handleOk(e:any) {
		this._handleClick(e,'ok');
	}

	private _handleYes(e:any) {
		this._handleClick(e,'yes');
	}

	private _handleNo(e:any) {
		this._handleClick(e,'no');
	}

	private _handleCancel(e:any) {
		this._handleClick(e,'cancel');
	}

}

declare global {
	interface HTMLElementTagNameMap {
		'my-dialog': MyDialogElement;
	}
}
