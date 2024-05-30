package com.iprody08.inquiryservice.dto.restclient;

import com.iprody08.inquiryservice.dto.InquiryDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class InquiryRestClient {
    @Getter
    private final RestTemplate restTemplate;

    @Setter
    private String url;

    InquiryRestClient(RestTemplate restTemplate, String endpoint) {
        this.restTemplate = restTemplate;
        this.url = endpoint;
    }

    public ResponseEntity<List<InquiryDto>> getListInquiries() {
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InquiryDto>>() {

                }
        );
    }

    public ResponseEntity<InquiryDto> getInquiryDto() {
        return restTemplate.getForEntity(url, InquiryDto.class);
    }

    public InquiryDto postInquiryDto(InquiryDto inquiryDto) {
        return restTemplate.postForObject(url, inquiryDto, InquiryDto.class);
    }

    public static CustomerRestClientBuilder builder() {
        return new CustomerRestClientBuilder();
    }

    public static class CustomerRestClientBuilder {
        private String url;

        private boolean ignore;

        CustomerRestClientBuilder() {

        }

        public CustomerRestClientBuilder url(String endpoint) {
            this.url = endpoint;
            return this;
        }

        public CustomerRestClientBuilder enableHttpsWithIgnoreSelfSignCertificate(boolean enable) {
            this.ignore = enable;
            return this;
        }

        public InquiryRestClient build() {
            RestTemplate restTemplate;
            if (ignore) {
                restTemplate = ignoreSelfSignCertificate();
            } else {
                restTemplate = new RestTemplate();
            }
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            if (url == null) {
                throw new IllegalArgumentException("URL path cant be null");
            }

            return new InquiryRestClient(restTemplate, url);

        }

        private RestTemplate ignoreSelfSignCertificate() {
            try {
                SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(
                        SSLContexts.custom().loadTrustMaterial(
                                null, new TrustSelfSignedStrategy()).build(),
                        NoopHostnameVerifier.INSTANCE);

                PoolingHttpClientConnectionManager connectionManager =
                        PoolingHttpClientConnectionManagerBuilder
                                .create()
                                .setSSLSocketFactory(csf)
                                .build();

                CloseableHttpClient httpClient = HttpClients.custom()
                        .setConnectionManager(connectionManager)
                        .build();

                HttpComponentsClientHttpRequestFactory requestFactory =
                        new HttpComponentsClientHttpRequestFactory();

                requestFactory.setHttpClient(httpClient);

                return new RestTemplate(requestFactory);

            } catch (NoSuchAlgorithmException
                     | KeyStoreException
                     | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

