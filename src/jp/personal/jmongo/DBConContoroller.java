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
    private MongoIFrame m_gui = null;
    private DBConnection m_dao = null;
    private DBActionManager m_act = null;

    /**
     * コンストラクタ
     *
     * @param port 接続ポート番号
     * @param host 接続ホスト
     * @param dbName DB名
     */
    public DBConContoroller(int port, String host, String dbName){
        m_dao = new DBConnection(port, host, dbName);
    }

    /**
     * GUIを返却する
     *
     * @return
     */
    public MongoIFrame getGUI(){
        if(m_gui == null){
            m_gui = new MongoIFrame(this);
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
            ret.add( JSON.serialize(m) );
        }

        return ret.toArray(new String[]{});
    }


    public boolean update(String collection, String upobj, String data){
        try{
            m_dao.update(collection, (Map)JSON.parse(upobj), (Map)JSON.parse(data));
            return true;
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

    public boolean delete(String collection, String data){
        try{
            m_dao.delete(collection, (Map)JSON.parse(data));
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
