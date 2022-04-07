import {LitElement} from 'lit-element';

function parseQuery(queryStr: string) {
    const urlSearchParams = new URLSearchParams(queryStr);
    return Object.fromEntries(urlSearchParams.entries());
}

function parseParams(pattern: string, uri: string) {
    let params: any = {};

    const patternArray = pattern.split('/').filter((path:string) => { return path != '' });
    const uriArray = uri.split('/').filter((path) => { return path != '' });

    patternArray.map((pattern, i) => {
        if (/^:/.test(pattern)) {
            params[pattern.substring(1)] = uriArray[i];
        }
    })
    return params;
}

function patternToRegExp(pattern: string) {
    if (pattern) {
        return new RegExp('^(|/)' + pattern.replace(/:[^\s/]+/g, '([^/]+)') + '(|/)$');
    } else {
        return new RegExp('(^$|^/$)');
    }
}

function testRoute(uri: string, pattern: string) {
    if (patternToRegExp(pattern).test(uri)) {
        return true;
    }
    return false;
}

interface Route {
    pattern: string;
    name: string;
}

type Routes = Array<Route>;

declare class Router {
    static routes: Routes;
    protected router(route?: string, params?: object, query?: object): void;
}

type Constructor<T = LitElement> = new (...args: any[]) => T;

export function router<TBase extends Constructor<LitElement>>(base: TBase): Constructor<Router> & TBase {
    // @ts-ignore
    return class extends base {
        static get properties() {
            return {
                route: {type: String, reflect: true, attribute: 'route'}
            };
        }

        route: string;

        constructor(...args: any[]) {
            super(...args);
            this.route = '';
        }

        connectedCallback() {
            super.connectedCallback();
            const routed = (name: string, params: any, query: any, callback:Function) => {
                callback(name,params,query);
            };
            const doRouting = (routes: Route[], callback: Function) => {
                const uri = decodeURI(window.location.pathname);
                const queryStr = decodeURI(window.location.search);
                let notFoundRoute = routes.filter(route => route.pattern === '*')[0];
                let activeRoute = routes.filter(route => route.pattern !== '*' && testRoute(uri, route.pattern))[0];
                let query = parseQuery(queryStr);
                if (activeRoute) {
                    let params = parseParams(activeRoute.pattern, uri);
                    routed(activeRoute.name, params, query, callback);
                } else if (notFoundRoute) {
                    routed(notFoundRoute.name, {}, query, callback);
                }
            };
            //@ts-ignore
            doRouting(this.constructor.routes, (...args) => this.router(...args));
            window.addEventListener('route', () => {
                //@ts-ignore
                doRouting(this.constructor.routes, (...args) => this.router(...args));
            })

            window.onpopstate = () => {
                window.dispatchEvent(new CustomEvent('route'));
            }
        }

    };
};

export function navigator<TBase extends Constructor<LitElement>>(base: TBase): Constructor<Router> & TBase {
    // @ts-ignore
    return class extends base {
        navigate(href: string) {
            window.history.pushState({}, <any>null, href);
            window.dispatchEvent(new CustomEvent('route'));
        }
    };
}
