package com.fibbery.commons.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author fibbery
 * @date 18/7/2
 */
@Slf4j
public class NetUtils {

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final int RND_PORT_START = 30000;

    private static final int RND_PORT_RANGE = 10000;

    private static volatile InetAddress LOCAL_ADDRESS = null;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getAvaliablePort() {
        ServerSocket server = null;
        try {
            server = new ServerSocket();
            server.bind(null);
            return server.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    public static InetAddress getLocalAddress0() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            if (isValidAddress(address)) {
                return address;
            }
        } catch (Throwable e) {
            log.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network= networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address= addresses.nextElement();
                    if (isValidAddress(address)) {
                        return address;
                    }
                }
            }
        } catch (SocketException e) {
            log.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }

        log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return address;
    }

    public static void main(String[] args) {
        System.out.println(getAvaliablePort());
        System.out.println(getLocalAddress().getHostAddress());
    }
}
