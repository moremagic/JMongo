/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

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
            m_properties.loadFromXML(new FileInputStream(_CONFIG_FILE));
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
    public String getProperty(String confName){
        return m_properties.getProperty(confName);
    }

    /**
     * 新しい設定を追加する
     *
     * @param confName 設定名
     * @param host ホスト名
     * @param port ポート番号
     * @param dbName DB名
     */
    public void setConfig(String confName, String host, int port, String dbName){
        m_properties.setProperty(confName, host + ":" + port + "/" + dbName);
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
                m_properties.storeToXML(new FileOutputStream(_CONFIG_FILE), "mongoDB Connection Config", "UTF-8");
                ret = true;
            }
        }catch(IOException err){
           err.printStackTrace();
        }
        return ret;
    }
}
