/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.types.ObjectId;

/**
 * DB Access Class!
 *
 * @author Administrator
 */
public class DBConnection {
    private int m_port = -1;
    private String m_host = null;
    private String m_dbName = "";
    private Mongo m_Mongo = null;
    private DB m_MongoDB = null;



    public DBConnection(int port, String host, String db_name) throws UnknownHostException {
        m_port = port;
        m_host = host;
        m_dbName = db_name;
        //デフォルトポート
        if (m_port == -1) {
            m_port = 27017;
        }

        MongoOptions options = new MongoOptions();
        options.connectionsPerHost = 3;
        options.threadsAllowedToBlockForConnectionMultiplier = 2;

        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        addresses.add(new ServerAddress(m_host, m_port));

        m_Mongo = new Mongo(addresses, options);
        m_MongoDB = m_Mongo.getDB(m_dbName);

        System.out.println(m_Mongo);
    }

    public int getDBPort() {
        return m_port;
    }

    public String getDBName() {
        return m_dbName;
    }

    public String getDBHost() {
        return m_host;
    }

    /**
     * コレクション名のリストを返却する
     * @return
     * @throws UnknownHostException
     */
    public String[] getCollectionNames() {
        Set<String> colls = m_MongoDB.getCollectionNames();
        return colls.toArray(new String[]{});
    }

    /**
     *
     * コレクションに値を書き込む。
     *
     * @param collection name
     * @param get
     * @return ハッシュIDを返します。
     */
    public void insert(String collection, Map map) {
        DBCollection coll = m_MongoDB.getCollection(collection);
        WriteResult w = coll.insert(map2DBObject(map));
        System.out.println(w);
    }

    /**
     * Mongodb # FindAndModify
     * 特殊な値のインサートや削除を行えるメソッドです。
     * 詳細はMongoDBのドキュメントを参照ください
     *
     * @param collection
     * @param query
     * @param fields
     * @param sort
     * @param remove
     * @param update
     * @param returnNew
     * @param upsert
     * @return
     */
    public Map findAndModify(String collection, Map query, Map fields, Map sort, boolean remove, Map update, boolean returnNew, boolean upsert){
        DBCollection coll = m_MongoDB.getCollection(collection);
        DBObject ret = coll.findAndModify(map2DBObject(query), null, null, false, map2DBObject(update), true, true);

        //値がnullの場合は、空のMAPを返す。
        if ( ret == null ) return new HashMap();
        return map4DBObject(ret);
    }


    /**
     * 条件に一致したコレクション内の値を取得する。
     * 値が見つからなかった時はnullを返す。
     *
     * @param collection
     * @return
     * @throws UnknownHostException
     */
    public Map findOne(String collection, Map map) {
        DBCollection coll = m_MongoDB.getCollection(collection);
        DBObject findOne = coll.findOne(map2DBObject( map ));
        if (findOne == null) {
            return null;
        }
        return map4DBObject(findOne);
    }

    /**
     * update
     *
     * @param collection
     * @param upobj
     * @param data
     * @throws MongoException
     * @throws UnknownHostException
     */
    public void update(String collection, Map upobj, Map data){
        DBCollection coll = m_MongoDB.getCollection(collection);
        WriteResult w = coll.update( map2DBObject(upobj), map2DBObject(data) );
        System.out.println(w);
    }

     /**
     * count
     *
     * @param collection
     * @param upobj
     * @param data
     * @throws MongoException
     * @throws UnknownHostException
     */
    public long count(String collection, Map upobj){
        DBCollection coll = m_MongoDB.getCollection(collection);
        return coll.count( map2DBObject(upobj) );
    }

    /**
     * delete
     *
     * @param collection
     * @param data
     * @throws MongoException
     * @throws UnknownHostException
     */
    public void delete(String collection, Map data){
        DBCollection coll = m_MongoDB.getCollection(collection);
        BasicDBObject request = new BasicDBObject("_id", map2DBObject(data).get("_id"));

        WriteResult w = coll.remove( request );
        System.out.println(w);
    }

    /**
     * drop
     *
     * @param collection
     * @param data
     * @throws MongoException
     * @throws UnknownHostException
     */
    public void drop(String collection){
        DBCollection coll = m_MongoDB.getCollection(collection);
        coll.drop();
    }

    /**
     * read
     *
     * @param collection
     * @param data
     * @return
     * @throws MongoException
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> read(String collection, Map data){
        if( data == null ) data = new HashMap();
        DBCollection coll = m_MongoDB.getCollection(collection);
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

        DBCursor cur = coll.find( map2DBObject(data) );
        for( DBObject buf : cur.toArray().toArray(new DBObject[]{})){
            ret.add(map4DBObject(buf));
        }

        return ret;
    }

    /**
     * HashMapをDBObjectに変換する
     *
     * ※参考
     * http://bsonspec.org/
     * https://github.com/mongodb/mongo-java-driver/blob/master/src/main/com/mongodb/RawDBObject.java
     * Object getObject()
     *
     * @param map
     * @return
     */
    private static DBObject map2DBObject(Map<String, Object> map){
        DBObject ret = new BasicDBObject ();
        if(map == null)return ret;

        for(String key:(String[])map.keySet().toArray(new String[]{})){
            if (key.equals("_id") || key.endsWith("._id")) {
                ret.put(key, getObjectId((map.get(key) == null)?"":map.get(key).toString()) );
            } else if (map.get(key) instanceof Date){
                ret.put(key, (Date)map.get(key));
            } else if (map.get(key) instanceof Boolean){
                ret.put(key, (Boolean)map.get(key));
            } else if (map.get(key) instanceof Map){
                ret.put(key, map2DBObject((Map)map.get(key)));
            }else{
                ret.put(key, map.get(key));
            }
        }
        return ret;
    }

    /**
     * DBObjecをHashMapに変換する
     *
     * @param map
     * @return
     */
    private static Map<String, Object> map4DBObject(DBObject obj){
        Map<String, Object> ret = new HashMap<String, Object>();
        if(obj == null)return ret;

        for(Object o:obj.keySet().toArray()){
            String key = (String)o;
            if (key.equals("_id") || key.endsWith("._id")) {
                ret.put(key, (obj.get(key) == null)?"":obj.get(key).toString() );
            }else if(obj.get(key) instanceof BasicDBList) {
                BasicDBList list = (BasicDBList)obj.get(key);
                Object[] array = new Object[list.size()];
                for(int i = 0 ; i < list.size() ; i++){
                    if(list.get(i) instanceof DBObject){
                        array[i] = map4DBObject((DBObject)list.get(i));
                    }else{
                        array[i] = list.get(i);
                    }
                }
                ret.put(key, array);
            }else if(obj.get(key) instanceof DBObject) {
                ret.put(key, map4DBObject((DBObject)obj.get(key)));
            }else{
                ret.put(key, obj.get(key));
            }
        }
        return ret;
    }


    /**
     * mongoDBが認識可能な、ObjcetIDを返すユーティリティ
     * @param id
     * @return
     */
    public static Object getObjectId(String id) {
        if(id == null || id.length() == 0){
            return null;
        }else{
            return new ObjectId(id);
        }
    }

    @Override
    public void finalize() throws Throwable{
        if(m_Mongo != null){
            m_Mongo.close();
        }
        super.finalize();
    }
}
