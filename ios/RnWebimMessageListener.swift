// Utils.swift

import Foundation
import WebimMobileSDK

class RnWebimMessageListener : MessageListener  {
    
    private var utils: Utils? = nil;
    
    init(utils: Utils){
        self.utils = utils;
    }
    
    func added(message newMessage: Message, after previousMessage: Message?) {
        var msg = [String:Any]()
        msg.updateValue(Utils.messageToJson(message: newMessage), forKey: "msg");
        self.utils?.sendEvent(name: "messageAdded", body: msg)
    }
    
    func removed(message: Message) {
        var msg = [String:Any]()
        msg.updateValue(Utils.messageToJson(message: message), forKey: "msg");
        self.utils?.sendEvent(name: "messageRemoved", body: msg)
    }
    
    func removedAllMessages() {
        var msg = [String:Any]()
        self.utils?.sendEvent(name: "allMessagesRemoved", body: msg)
    }
    
    func changed(message oldVersion: Message, to newVersion: Message) {
        var map = [String:Any]()
        map.updateValue(Utils.messageToJson(message: oldVersion), forKey: "from");
        map.updateValue(Utils.messageToJson(message: newVersion), forKey: "to");
        self.utils?.sendEvent(name: "messageChanged", body: map)

    }
    
}

