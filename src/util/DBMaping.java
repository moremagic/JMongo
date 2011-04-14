/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mitsu
 */
public class DBMaping {

    public static void main(String[] args){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("テストキー1", "テストばりゅー1");
        /*
        map.put("テストキー2", "テストばりゅー2");
        map.put("テストキー3", "テストばりゅー3");
        map.put("テストキー4", "テストばりゅー4");
        map.put("intテスト", 123);
        map.put("intテスト", 99999);
        map.put("-intテスト", 99999);
        map.put("doubleテスト", 99.999);
        map.put("-doubleテスト", -99.999);*/
        {
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("テスト1", "テスト11");
            map.put("map1", map1);
        }
        map.put("boolean T", true);
        map.put("boolean F", false);
        map.put("null", null);
        map.put("Array", new Object[]{true, false, null, "あああ", 123});


        DBMaping obj = new DBMaping(map);

        System.out.println(obj.toString());
        System.out.println(new DBMaping(obj.toString()).toString());
    }

    private Map<String, Object> m_map = null;
    public DBMaping(String sData){
        m_map = createMap(sData);
    }

    public DBMaping(Map<String, Object> map){
        m_map = map;
    }

    public DBMaping(DBMaping obj){
        m_map = obj.toMap();
    }

    public Map<String, Object> toMap(){
        return m_map;
    }


    private static Map<String, Object> createMap(String sData){
        Map<String, Object> ret = new HashMap<String, Object>();

        String key = null;
        int ibuf = 1;
        char[] cArray = sData.toCharArray();
        for(int i = 1 ; i < cArray.length ; i++){
            if(cArray[i] == '{'){
                String ss = sData.substring(i, sData.indexOf("}", i+1)+1);
                ret.put(trimQuote(key.trim()), createMap(ss));

                i += ss.toCharArray().length;
                key = null;
            }else if(cArray[i] == '[') {
                String ss = sData.substring(i, sData.indexOf("]", i+1)+1);
                ret.put(trimQuote(key.trim()), createArray(ss));

                i += ss.toCharArray().length;
                key = null;
            }else if(cArray[i] == ':') {
                key = new String(cArray, ibuf, i-ibuf);
                ibuf = i+1;
            }else if(cArray[i] == '}' || cArray[i] == ','){
                if(key != null){
                    ret.put(trimQuote(key.trim()), trimQuote(new String(cArray, ibuf, i - ibuf).trim()));
                    ibuf = i+1;
                    key = null;
                }
            }
        }

        return ret;
    }

    private static Object[] createArray(String sData){
        List<Object> ret = new ArrayList<Object>();

        int ibuf = 1;
        char[] cArray = sData.toCharArray();
        for(int i = 1 ; i < cArray.length ; i++){
            if(cArray[i] == ','){
                ret.add( getObject(new String(cArray, i, i-ibuf)) );
                ibuf = i+1;
            }
        }
        return ret.toArray();
    }


    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        ret.append("{");
        String[] keys = m_map.keySet().toArray(new String[]{});
        for(int i = 0 ; i < keys.length ; i++){
            ret.append(getString(keys[i])).append(" : ").append(getString(m_map.get(keys[i])));
            if(i+1 != keys.length){
                ret.append(",");
            }
        }
        ret.append("}");

        return ret.toString();
    }

    /**
     * オブジェクトを正しいJSON文字列に変換する
     *
     * @param o
     * @return
     */
    private static Object getObject(String s){
        if(s.equals("null")){
            return null;
        }else if(s.equals("true") || s.equals("false")){
            return Boolean.parseBoolean(s);
        }else if( isNumber(s) ){
            return Double.parseDouble(s);
        }else{
            return s;
        }
    }

    /**
     * オブジェクトを正しいJSON文字列に変換する
     *
     * @param o
     * @return
     */
    private static String getString(Object o){
        StringBuilder ret = new StringBuilder();
        if(o == null){
            //null
            ret.append("null");
        }else if(o instanceof Boolean) {
            ret.append( Boolean.parseBoolean(o.toString()) );
        }else if(o instanceof Map) {
            //マップ
            ret.append( new DBMaping((Map)o).toString());
        }else if(o.getClass().isArray()) {
            //配列
            ret.append("[");
            Object[] ary = (Object[])o;
            for(int i = 0 ; i < ary.length ; i++){
                ret.append(getString(ary[i]));
                if(i+1 != ary.length){
                    ret.append(",");
                }
            }
            ret.append("]");
        }else{
            //文字列
            ret.append("\"").append(o.toString()).append("\"");
        }

        return ret.toString();
    }

    /**
     * ダブルクオートをトリムするメソッド
     *
     * @param s
     * @return
     */
    private static String trimQuote(String s){
        String ret = s;
        if( ret.startsWith("\"") && ret.endsWith("\"") ){
            ret = ret.substring(1, ret.length()-1);
        }

        return ret;
    }

    /**
     * 数値かどうかを判定する
     *
     * @param s
     * @return
     */
    private static boolean isNumber(String s){
        try{
            Double.parseDouble(s);
            return true;
        }catch(Exception err){
            return false;
        }
    }
}
