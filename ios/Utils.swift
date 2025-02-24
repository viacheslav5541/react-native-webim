// Utils.swift

import Foundation
import WebimClientLibrary

class Utils : NSObject  {
    
    func sendEvent(name: String, body: Any?) {
        EventEmitter.sharedInstance.dispatch(name: name, body: body)
    }
    
    static func messageToJson(message: Message) -> [String:Any]{

        var map = [String:Any]()
        
        let id : String = message.getID();
        let time : String = message.getTime().datatypeValue;
        let type : String = String(describing: message.getType());
        let text : String = message.getText();
        let name : String = message.getSenderName();
        let status : String = String(describing: message.getSendStatus());
        let avatar : String = message.getSenderAvatarFullURL()?.absoluteString ?? "";
        let read : Bool = message.isReadByOperator();
        let canEdit : Bool = message.canBeEdited();
        let keyboard : [[KeyboardButton]]? = message.getKeyboard()?.getButtons();
        var buttons = [String]();

        if keyboard != nil {
            for key in keyboard! {
                for button in key {
                    let buttonText = button.getText();
                    buttons.append(buttonText)
                }
            }
        } else {
            print ("KEYBOARD BUTTON NIL")
        }
        
        let url : String = message.getData()?.getAttachment()?.getFileInfo().getURL()?.absoluteString ?? "";
        let filename : String = message.getData()?.getAttachment()?.getFileInfo().getFileName() ?? "";
        let contentType : String = message.getData()?.getAttachment()?.getFileInfo().getContentType() ?? "";

        map.updateValue(id, forKey: "id");
        map.updateValue(time, forKey: "time");
        map.updateValue(type, forKey: "type");
        map.updateValue(text, forKey: "text");
        map.updateValue(name, forKey: "name");
        map.updateValue(status, forKey: "status");
        map.updateValue(avatar, forKey: "avatar");
        map.updateValue(read, forKey: "read");
        map.updateValue(canEdit, forKey: "canEdit");
        map.updateValue(buttons, forKey: "buttons");
        map.updateValue(url, forKey: "url");
        map.updateValue(filename, forKey: "filename");
        map.updateValue(contentType, forKey: "contentType");

        return map;
    }
    
    static func messagesToJson(messages: [Message]) -> [String:Any]{

        var map = [String:Any]()
        var jsonArray = [[String:Any]]();
        
        for message in messages {
            jsonArray.append(messageToJson(message: message));
        }
        
        map.updateValue(jsonArray, forKey: "messages")
        
        return map;
    }
}
