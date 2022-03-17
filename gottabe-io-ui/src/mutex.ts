/*
import {Mutex} from 'async-mutex';

export class MMutex {
    _mutex: Mutex;
    _release:any;
    constructor() {
        this._mutex = new Mutex();
    }
    wait(_millis:number): Promise<any> {
        if (this._release)
            return this._mutex.waitForUnlock();
        else
            return Promise.resolve();
    }
    lock() {
        if (!this._release)
            this._release = this._mutex.acquire();
    }
    unlock() {
        if (this._release) this._release();
        this._release = null;
    }
}
*/

export class Mutex {
    private timeoutIds: Array<any>;
    private resolvers: Array<any>;
    private locked: boolean;
    constructor() {
        this.timeoutIds = [];
        this.resolvers = [];
        this.locked = false;
    }
    wait(milliseconds: number): Promise<any> {
        console.log('Waiting mutex');
        let that = this;
        return new Promise((resolve, reject) => {
            if (!that.locked) resolve(that);
            else {
                that.resolvers.push(resolve);
                that.timeoutIds.push(setTimeout(() => {
                    console.log('Mutex timeout');
                    reject(new Error("Wait timeout."));
                }, milliseconds));
            }
        });
    }
    lock() {
        if (this.locked) throw new Error("Mutex is already locked.");
        console.log('Mutex locked');
        this.locked = true;
    }
    unlock() {
        let that = this;
        if (this.locked) {
            console.log('Mutex unlocked');
            this.locked = false;
            this.timeoutIds.forEach(clearTimeout);
            this.resolvers.forEach(resolve => resolve(that));
            this.timeoutIds = [];
            this.resolvers = [];
        }
    }
}

export class Timer {
    private _id: any;
    private readonly _repeatable: boolean;

    constructor(repeatable?:boolean) {
        this._repeatable = !!repeatable;
    }

    start(milliseconds: number, ...args:any[]) {
        let that = this;
        let _timeFunc = this._repeatable ? setInterval : setTimeout;
        return new Promise((resolve, _reject) => {
            that._id = _timeFunc(() => {
                that._id = undefined;
                return resolve(that);
            }, milliseconds, ...args);
        });
    };

    cancel() {
        if (this._id) {
            let _clearTimeFunc = this._repeatable ? clearInterval : clearTimeout;
            _clearTimeFunc(this._id);
            this._id = undefined;
        }
    }
}
