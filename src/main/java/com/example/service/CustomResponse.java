package com.example.service;

import com.example.model.MetaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class CustomResponse {

    public static <T>ResponseEntity<Map> ok(){
        MetaResponse metaInfo = getMeta("SUCCESS", 200, false);
        //Prepare response
        Map<String, Object> map = new HashMap<>();
        map.put("meta", metaInfo);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    public static <T> ResponseEntity<Map> ok(T data){
        //Prepare meta info
        MetaResponse metaInfo = getMeta("SUCCESS", 200, false);
        //Prepare response
        Map<String, Object> map = new HashMap<>();
        map.put("meta", metaInfo);
        map.put("data", data);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static MetaResponse getMeta(String msg, int statusCode, boolean isError) {
        MetaResponse metaInfo = new MetaResponse();
        metaInfo.setMsg(msg);
        metaInfo.setStatusCode(statusCode);
        metaInfo.setError(isError);
        return metaInfo;
    }


}
