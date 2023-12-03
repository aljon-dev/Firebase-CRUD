package com.example.noteapp;

public class Note {
    String key, title, content;

    public Note() {

    }



    public String getTitle(){
        return title;

    }
    public String  getKey(){
        return key;
    }

    public String getContent(){

        return content;
    }

    public void setKey(String setKey){
        this.key = setKey;
    }

    public void setContent(String setContent) {
        this.content = setContent;
    }

    public  void setTitle (String setTitle){

        this.title = setTitle;

    }




}


