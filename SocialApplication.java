package com.tts.social;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.oauth2.sdk.OAuth2Error;


@SpringBootApplication
@RestController
public class SocialApplication extends WebSecurityConfigurerAdapter{

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
	
	/*@GetMapping("/error")
	public String error() {
		String message = (String) request.getSession().getAttribute("error.message");
		request.getSession().removeAttribute("error.message");
		return message;
	}*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	// @formatter:off
        http
            .authorizeRequests(a -> a
                .antMatchers("/", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .logout(l -> l
                    .logoutSuccessUrl("/").permitAll()
                )
            .csrf(c -> c
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
            .oauth2Login();
        // @formatter:on
    }
    
    
    /*
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
	    DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
	    return request -> {
	        OAuth2User user = delegate.loadUser(request);
	        if (!"github".equals(request.getClientRegistration().getRegistrationId())) {
	        	return user;
	        }

	        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
	                (request.getClientRegistration(), user.getName(), request.getAccessToken());
	        String url = user.getAttribute("organizations_url");
	        List<Map<String, Object>> orgs = ((Object) rest
	                .get()).uri(url)
	                .attributes(oauth2AuthorizedClient(client))
	                .retrieve()
	                .bodyToMono(List.class)
	                .block();

	        if (((Object) orgs.stream()).anyMatch(org -> "spring-projects".equals(org.get("login")))) {
	            return user;
	        }

	        throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in Spring Team", ""));
	    };
	}
	private Object oauth2AuthorizedClient(OAuth2AuthorizedClient client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Bean
	public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {
	    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
	            new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
	    return ((Object) WebClient.builder())
	            .filter(oauth2).build();
	}*/
    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
    
    

}
