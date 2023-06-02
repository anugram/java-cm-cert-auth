package com.example.demo;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CmCertAuthApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CmCertAuthApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		final String password = "P@ssw0rd!";
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(new FileInputStream(ResourceUtils.getFile("classpath:bundle.p12")), "P@ssw0rd!".toCharArray());

		SSLContext sslContext = SSLContextBuilder.create()
				.loadTrustMaterial(ResourceUtils.getFile("classpath:bundle.p12"), password.toCharArray())
				.loadKeyMaterial(keyStore, password.toCharArray())
				.build();
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				NoopHostnameVerifier.INSTANCE);
		final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("https", sslsf)
				.register("http", new PlainConnectionSocketFactory()).build();

		final BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
				socketFactoryRegistry);
		final CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
		final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		String url = "https://ciphertrust.com/api/v1/auth/tokens/";
		JwtRequestBean tokenRequest = new JwtRequestBean("user_certificate");
		HttpHeaders tokenRequestHeaders = new HttpHeaders();
		tokenRequestHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<JwtRequestBean> tokenRequestEntity = new HttpEntity<JwtRequestBean>(tokenRequest,
				tokenRequestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, tokenRequestEntity, String.class);

		System.out.println("Result = " + response.getBody());
	}

}
