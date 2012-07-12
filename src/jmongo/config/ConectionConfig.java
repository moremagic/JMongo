/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmongo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBコネクションを管理するクラス
 *
 * @author mitsu
 */
public class ConectionConfig {
    private static final String _CONFIG_FILE = "dbconfig.xml";
    private Properties m_properties =null;

    public ConectionConfig() {
        try {
            m_properties = new Properties();
            
            File f = new File(System.getProperty("user.home") + "/jmongo", _CONFIG_FILE);
            m_properties.loadFromXML(new FileInputStream(f));
        } catch (IOException ex) {
            Logger.getLogger(ConectionConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 設定名の配列を取得する
     *
     * @return 設定名の配列
     */
    public String[] getConfigList(){
        return m_properties.stringPropertyNames().toArray(new String[]{});
    }

    /**
     * 設定を取得する
     *
     * @param confName 設定名
     * @return 接続設定
     */
    public DBConnectionBean getProperty(String confName){
        DBConnectionBean ret = null;
        String value = m_properties.getProperty(confName);
        
        try{
            String dbHost = value.substring(0, value.indexOf(":"));
            String port = value.substring(value.indexOf(":")+1, value.indexOf("/"));
            String dbName = value.substring(value.indexOf("/")+1, value.indexOf("@"));
            String dbUser = value.substring(value.indexOf("@") + 1, value.indexOf("="));
            String dbPasswd = value.substring(value.indexOf("=")+1);        
            ret = new DBConnectionBean(confName, dbHost, Integer.parseInt(port), dbName, dbUser, dbPasswd);
        }catch(Exception err){
            err.printStackTrace();
        }
        return ret;
    }

    /**
     * 新しい設定を追加する
     * 
     * @param conf 
     */
    public void setConfig(DBConnectionBean conf){
        setConfig(conf.getM_ConfName(), conf.getM_host(), conf.getM_port(), conf.getM_DBName(), conf.getM_DBUser(), conf.getM_DBPass());
    }
    
    /**
     * 新しい設定を追加する
     *
     * @param confName 設定名
     * @param host ホスト名
     * @param port ポート番号
     * @param dbName DB名
     */
    public void setConfig(String confName, String host, int port, String dbName, String dbuser, String dbpasswd){
        m_properties.setProperty(confName, host + ":" + port + "/" + dbName + "@" + dbuser + "=" + dbpasswd );
    }

    /**
     * 設定をファイルに保存する
     *
     * @return
     */
    public boolean save(){
        boolean ret = false;
        try{
            if(m_properties != null){
                File f = new File(System.getProperty("user.home") + "/jmongo", _CONFIG_FILE);
                if(!f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                }
                
                m_properties.storeToXML(new FileOutputStream(f), "mongoDB Connection Config", "UTF-8");
                ret = true;
            }
        }catch(IOException err){
           err.printStackTrace();
        }
        return ret;
    }
}
