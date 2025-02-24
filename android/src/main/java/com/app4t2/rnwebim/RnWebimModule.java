package com.app4t2.rnwebim;

import java.util.List;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.webim.android.sdk.Webim;
import ru.webim.android.sdk.WebimSession;
import ru.webim.android.sdk.MessageListener;
import ru.webim.android.sdk.Message;
import ru.webim.android.sdk.MessageTracker;
import ru.webim.android.sdk.WebimLog;
import com.facebook.react.bridge.WritableMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.net.Uri;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.webkit.MimeTypeMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.webim.android.sdk.WebimError;
import ru.webim.android.sdk.MessageStream;
import ru.webim.android.sdk.Message;

public class RnWebimModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private WebimSession session;
    private MessageTracker tracker;
    private Utils utils;
    private WebimMessageListener messageListener;

    public RnWebimModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.utils = new Utils(this.reactContext);
        this.messageListener = new WebimMessageListener(this.utils);
    }

    @Override
    public String getName() {
        return "RnWebim";
    }


    private void build(String accountName, String location, String userFields, String appVersion) {
        Webim.SessionBuilder builder = Webim.newSessionBuilder().setContext(this.reactContext)
                .setAccountName(accountName).setLocation(location)
                .setLogger(new WebimLog() {
                    @Override
                    public void log(String log) {
                        Log.i("WEBIM LOG", log);
                    }
                }, Webim.SessionBuilder.WebimLogVerbosityLevel.VERBOSE)
                .setAppVersion(appVersion);
                
        if (userFields != null) {
            builder.setVisitorFieldsJson(userFields);
        }
        session = builder.build();
    }

    @ReactMethod
    public void resume(ReadableMap builderData, String appVersion, Promise promise) {

        String accountName = builderData.getString("accountName");
        String location = builderData.getString("location");
        String userFields = builderData.getString("userFields");

        if (session == null) {
            build(accountName, location, userFields, appVersion);
        }

        if (session == null) {
            promise.reject("errorcode","Unable to build session");
        }
        session.resume();
        session.getStream().startChat();
        session.getStream().setChatRead();
        tracker = session.getStream().newMessageTracker(this.messageListener);
        promise.resolve("success");

    }

    @ReactMethod
    public void pause(Promise promise) {
        if (session == null) {
            promise.reject("errorcode","Unable to find session");
        }
        session.pause();
        promise.resolve("success");

    }

    @ReactMethod
    public void destroy(Promise promise) {
        if (session != null) {
            session.getStream().closeChat();
            tracker.destroy();
            session.destroy();
            session = null;
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void sendMessage(String message, Promise promise) {
        try {
           Message.Id messageId = session.getStream().sendMessage(message);
            Log.i("WEBIM LOG DEBUG", "sendMessage messageid");
            Log.i("WEBIM LOG DEBUG", messageId.toString());
            promise.resolve("success");
        } catch (Exception e) {
            Log.i("WEBIM LOG DEBUG", "Send message error");
            Log.i("WEBIM LOG DEBUG", e.toString());
            promise.reject("errorcode", "Send message error", e);
        }
    }


    @ReactMethod
    public void getLastMessages(int limit, final Promise promise) {
        try {
            tracker.getLastMessages(limit, new MessageTracker.GetMessagesCallback() {
                @Override
                public void receive(@NonNull final List< ? extends Message> received) {
                    Log.i("WEBIM LOG DEBUG", "getLastMessages");
                    Log.i("WEBIM LOG DEBUG", received.toString());
                    WritableMap response = Utils.messagesToJson(received);
                    promise.resolve(response);
                }
            });
        } catch (Exception e) {
            Log.i("WEBIM LOG DEBUG", "Error when getting last messages");
            Log.i("WEBIM LOG DEBUG", e.toString());
            promise.reject("errorcide","Error when getting last messages", e);

        }

    }
    @ReactMethod
    public void getNextMessages(int limit, final Promise promise) {
        try {
            tracker.getNextMessages(limit, new MessageTracker.GetMessagesCallback() {
                @Override
                public void receive(@NonNull final List< ? extends Message> received) {
                    Log.i("WEBIM LOG DEBUG", "getNextMessages");
                    Log.i("WEBIM LOG DEBUG", received.toString());
                    WritableMap response = Utils.messagesToJson(received);
                    promise.resolve(response);
                }
            });
        } catch (Exception e) {
            Log.i("WEBIM LOG DEBUG", "Error when getting next messages");
            Log.i("WEBIM LOG DEBUG", e.toString());
            promise.reject("errorcide","Error when getting next messages", e);

        }

    }

   @ReactMethod
    public void sendFile(String uri, String name, String mime, String extension, final Promise promise) {
        File file = null;
        try {
            Activity activity = getContext().getCurrentActivity();
            if (activity == null) {
                promise.reject("errorcide","Error");
            }

            InputStream inp = activity.getContentResolver().openInputStream(Uri.parse(uri));
            if (inp != null) {
                file = File.createTempFile("webim", extension, activity.getCacheDir());
                writeFully(file, inp);
            }
        } catch (IOException e) {
            if (file != null) {
                file.delete();
            }
            promise.reject("errorcide","Error", e);
        }
        if (file != null && name != null) {
            final File fileToUpload = file;
           session.getStream().sendFile(fileToUpload, name, mime, new MessageStream.SendFileCallback() {
                @Override
                public void onProgress(@NonNull Message.Id id, long sentBytes) {
                }

                @Override
                public void onSuccess(@NonNull Message.Id id) {
                    fileToUpload.delete();
                    promise.resolve(id.toString());
                }

                @Override
                public void onFailure(@NonNull Message.Id id, @NonNull WebimError<SendFileError> error) {
                    fileToUpload.delete();
                    String msg;
                    switch (error.getErrorType()) {
                        case FILE_TYPE_NOT_ALLOWED:
                            msg = "type not allowed";
                            break;
                        case FILE_SIZE_EXCEEDED:
                            msg = "file size exceeded";
                            break;
                        default:
                            msg = "unknown";
                    }
                    promise.reject("errorcide","Error");
                }
            });
        } else {
            promise.reject("errorcide","Error");
        }
    }

    private WritableMap getSimpleMap(String key, String value) {
        WritableMap map = Arguments.createMap();
        map.putString(key, value);
        return map;
    }

    private static void writeFully(@NonNull File to, @NonNull InputStream from) throws IOException {
        byte[] buffer = new byte[4096];
        OutputStream out = null;
        try {
            out = new FileOutputStream(to);
            for (int read; (read = from.read(buffer)) != -1; ) {
                out.write(buffer, 0, read);
            }
        } finally {
            from.close();
            if (out != null) {
                out.close();
            }
        }
    }

    private ReactApplicationContext getContext() {
        return reactContext;
    }
    
    @ReactMethod
    public void getUnreadByVisitorMessageCount(Promise promise) {
        try {
            int count = session.getStream().getUnreadByVisitorMessageCount();
            promise.resolve(count);
        } catch (Exception e) {
            promise.reject("errorcode", "Getting count of unread messages error", e);
        }
    }

    @ReactMethod
    public void setChatRead(Promise promise) {
        try {
           session.getStream().setChatRead();
            promise.resolve("success");
        } catch (Exception e) {
            promise.reject("errorcode", "Chat reading error", e);
        }
    }
}
