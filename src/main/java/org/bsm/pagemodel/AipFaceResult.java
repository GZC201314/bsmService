package org.bsm.pagemodel;

public class AipFaceResult {
    private Result result;


    private String error_msg;

    private int cached;

    private int error_code;

    private int timestamp;

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getError_msg() {
        return this.error_msg;
    }

    public void setCached(int cached) {
        this.cached = cached;
    }

    public int getCached() {
        return this.cached;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public int getError_code() {
        return this.error_code;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return this.timestamp;
    }
}