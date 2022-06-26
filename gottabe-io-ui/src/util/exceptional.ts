import {LitElement} from "lit-element";
import {ErrorFieldVO, ErrorVO} from "../types";

type Constructor<T = LitElement> = new (...args: any[]) => T;

declare class Component {
    error?:string;
    errorFields?:any;
}

type CallingFunction<T> = () => T;
type CallingFunctionAsync<T> = () => Promise<T>;

export function exceptional<TBase extends Constructor<LitElement>>(base: TBase): Constructor<Component> & TBase {
    // @ts-ignore
    return class extends base {

        async processError(e: any) {
            console.log(e);
            let error: ErrorVO = await e.response.json();
            if (error.message)
                (<any>this).error = error.message;
            if (error.fields && error.fields.length)
                error.fields.forEach(((field: ErrorFieldVO) => {
                    if (field.id)
                        (<any>this).errorFields[field.id] = field.message;
                    else
                        (<any>this).error += "\n" + field.message;
                    }).bind(this));
            await this.performUpdate();
        }

        clearErrors() {
            (<any>this).error = '';
            (<any>this).errorFields = {};
        }

        tryCatch<T>(callingFunction: CallingFunction<T>): T | void {
            try {
                return callingFunction();
            } catch(e:any) {
                this.processError(e).then();
            }
        }

        async tryCatchAsync<T>(callingFunction: CallingFunctionAsync<T>): Promise<T | void> {
            try {
                return await callingFunction();
            } catch(e:any) {
                await this.processError(e);
            }
        }

    };
};
