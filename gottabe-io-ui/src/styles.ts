import { css } from 'lit-element'

export const style = css`
	:host {
		display: block;
	}
	div.container {
		width: 250px;
		border: 1px solid #777;
		margin-left: auto;
		margin-right: auto;
		padding: 8px;
	}
	div.container div {
		margin-top: 8px;
	}
	label, input:not([type='checkbox']):not([type='radio']), button {
		display: block;
	}
	label {
		width: 100%;
		color: white;
	} 
    textarea:focus, input:focus{
        outline: none;
    }
    input:not([type='checkbox']):not([type='radio']), button {
		padding: 4px;
		width: calc(100% - 8px);
		border: 1px solid #777;
		border-radius: 4px;
	}
	button {
		width: 100%;
		font-weight: bold;
	}
	button.primary {
		color: #eeefef;
		border: 1px solid #556677;
		background-color: #3588bb;
	}
	button.secondary {
		color: #eeefef;
		border: 1px solid #557766;
		background-color: #35bb88;
	}
	button.warning {
		color: #efefed;
		border: 1px solid #776655;
		background-color: #bb8835;
	}
	button.error {
		color: #efefed;
		border: 1px solid #775545;
		background-color: #bb4535;
	}
	.error {
		color: red;
	}
	.error h3 {
		margin: 0px;
	}
`;
