package app.techify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Map;

@Service
public class GoogleAuthService {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private RestTemplate restTemplate;

    public String createAuthorizationURL() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        return clientRegistration.getProviderDetails().getAuthorizationUri() + "?" +
                "client_id=" + clientRegistration.getClientId() +
                "&redirect_uri=" + clientRegistration.getRedirectUri() +
                "&response_type=code" +
                "&scope=" + String.join(" ", clientRegistration.getScopes());
    }

    public String getUserEmailFromToken(OAuth2AuthenticationToken token) {
        OAuth2User user = token.getPrincipal();
        return user.getAttribute("email");
    }

    public Map<String, Object> getUserInfo(OAuth2AuthenticationToken token) {
        OAuth2User user = token.getPrincipal();
        return user.getAttributes();
    }

    public OAuth2AccessToken getAccessToken(OAuth2AuthenticationToken token) {
        return authorizedClientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName()
        ).getAccessToken();
    }
    public String getAccessTokenFromCode(String code) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");

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
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }
}