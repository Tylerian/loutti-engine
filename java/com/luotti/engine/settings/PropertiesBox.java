package com.luotti.engine.settings;

import com.luotti.engine.Environment;
import com.luotti.engine.utilities.memory.IDisposable;

import java.util.Properties;
import java.io.FileInputStream;

public class PropertiesBox extends Properties implements IDisposable {

    @Override
    public void destruct()
    {
        this.clear();
    }

    public String get(String key)
    {
        return this.get(key, null);
    }

    public int getInt(String key)
    {
        return this.getInt(key, 0);
    }

    public short getShort(String key)
    {
        return this.getShort(key, (short) 0);
    }

    public boolean getBoolean(String key)
    {
        return Boolean.parseBoolean(this.get(key));
    }

    public int getInt(String key, int defaultVal)
    {
        try
        {
            return Integer.parseInt(this.get(key));
        }

        catch (Exception ex) { } return defaultVal;
    }

    public short getShort(String key, short defaultVal)
    {
        try
        {
            return Short.parseShort(this.get(key));
        }

        catch (Exception ex) { } return defaultVal;
    }

    public boolean getBoolean(String key, boolean defaultVal)
    {
        try
        {
            return Boolean.parseBoolean(this.get(key));
        }

        catch (Exception ex) { } return defaultVal;
    }

    public boolean bootstrap(String path)
    {
        try
        {
            super.load(new FileInputStream(path));
        }

        catch (Exception ex)
        { Environment.printOutBootError(ex.getMessage()); return false; }

        return true;
    }

    public String get(String key, String defaultVal)
    {
        return super.getProperty(key, defaultVal).replace("#", "").replace(" ", "").trim();
    }
}