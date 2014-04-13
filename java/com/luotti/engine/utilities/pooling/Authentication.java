package com.luotti.engine.utilities.pooling;

public class Authentication {

    private String username;
    private String password;

    Authentication(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    @Override
    public int hashCode()
    {
        return (password == null ? 0 : password.hashCode());
    }

    @Override
    public boolean equals(Object wrapper)
    {
        Authentication credentials = ((Authentication) wrapper);
        if (username != null && !username.equals(credentials.username))
        {
            return false;
        }
        else if (username != credentials.username)
        {
            return false;
        }
        else if (password != null && !password.equals(credentials.password))
        {
            return false;
        }
        else if (password != credentials.password)
        {
            return false;
        }

        return true;
    }
}