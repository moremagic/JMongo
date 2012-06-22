/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmongo.config;

/**
 * DB設定Bean
 * 
 * @author mitsu
 */
public class DBConnectionBean {
    private String m_ConfName = "";//ConfigName
    private String m_host = "";//DBHost
    private int m_port = 27017;//DBPort
    private String m_DBName = "";//DBName
    private String m_DBUser = "";//DBUser
    private String m_DBPass = "";//DBPasswd

    public DBConnectionBean(String confName, String host, int port, String dbName, String dbUser, String dbPass) {
        m_ConfName = confName;
        m_host = host;
        m_port = port;
        m_DBName = dbName;
        m_DBUser = dbUser;
        m_DBPass = dbPass;
    }

    public String getM_host() {
        return m_host;
    }

    public void setM_host(String m_host) {
        this.m_host = m_host;
    }
    
    public String getM_ConfName() {
        return m_ConfName;
    }

    public void setM_ConfName(String m_ConfName) {
        this.m_ConfName = m_ConfName;
    }

    public String getM_DBName() {
        return m_DBName;
    }

    public void setM_DBName(String m_DBName) {
        this.m_DBName = m_DBName;
    }

    public String getM_DBPass() {
        return m_DBPass;
    }

    public void setM_DBPass(String m_DBPass) {
        this.m_DBPass = m_DBPass;
    }

    public String getM_DBUser() {
        return m_DBUser;
    }

    public void setM_DBUser(String m_DBUser) {
        this.m_DBUser = m_DBUser;
    }

    public int getM_port() {
        return m_port;
    }

    public void setM_port(int m_port) {
        this.m_port = m_port;
    }
}
