package com.java6.todolist.format;

public class ResultData<T> {
    public boolean result;  //명령어 처리 결과
    public T message;		//각 명령어 처리에 대한 결과 데이터(에러메세지, id 등 명령어마다 다름)

    public ResultData(boolean result, T message) {
        this.result = result;
        this.message = message;
    }
}