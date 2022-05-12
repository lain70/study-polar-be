package com.polar.bear.api.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
	public static void responseMessage(HttpServletResponse response, HttpStatus status, String code, String msg) throws IOException {

		JSONObject json = new JSONObject();
		json.put("ERROR_CODE", code);
		json.put("ERROR_MSG", msg);		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(status.value());
		PrintWriter writer = response.getWriter();
		writer.print(json.toString());
		writer.flush();
		writer.close();
	}
	
	public static ResponseEntity<?> getResponseEntity(String errorMsg, HttpHeaders responseHeaders, HttpStatus status) {
		JSONObject json = new JSONObject();
		try {
			json.put("ERROR_CODE", status.getReasonPhrase());
			json.put("ERROR_MSG", errorMsg);
			responseHeaders.add("Status-Code", String.valueOf(status.value()));

		} catch (JSONException e1) {
//			e1.printStackTrace();
		}

		responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
		return new ResponseEntity<>(json.toString(), responseHeaders, status);
	}
}
