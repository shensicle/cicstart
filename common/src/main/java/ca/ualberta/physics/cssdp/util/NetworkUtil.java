package ca.ualberta.physics.cssdp.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class NetworkUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(NetworkUtil.class);

	/**
	 * Utility method to help us determine which server we're running on
	 * 
	 * @param ipOrHostname
	 * @return
	 */
	public static boolean currentlyRunningOn(String ipOrHostname) {

		Enumeration<NetworkInterface> ifaces;
		try {
			ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface iface = ifaces.nextElement();
				Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
				while (inetAddrs.hasMoreElements()) {
					InetAddress inetAddr = inetAddrs.nextElement();
					if (inetAddr.getHostAddress().equals(ipOrHostname)
							|| inetAddr.getHostName().equals(ipOrHostname)) {
						return true;
					}
				}
			}
		} catch (SocketException e) {
			logger.error("Could not determine interface addresses", e);
			Throwables.propagate(e);
		}

		return false;

	}

	/**
	 * Utility method to determine if the given host reachable. Usefull when
	 * waiting for VMs to startup in the cloud.
	 * 
	 * @param host
	 * @param numTries
	 * @return
	 */
	public static boolean isReachable(String host, int numTries) {

		int tryNo = 0;

		boolean reachable = false;
		while (tryNo < numTries) {

			Socket socket = null;
			try {
				socket = new Socket(host, 22);
				reachable = true;
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					break;
				}
				logger.info("not reachable yet, trying again (" + tryNo + "/"
						+ numTries + ") - " + e.getMessage());
				tryNo++;
			} finally {
				if (socket != null)
					try {
						socket.close();
					} catch (IOException ignore) {
					}
			}
		}
		return reachable;
	}

}