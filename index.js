import { NativeModules, NativeEventEmitter } from 'react-native';

const { RnWebim } = NativeModules;

const eventEmitter = new NativeEventEmitter(RnWebim);

const EVENTS = {
    MESSAGE_ADDED: 'messageAdded',
    MESSAGE_REMOVED: 'messageRemoved',
    MESSAGE_CHANGED: 'messageChanged',
    ALL_MESSAGES_REMOVED: 'allMessagesRemoved',
}

const onEvent = (event, callback) => {
    eventEmitter.addListener(event, callback);
    return () => eventEmitter.removeListener(event, callback)
}

const resume = (params = {}, appVersion) => {
    return RnWebim.resume({
        ...params,
        userFields: params.userFields ? JSON.stringify(params.userFields) : null,
    }, appVersion)
}

const pause = () => {
    return RnWebim.pause()
}

const destroy = () => {
    return RnWebim.destroy()
}

const sendMessage = (text = '') => {
    return RnWebim.sendMessage(text)
}

const getLastMessages = (maxCount = 10) => {
    return RnWebim.getLastMessages(maxCount)
}

const getNextMessages = (maxCount = 10) => {
    return RnWebim.getLastMessages(maxCount)
}

const sendFile = (uri = '', name = '', mime = '', extension = '') => {
    return RnWebim.sendFile(uri, name, mime, extension)
}

const getUnreadByVisitorMessageCount = () => {
    return RnWebim.getUnreadByVisitorMessageCount()
}

const setChatRead = () => {
    return RnWebim.setChatRead()
}

export default {
    resume,
    pause,
    destroy,
    sendMessage,
    getLastMessages,
    getNextMessages,
    sendFile,
    getUnreadByVisitorMessageCount,
    setChatRead,
    EVENTS,
    onEvent,
};


