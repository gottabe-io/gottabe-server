import {LitElement, html, customElement, css, property} from 'lit-element';
import {UserVO} from "../types";

@customElement("home-logged")
class HomeLogged extends LitElement {

	static get styles() {
		return css`
			:host {
				display: block;
				align-items: center;
				width: 100%;
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
		`;
	}

	@property({
		type: Object,
		attribute: false
	})
	userData: UserVO | undefined;

	render() {
		return html`
			<section class="s1">
				<h1>Manage your C/C++ project dependencies and build great stuff!</h1>
				<p>The World Wide Fund for Nature (WWF) is an international organization working on issues regarding the conservation, research and restoration of the environment, formerly named the World Wildlife Fund. WWF was founded in 1961.</p>
			</section>
			<section class="s2">
				<h2>Another title about how this is good</h2>
				<p>The Panda has become the symbol of WWF. The well-known panda logo of WWF originated from a panda named Chi Chi that was transferred from the Beijing Zoo to the London Zoo in the same year of the establishment of WWF.</p>
			</section>
			<section class="s3">
				<h2>A third section. My creativity is over.</h2>
				<p>The Panda has become the symbol of WWF. The well-known panda logo of WWF originated from a panda named Chi Chi that was transferred from the Beijing Zoo to the London Zoo in the same year of the establishment of WWF.</p>
			</section>
		`;
	}

	constructor() {
		super();
	}

}

declare global {
	interface HTMLElementTagNameMap {
		'home-logged': HomeLogged;
	}
}
