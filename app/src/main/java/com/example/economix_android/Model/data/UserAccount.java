package com.example.economix_android.Model.data;

import java.util.Objects;

/**
 * Simple in-memory representation of a registered user.
 */
public final class UserAccount {

    private final String name;
    private final String email;
    private final String password;

    public UserAccount(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAccount that = (UserAccount) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}