package com.board.boardapp.service;


public interface ObjectReadValueService {

    public <T> T setReadValue(T dto, String responseValue);
}
