package com.github.hollykunge.security.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hollykunge
 */
public class ClientUtil {
	/**
	 * 获取客户端真实ip
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request){
		String ip = request.getHeader("clientIp");
		if (ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取用户身份证
	 * @param request
	 * @return
	 */
	public static String getPid(HttpServletRequest request, String userId){
		String pid = request.getHeader("pid");
		if (pid==null||pid.length()==0||"unknown".equalsIgnoreCase(pid)) {
			pid = request.getHeader("pid");
		}
		if (pid==null||pid.length()==0||"unknown".equalsIgnoreCase(pid)) {
			pid = userId;
		}
		return pid;
	}
}
