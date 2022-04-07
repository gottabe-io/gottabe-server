package io.gottabe.commons.security.oauth;

import io.gottabe.commons.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TokenAuthentication implements Authentication {

    private static final long serialVersionUID = 1L;

    final User user;
    final Set<GrantedAuthority> auths = new HashSet<>();

    public TokenAuthentication(User user) {
        this.user = user;
        auths.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return auths;
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public Object getDetails() {
        return user;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return user != null;
    }

    @Override
    public void setAuthenticated(boolean arg0) throws IllegalArgumentException {

    }

    public User getUser() {
        return user;
    }

}
