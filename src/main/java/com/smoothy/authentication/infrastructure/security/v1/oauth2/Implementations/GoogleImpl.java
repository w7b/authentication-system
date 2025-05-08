package com.smoothy.authentication.infrastructure.security.v1.oauth2.Implementations;
import com.smoothy.authentication.infrastructure.security.v1.oauth2.repository.iOAuthRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.net.URL;

public class GoogleImpl implements iOAuthRepository {

    private final OAuth2User user;

    public GoogleImpl(OAuth2User user) {
        this.user = user;
    }

    @Override
    public String getAvatar_url() {
        Object value = user.getAttribute("picture");
        return value instanceof URL ? ((URL) value).toString() : String.valueOf(value);
    }

    @Override
    public String getAccountId() {
//        if (user instanceof OidcUser oidcUser) {
//            return oidcUser.getSubject();
//        }
        return (String) user.getAttribute("sub");
    }

    @Override
    public String getEmail() {
        return (String) user.getAttribute("email");
    }

    @Override
    public String getName() {
        String givenName = user.getAttribute("given_name");
        String familyName = user.getAttribute("family_name");

        if (givenName != null && familyName != null) {
            return givenName + " " + familyName;
        }
        if (givenName != null) {
            return givenName;
        }
        if (familyName != null) {
            return familyName;
        }
        return user.getAttribute("name");
    }

    @Override
    public String getLocale() {
        return user.getAttribute("locale");
    }

}
