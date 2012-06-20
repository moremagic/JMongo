/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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



    public DBConnection(String host, int port, String db_name, String dbuser, String dbpasswd) throws UnknownHostException {
        this(host, port, db_name);
        if( dbuser != null && dbuser.length() > 0 && !m_MongoDB.authenticate(dbuser, dbpasswd.toCharArray()) ){
            throw new UnknownHostException(host + " ユーザ認証エラー");
        }
    }
    
    public DBConnection(String host, int port, String db_name) throws UnknownHostException {
        m_host = host;
        m_port = port;
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
        return (upobj == null)?coll.count():coll.count(map2DBObject(upobj));
    }
    
    /**
     * count
     * 
     * @param collection
     * @return 
     */
    public long count(String collection){
        return count(collection, null);
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
     * MongoDBでの特殊な検索をサポートするメソッド
     * ※DBCursorの初期値を使用し、オーバーロードもとのメソッドに処理を委譲する
     *   https://github.com/mongodb/mongo-java-driver/blob/master/src/main/com/mongodb/DBCursor.java
     *
     * @param collection
     * @param data
     * @return
     * @throws MongoException
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> selectRead(String collection, Map fields, Map query) {
        return selectRead(collection, fields, query, 0, 0, null);
    }

    /**
     * select_read ソート版
     *
     * @param collection
     * @param data
     * @return
     * @throws MongoException
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> selectRead(String collection, Map fields, Map query, Map sort) {
        return selectRead(collection, fields, query, 0, 0, sort);
    }

    /**
     * select_read_高機能版
     *
     * @param collection
     * @param data
     * @return
     * @throws MongoException
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> selectRead(String collection, Map fields, Map query, int skip, int limit, Map sort) {
        try {
            if (query == null) {
                query = new HashMap();
            }
            DBCollection coll = m_MongoDB.getCollection(collection);
            List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
            //filedsとsortをBasicDBObjectに直接変換している理由はfieldsに_idなどの特殊カラムが指定された場合、
            //map2DBObjcectの変換エラーが発生する為。また、filedsはvalueを変換する必要はない。
            BasicDBObject boFileds = null;
            BasicDBObject boSort = null;
            if (fields != null) {
                boFileds = new BasicDBObject(fields);
            }
            if (sort != null) {
                boSort = new BasicDBObject(sort);
                try{
                    createIndex(coll, sort);
                }catch(Exception e){
                    //sort項目のインデクス作成時のException
                    //e.printStackTrace();
                }
            }
            DBCursor cur = coll.find(
                    map2DBObject(query),
                    (DBObject) boFileds).skip(skip)//Skip
                    .limit(limit)//Limit
                    .sort((DBObject) boSort); //Sort
            for (DBObject buf : cur.toArray().toArray(new DBObject[]{})) {
                ret.add(map4DBObject(buf));
            }
            return ret;
        } catch (MongoException err) {
            err.printStackTrace();
            return null;
        }
    }
    
    
    
    //***********<<　GridFS　>>*************
    
    /**
     * バケットリストを返却する
     * 
     * @return 
     */
    public String[] getBucketList(){
        List<String> ret = new ArrayList<String>();
        for(String col : m_MongoDB.getCollectionNames()){
            if(col.endsWith(".chunks")){
                ret.add(col.substring(0, col.indexOf(".chunks")));
            }
        }
        
        return ret.toArray(new String[]{});
    }

    /**
     * ファイルリストを取得する
     * 
     * @param backet
     * @return DB格納状態のファイルドキュメント。JSON形式
     */
    public String[] listFile(String bucket, Map query){
        List<String> ret = new ArrayList<String>();
        
        GridFS gfs = new GridFS(m_MongoDB, bucket);        
	DBCursor cursor = gfs.getFileList(map2DBObject(query));
	while (cursor.hasNext()) {
            ret.add(cursor.next().toString());
	}
        return ret.toArray(new String[0]);
    }
    
    /**
     * 指定したバケットにファイルをセーブします
     * 
     * @param bucket
     * @param f
     * @return 
     */
    public boolean saveFile(String bucket, File f){
        boolean ret = false;
        
        GridFS gfs = new GridFS(m_MongoDB, bucket);
        GridFSDBFile gFile = gfs.findOne(f.getName());
        if(gFile == null){
            GridFSInputFile gfsin = null;
            try {
                gfsin = gfs.createFile(f);
                gfsin.save();
                ret = true;
            } catch (IOException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }
        
        return ret;
    }
    
    /**
     * 指定したバケットに存在するファイルを読み込みます
     * 
     * @param bucket
     * @param fileName
     * @return 
     */
    public InputStream readFile(String bucket, String fileName){
        GridFS gfs = new GridFS(m_MongoDB, bucket);
        GridFSDBFile gFile = gfs.findOne(fileName);
        return gFile.getInputStream();                
    }
    
    /**
     * 指定したバケットのファイルを削除します
     * 
     * @param bucket
     * @param fileName
     * @return 
     */
    public void deleteFile(String bucket, String fileName){
        GridFS gfs = new GridFS(m_MongoDB, bucket);
        gfs.remove(fileName);
    }
    
    /**
     * 与えられたMapのKeyに対しインデックスを作成します。
     * @param coll
     * @param mapIndexKey
     */
    private void createIndex(DBCollection coll, Map mapIndexKey) {
        //Bugfix sort項目にはインデックスをはる。
        BasicDBObject index = new BasicDBObject(mapIndexKey);
        Set<Map.Entry<String, Object>> entrySet = index.entrySet();
        for (Map.Entry<String, Object> ent : entrySet) {
            //値の内容を１に統一する。
            index.put(ent.getKey(), 1);
        }
        coll.ensureIndex(index);
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
