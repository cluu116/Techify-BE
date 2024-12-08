package app.techify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FacebookAuthService {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private RestTemplate restTemplate;

    public String createAuthorizationURL() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("facebook");
        return clientRegistration.getProviderDetails().getAuthorizationUri() + "?" +
                "client_id=" + clientRegistration.getClientId() +
                "&redirect_uri=" + clientRegistration.getRedirectUri() +
                "&response_type=code" +
                "&scope=" + String.join(" ", clientRegistration.getScopes());
    }

    public String getAccessTokenFromCode(String code) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("facebook");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", clientRegistration.getClientId());
        map.add("client_secret", clientRegistration.getClientSecret());
        map.add("redirect_uri", clientRegistration.getRedirectUri());
        map.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                clientRegistration.getProviderDetails().getTokenUri(),
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public Map<String, Object> getUserInfoFromAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://graph.facebook.com/v21.0/me?fields=id,name,email,picture",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> userInfo = response.getBody();

        // Xử lý trường hợp email không được trả về
        if (!userInfo.containsKey("email")) {
            userInfo.put("email", userInfo.get("id") + "@facebook.com");
        }

        return userInfo;
    }
}