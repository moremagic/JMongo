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
     * データを全て取得する
     *
     * @return コレクション名
     * @throws UnknownHostException
     */
    public String[] findString(String collection) throws UnknownHostException, MongoException{
        List<String> ret = new ArrayList<String>();
        for(Map m: m_dao.read(collection, new HashMap()).toArray(new Map[]{})){
            try{
                ret.add( JSON.serialize(m) );
            }catch(OutOfMemoryError err){
                ret.add("{ \"_id\" : " + m.get("_id") + ", \"GUI_ERR\": \"" + err.getMessage() + "\" }");
            }
        }

        return ret.toArray(new String[]{});
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
                m_dao.update(collection, (Map)JSON.parse(selectData), (Map)JSON.parse(newData));
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
