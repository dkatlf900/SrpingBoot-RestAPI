package com.example.szssonjunyoung.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("MessageSourceService")
public class MessageSourceService {

    @Autowired
    MessageSource ms;

    public String getLocaleMsg(String code, Locale locale) {
        String resultMsg = "";
        resultMsg = ms.getMessage(code, null, locale);

        return resultMsg;
    }


}
