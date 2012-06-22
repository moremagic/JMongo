/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmongo;

import jmongo.action.DBActionManager;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import jmongo.dao.DBConnection;
import jmongo.gui.GridFsFrame;
import jmongo.gui.MongoCollectionFrame;
import java.io.*;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DB接続単位でのコントローラクラス 以下情報を一元管理します
 *
 * ・GUI【JInternalFrame】 ・DAO【DBConnection】 ・Action【DBActionManager】
 *
 * @author mitsu
 */
public class DBConContoroller {

    private String m_confName = "";
    private MongoCollectionFrame m_gui = null;//コレクション表示フレーム
    private GridFsFrame m_gui_fs = null;//GridFS表示フレーム
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
    public DBConContoroller(String confName, String host, int port, String dbName, String dbuser, String dbpasswd) throws UnknownHostException {
        m_dao = new DBConnection(host, port, dbName, dbuser, dbpasswd);
        m_confName = confName;
    }

    /**
     * GUIを返却する
     *
     * @return
     */
    public MongoCollectionFrame getGUI() {
        if (m_gui == null) {
            m_gui = new MongoCollectionFrame(this);
            m_gui.setTitle("Collection Viewer [" + m_confName + "]");
        }
        return m_gui;
    }

    /**
     * GridFS GUIを返却する
     *
     * @return
     */
    public GridFsFrame getGridFSGUI() {
        if (m_gui_fs == null) {
            m_gui_fs = new GridFsFrame(this);
            m_gui_fs.setTitle("GridFS Viewer [" + m_confName + "]");
        }
        return m_gui_fs;
    }

    /**
     * Actionマネージャを返却する
     *
     * @return
     */
    public DBActionManager getActionMgr() {
        if (m_act == null) {
            m_act = new DBActionManager(this);
        }
        return m_act;
    }

    /**
     * DB名の取得
     *
     * @return DB名
     */
    public String getDBName() {
        return m_dao.getDBName();
    }

    /**
     * 接続ホスト情報の取得
     *
     * @return
     */
    public String getHost() {
        return m_dao.getDBHost() + ":" + m_dao.getDBPort();
    }

    /**
     * コレクション名を取得する
     *
     * @return コレクション名
     * @throws UnknownHostException
     */
    public String[] getCollections() {
        return m_dao.getCollectionNames();
    }

    /**
     * Bucketコレクション名を取得する
     *
     * @return Bucketコレクション名
     * @throws UnknownHostException
     */
    public String[] getBucketCollections() {
        return m_dao.getBucketList();
    }

    /**
     * Bucketのファイルリストを表示する
     *
     * @return Bucketコレクション名
     * @throws UnknownHostException
     */
    public String[] getBucketFiles(String bucketName) {
        return getBucketFiles(bucketName, null);
    }

    /**
     * Bucketのファイルリストを表示する
     *
     * @return Bucketコレクション名
     * @throws UnknownHostException
     */
    public String[] getBucketFiles(String bucketName, Map query) {
        return m_dao.listFile(bucketName, query);
    }

    /**
     * 指定したバケットにファイルを保存する
     *
     * @param bucketName バケット名
     * @param f 保存するファイル
     * @return
     */
    public boolean saveFile(String bucketName, File f) {
        return m_dao.saveFile(bucketName, f);
    }

    /**
     * 指定したバケットのファイルを削除する
     *
     * @param bucketName バケット名
     * @param f 保存するファイル
     * @return
     */
    public void deleteFile(String bucketName, String fname) {
        m_dao.deleteFile(bucketName, fname);
    }

    /**
     * 指定したバケットのファイルを保存する
     *
     * @param bucketName
     * @param fname
     * @param f 保存先ファイル名
     * @return
     */
    public boolean loadFile(String bucketName, String fname, File f) {
        boolean ret = false;
        if (!f.exists()) {
            try {//ファイル出力
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new BufferedInputStream(m_dao.readFile(bucketName, fname));
                    out = new BufferedOutputStream(new FileOutputStream(f));
                    int cnt = 0;
                    byte[] buf = new byte[1024];
                    while ((cnt = in.read(buf, 0, buf.length)) != -1) {
                        out.write(buf, 0, cnt);
                    }
                    ret = true;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * データを取得する
     *
     * @return コレクション名
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> findCollection(String collection, Map query, int skip, int limit) throws MongoException {
        return m_dao.selectRead(collection, null, query, skip, limit, null);
    }

    /**
     * コレクションの全体サイズを取得する
     *
     * @param collection
     * @return
     */
    public long getCollectionDataCount(String collection) {
        return m_dao.count(collection, null);
    }

    /**
     * アップデートを行う
     *
     * @return 成功したらTrue
     */
    public boolean update() {
        try {
            String collection = m_gui.getCollectionNames()[0];
            String newData = m_gui.getEditData();
            String selectData = m_gui.getSelectedData();
            if (selectData == null) {
                return insert(collection, newData);
            } else {
                Map<String, Object> find = new HashMap<String, Object>();
                find.put("_id", ((Map) JSON.parse(selectData)).get("_id"));

                Map<String, Object> update = (Map) JSON.parse(newData);
                m_dao.update(collection, find, update);
                return true;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean insert(String collection, String data) {
        try {
            m_dao.insert(collection, (Map) JSON.parse(data));
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public boolean delete() {
        try {
            String collection = m_gui.getCollectionNames()[0];
            String newData = m_gui.getEditData();

            m_dao.delete(collection, (Map) JSON.parse(newData));
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * 指定されたコレクションをドロップします
     *
     * @return
     */
    public boolean drop() {
        boolean ret = false;
        try {
            for (String collection : m_gui.getCollectionNames()) {
                m_dao.drop(collection);
            }
            ret = true;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return ret;
    }

    /**
     * DBデータをファイルへダンプします
     *
     * @param f
     * @return
     */
    public boolean dump(File fdir) {
        try {
            BufferedWriter bw = null;
            for (String collection : m_gui.getCollectionNames()) {
                try {
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fdir, collection)), "UTF-8"));

                    List<Map<String, Object>> list = m_dao.read(collection, null);
                    for (Map m : list) {
                        bw.write(JSON.serialize(m) + "\r\n");
                    }
                } finally {
                    if (bw != null) {
                        bw.close();
                    }
                }
            }
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * 指定したファイルからDBへデータをロードします
     *
     * @param f
     * @return
     */
    public boolean load(File f) {
        try {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

                String line = "";
                while ((line = br.readLine()) != null) {
                    Map<String, Object> m = (Map<String, Object>) JSON.parse(line);
                    m_dao.insert(f.getName(), m);
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    /**
     * DBコントローラをクローズする
     *
     * ※メモリリーク対策
     */
    public void close() {
        m_gui = null;
        m_dao = null;
        m_act = null;
    }
}
