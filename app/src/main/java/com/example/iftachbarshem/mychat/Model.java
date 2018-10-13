package com.example.iftachbarshem.mychat;

public class Model {
    private static Model instance;
    private JavaChatClient javaChatClient;
    private String myName;

    private String defaultSender;

    public static Model getInstance() {
        if  (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    private Model() {
        javaChatClient = new JavaChatClient();
        defaultSender = "anonymous";
        setMyName("My");
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public void subscribe(JavaChatClient.Callback callback) {
        javaChatClient.subscribe(callback);
    }

    public void unsubscribe(JavaChatClient.Callback callback) {
        javaChatClient.unsubscribe(callback);
    }
    public JavaChatClient getJavaChatClient() {
        return javaChatClient;
    }

    public String getDefaultSender() {
        return defaultSender;
    }
}
