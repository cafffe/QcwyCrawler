package com.kmgh;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * ConfigurationRead 属�?�文件读取类
 * </p>
 * <p>
 * Copyright 2007 XWTECH INC. All Rights reserved
 * </p>
 * <p>
 * XWTECH INC.
 * </p>
 * 
 * @author huangyi
 * @version 1.0
 */
public class ConfigurationRead
{
    
    
    /** 属�?�文件类 */
    public Properties properties;
    
    /** 路径 */
    private String strPropertyPath = "/";
    
    private String strPropertyFile = "config.properties";
    
    /** 单例 */
    private final static ConfigurationRead CFG_INSTANCE = new ConfigurationRead();
    
    /**
     * �?
     */
    private ConfigurationRead()
    {
        
    }
    
    /**
     * 得单�?
     * 
     * @return ConfigurationRead
     */
    public static ConfigurationRead getInstance()
    {
        return CFG_INSTANCE;
    }
    
    /**
     * Key取�??
     * 
     * @param strKey
     *            String
     * @return String
     */
    public String getValue(String strKey)
    {
        return properties.getProperty(strKey);
    }
    
    /**
     * 设置
     * 
     * @param strPropertyFile
     *            String
     */
    public void setPropertyFile(String strPropertyFile)
    {
        this.strPropertyFile = strPropertyFile;
    }
    
    /**
     * 根据路径创建properties
     * 
     * @param path
     * @return
     */
    public Properties propertiesCreate(String path)
    {
        
        Properties properties = new Properties();
        InputStream is = null;
        try
        {
            is = getClass().getResourceAsStream("/" + path);
            properties.load(is);
            return properties;
        }
        catch (Exception e)
        {
            try
            {
                throw new Exception("ConfigReader read file Exception");
            }
            catch (Exception e1)
            {
            }
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                    is = null;
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return properties;
    }
    
    /**
     * 根据绝对路径创建properties
     * 
     * @param path
     * @return
     */
    public Properties propertiesCreateByPath(String path)
    {
        
        Properties properties = new Properties();
        InputStream is = null;
        try
        {
            // is = getClass().getResourceAsStream(path);
            is = new BufferedInputStream(new FileInputStream(path));
            properties.load(is);
            return properties;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                throw new Exception("ConfigReader read file Exception");
            }
            catch (Exception e1)
            {
                // TODO 自动生成 catch �?
            }
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                    is = null;
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return properties;
    }
    
    /**
     * 得到配置文件中所有项目key
     */
    public String[] getAllItem(Properties properties)
    {
        Enumeration en = properties.propertyNames();
        
        List list = new ArrayList();
        
        while (en.hasMoreElements())
        {
            list.add(en.nextElement());
        }
        return (String[])list.toArray(new String[list.size()]);
        
    }
    
    /**
     * 得到配置文件中所有项目key
     */
    public String[] getAllItem()
    {
        Enumeration en = properties.propertyNames();
        
        List list = new ArrayList();
        
        while (en.hasMoreElements())
        {
            list.add(en.nextElement());
        }
        return (String[])list.toArray(new String[list.size()]);
        
    }
    
    /**
     * 设置应用上下文路�?
     */
    public void setAppPath(String appPath)
    {
        properties.put("appPath", appPath);
    }
    
    /**
     * 取应用上下文路径
     * 
     * @return String
     */
    public String getAppPath()
    {
        return (String)properties.get("appPath");
    }
    
}
