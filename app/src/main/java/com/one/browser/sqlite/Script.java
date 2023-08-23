package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class Script {
    private String title;
    private String script;
    private String state;
    private String time;


    /*无参构造方法*/

    public Script(){

    }

    /*有参构造方法*/

    public Script(String title, String script, String state, String time) {
        this.title = title;
        this.script = script;
        this.state = state;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
