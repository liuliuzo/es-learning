package com.liuliu.es.learning.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContexts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuliu e077417
 * @version 1.0
 * @email Liuliu.Zhao@mastercard.com
 * @date 12/9/2019 6:46 PM
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SSLContextUtils {

	public static SSLContext getSSLContext(String trustStorePath) {
		SSLContext sslContext = null;
		File file = new File(trustStorePath);
		try (FileInputStream instream = new FileInputStream(file);) {
			KeyStore trustStore = null;
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(instream, null);
			sslContext = SSLContexts
						.custom()
						.loadTrustMaterial(trustStore, null)
						.build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return sslContext;
	}

	public static final HostnameVerifier createSkipHostnameVerifier() {
		return (requestedHost, remoteServerSession) -> {
			log.debug("requestedHost={},remoteServerSessio={}", requestedHost, remoteServerSession);
			return requestedHost.equalsIgnoreCase(remoteServerSession.getPeerHost());
		};
	}
}
