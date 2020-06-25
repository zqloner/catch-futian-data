package com.mgl.utils.ftp.bju;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * ftp上传工具类
 * @author zhaohy
 */
@Component
public class SFTPChannel {

	private static Session session = null;
	private static Channel channel = null;
	private static Logger LOGGER = LoggerFactory.getLogger(SFTPChannel.class);


	public static String host;
	public static String portf;
	public static String username;
	public static String password;
	public static int defatultport;
	public static String serverpath;
	public static String reqUrl;

	@Value("${ftp.host}")
	public void setHost(String host) {
		SFTPChannel.host = host;
	}
	@Value("${ftp.port}")
	public void setPort(String port) {
		SFTPChannel.portf = port;
	}
	@Value("${ftp.username}")
	public void setUsername(String username) {
		SFTPChannel.username = username;
	}
	@Value("${ftp.password}")
	public void setPassword(String password) {
		SFTPChannel.password = password;
	}
	@Value("${ftp.defatultport}")
	public void setDefatultport(int defatultport) {
		SFTPChannel.defatultport = defatultport;
	}
	@Value("${ftp.serverpath}")
	public void setServerpath(String serverpath) {
		SFTPChannel.serverpath = serverpath;
	}

	@Value("${ftp.req.url}")
	public void setReqUrl(String reqUrl){
		SFTPChannel.reqUrl = reqUrl;
	}

	public static ChannelSftp getChannel(int timeout) throws JSchException {
		if(channel!=null&&channel.isConnected()){
			return (ChannelSftp) channel;
		}
		String ftpHost = host;
		String port = portf; //Constants.SFTP_REQ_PORT;
		String ftpUserName = username;// Constants.SFTP_REQ_USERNAME;
		String ftpPassword = password;//Constants.SFTP_REQ_PASSWORD;
		int ftpPort = defatultport;
		if (port != null && !port.equals("")) {
			ftpPort = Integer.valueOf(port);
		}
		JSch jsch = new JSch(); // 创建JSch对象
		session = jsch.getSession(ftpUserName, ftpHost, ftpPort); // 根据用户名，主机ip，端口获取一个Session对象
		LOGGER.debug("Session created.");
		if (ftpPassword != null) {
			session.setPassword(ftpPassword); // 设置密码
		}
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config); // 为Session对象设置properties
		session.setTimeout(timeout); // 设置timeout时间
		session.connect(); // 通过Session建立链接
		LOGGER.debug("Session connected." + session);
		LOGGER.debug("Opening Channel.");
		channel = session.openChannel("sftp"); // 打开SFTP通道
		channel.connect(); // 建立SFTP通道的连接
		LOGGER.debug("Connected successfully to ftpHost = " + ftpHost + ",as ftpUserName = " + ftpUserName + ", returning: " + channel);
		return (ChannelSftp) channel;
	}

	public static void closeChannel() throws Exception {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}
}