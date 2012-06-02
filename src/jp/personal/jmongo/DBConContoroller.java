/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.personal.jmongo;

import action.DBActionManager;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import dao.DBConnection;
import gui.MongoIFrame;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB接続単位でのコントローラクラス
 * 以下情報を一元管理します
 *
 * ・GUI【JInternalFrame】
 * ・DAO【DBConnection】
 * ・Action【DBActionManager】
 *
 * @author mitsu
 */
public class DBConContoroller {
    private String m_confName = "";
    private MongoIFrame m_gui = null;
    private DBConnection m_dao = null;
    private DBActionManager m_act = null;

    /**
     * コンストラクタ
     *
     * @param confName
     * @param port
     * @param host
     * @param dbName
     * @param dbuser
     * @param dbpasswd
     * @throws UnknownHostException
     */
    public DBConContoroller(String confName, String host, int port, String dbName, String dbuser, String dbpasswd) throws UnknownHostException{
        m_dao = new DBConnection(host, port,dbName, dbuser, dbpasswd);
        m_confName = confName;
    }

    /**
     * GUIを返却する
     *
     * @return
     */
    public MongoIFrame getGUI(){
        if(m_gui == null){
            m_gui = new MongoIFrame(this);
            m_gui.setTitle(m_confName);
        }
        return m_gui;
    }

    /**
     * Actionマネージャを返却する
     *
     * @return
     */
    public DBActionManager getActionMgr(){
        if(m_act == null){
            m_act = new DBActionManager(this);
        }
        return m_act;
    }

    /**
     * DB名の取得
     *
     * @return DB名
     */
    public String getDBName(){
        return m_dao.getDBName();
    }

    /**
     * コレクション名を取得する
     *
     * @return コレクション名
     * @throws UnknownHostException
     */
    public String[] getCollections() throws UnknownHostException, MongoException{
        return m_dao.getCollectionNames();
    }

    /**
     * データを取得する
     *
     * @return コレクション名
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> findCollection(String collection, Map query, int skip, int limit) throws UnknownHostException, MongoException{
        return m_dao.selectRead(collection, null, query, skip, limit, null);
    }
    
    /**
     * コレクションの全体サイズを取得する
     * 
     * @param collection
     * @return 
     */
    public long getCollectionDataCount(String collection){
        return m_dao.count(collection, null);
    }

    /**
     * アップデートを行う
     * @return 成功したらTrue
     */
    public boolean update(){
        try{
            String collection = m_gui.getCollectionName();
            String newData = m_gui.getEditData();
            String selectData = m_gui.getSelectedData();
            if(selectData == null){
                return insert(collection, newData);
            }else{
                Map<String, Object> find = new HashMap<String, Object>();
                find.put("_id", ((Map)JSON.parse(selectData)).get("_id"));

                Map<String, Object> update =  (Map)JSON.parse(newData);
                m_dao.update(collection, find, update);
                return true;
            }
        }catch(Exception err){
            err.printStackTrace();
            return false;
        }
    }


    public boolean insert(String collection, String data){
        try{
            m_dao.insert(collection, (Map)JSON.parse(data));
            return true;
        }catch(Exception err){
            err.printStackTrace();
            return false;
        }
    }

    public boolean delete(){
        try{
            String collection = m_gui.getCollectionName();
            String newData = m_gui.getEditData();

            m_dao.delete(collection, (Map)JSON.parse(newData));
            return true;
        }catch(Exception err){
            err.printStackTrace();
            return false;
        }
    }

    public boolean drop(){
        try{
            String collection = m_gui.getCollectionName();
            m_dao.drop(collection);
            return true;
        }catch(Exception err){
            err.printStackTrace();
            return false;
        }
    }

    public boolean dump(File f){
        try{
            BufferedWriter bw = null;
            String collection = m_gui.getCollectionName();
            try{
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
            
                List<Map<String, Object>> list = m_dao.read(collection, null);
                for(Map m : list){
                    bw.write(JSON.serialize(m) + "\r\n");
                }
            }finally{
                if(bw != null)bw.close();
            }
            
            return true;
        }catch(Exception err){
            err.printStackTrace();
            return false;
        }
    }    
    
    public boolean load(File f){
        try{
            BufferedReader br = null;
            String collection = m_gui.getCollectionName();
            try{
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            
                String line = "";
                while((line = br.readLine()) != null){
                    Map<String, Object> m = (Map<String, Object>)JSON.parse(line);
                    m_dao.insert(collection, m);
                }
            }finally{
                if(br != null)br.close();
            }
            
            return true;
        }catch(Exception err){
            err.printStackTrace();
            return false;
        }
    }    
    
    
    /**
     * DBコントローラをクローズする
     *
     * ※メモリリーク対策
     */
    public void close(){
        m_gui = null;
        m_dao = null;
        m_act = null;
    }
}
