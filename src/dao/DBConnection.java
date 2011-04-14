/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DB Access Class!
 *
 * @author Administrator
 */
public class DBConnection {
    private int m_port = -1;
    private String m_host = null;
    private String m_dbName = "";

    public DBConnection(int port, String host, String db_name){
        m_port = port;
        m_host = host;
        m_dbName = db_name;

        //デフォルトポート
        if(m_port == -1)m_port = 27017;
    }

    public int getDBPort(){
        return m_port;
    }
    public String getDBName(){
        return m_dbName;
    }
    public String getDBHost(){
        return m_host;
    }


    /**
     * コレクション名のリストを返却する
     * @return
     * @throws UnknownHostException
     */
    public String[] getCollectionNames() throws UnknownHostException, MongoException{
        Mongo m = new Mongo(m_host, m_port);
        DB db = m.getDB(m_dbName);

        Set<String> colls = db.getCollectionNames();
        return colls.toArray(new String[]{});
    }

    public synchronized void insert(String collection, Map data) throws MongoException, UnknownHostException{
        Mongo m = new Mongo(m_host, m_port);
        DB db = m.getDB(m_dbName);
        DBCollection coll = db.getCollection(collection);

        WriteResult w = coll.insert( map2DBObject(data) );
        System.out.println(w);
    }

    public synchronized void update(String collection, Map upobj, Map data) throws MongoException, UnknownHostException{
        Mongo m = new Mongo(m_host, m_port);
        DB db = m.getDB(m_dbName);
        DBCollection coll = db.getCollection(collection);

        WriteResult w = coll.update( map2DBObject(upobj), map2DBObject(data) );
        System.out.println(w);
    }

    public synchronized void delete(String collection, Map data) throws MongoException, UnknownHostException{
        Mongo m = new Mongo(m_host, m_port);
        DB db = m.getDB(m_dbName);
        DBCollection coll = db.getCollection(collection);

        WriteResult w = coll.remove( map2DBObject(data) );
        System.out.println(w);
    }

    public List<Map<String, Object>> read(String collection, Map data) throws MongoException, UnknownHostException{
        Mongo m = new Mongo(m_host, m_port);
        DB db = m.getDB(m_dbName);
        DBCollection coll = db.getCollection(collection);

        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

        DBCursor cur = coll.find( map2DBObject(data) );
        for( DBObject buf : cur.toArray().toArray(new DBObject[]{})){
            ret.add(buf.toMap());
        }
        
        return ret;

    }

    /**
     * HashMapをDBObjectに変換する
     *
     * @param map
     * @return
     */
    private static DBObject map2DBObject(Map map){
        DBObject ret = new BasicDBObject ();
        for(String key:(String[])map.keySet().toArray(new String[]{})){
            if(map.get(key) instanceof Map){
                ret.put(key, map2DBObject((Map)map.get(key)));
            }else{
                ret.put(key, map.get(key));
            }
        }
        return ret;
    }

}
