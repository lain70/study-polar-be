package com.polar.bear.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogTool {
    private StringBuffer logSb;

    public LogTool(){
        if(logSb == null){
            logSb = new StringBuffer();
            logSb.append("\r\n[Request Start        ] " + getDate("yyyy/MM/dd HH:mm:ss.SSS") + "\r\n");
        }
    }

    public StringBuffer getLog(){
        if(logSb == null){
            logSb = new StringBuffer();
            logSb.append("\r\n[Request Start        ] " + getDate("yyyy/MM/dd HH:mm:ss.SSS") + "\r\n");
        }

        return logSb;
    }

    public void addLog(String message){
        if(logSb == null){
            logSb = new StringBuffer();
            logSb.append("\r\n[Request Start        ] " + getDate("yyyy/MM/dd HH:mm:ss.SSS") + "\r\n");
        }

        logSb.append(message + "\r\n");
    }

    public String toString(){
        logSb.append("[Request End          ] " + getDate("yyyy/MM/dd HH:mm:ss.SSS") + "\r\n");
        return logSb.toString();
    }
    
	/**
	 * 현재날짜 가져오기
	 * 
	 * @param format
	 * @return
	 */
	public static String getDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format,
				Locale.KOREA);
		return sdf.format(new Date());
	}
}
