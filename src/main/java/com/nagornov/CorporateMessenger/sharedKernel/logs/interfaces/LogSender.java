package com.nagornov.CorporateMessenger.sharedKernel.logs.interfaces;


import com.nagornov.CorporateMessenger.sharedKernel.logs.model.Log;

public interface LogSender {

    void sendLog(Log log);

}