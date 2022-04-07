import PubSub from 'pubsub-js';

export type TopicCallbackFunction = PubSubJS.SubscriptionListener<any>;

const storageKeys: any = {};

class PubSubService {
    init(topic: string, localStorageKey: string) {
        storageKeys[topic] = localStorageKey;
        const callbackCaller = (_topic: string, value: any) => {
            if (typeof value != 'undefined')
                window.localStorage.setItem(localStorageKey, JSON.stringify(value));
            else
                window.localStorage.removeItem(localStorageKey);
        };
        PubSub.subscribe(topic, callbackCaller);
    }
    subscribe(topic: string, callback: TopicCallbackFunction): string {
        let localStorageKey = storageKeys[topic];
        if (localStorageKey) {
            let valueStr = window.localStorage.getItem(localStorageKey);
            if (valueStr)
                callback(topic,  JSON.parse(valueStr))
        }
        return PubSub.subscribe(topic, callback);
    }
    publish(topic: string, data: any) {
        return PubSub.publish(topic, data);
    }
    publishSync(topic: string, data: any) {
        return PubSub.publishSync(topic, data);
    }
    unsubscribe(topicOrCallback: string | TopicCallbackFunction) {
        return PubSub.unsubscribe(topicOrCallback);
    }
}

export const pubSubService = new PubSubService;
