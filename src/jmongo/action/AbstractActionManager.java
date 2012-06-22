/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmongo.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

/**
 * アクション管理を行うためのAbstractクラスです。
 *
 *
 * @author mitsu
 */
public abstract class AbstractActionManager {
    private Map<String, Action> m_ActMap = new HashMap<String, Action>();

    /**
     * 一元管理されたアクションを取得します
     *
     * @param className クラスの完全名
     * @return Actionクラス
     */
    public Action getAction(String className){
        return getAction(className, new Class[]{});
    }

    /**
     * 一元管理されたアクションを取得します
     *
     * @param className クラスの完全名
     * @param param コンストラクタパラメータ
     * @return Actionクラス
     */
    public Action getAction(String className, Object[] param){
        if( !m_ActMap.containsKey(className) ){
            try {
                ClassLoader loader = getClass().getClassLoader();
                Class clazz = loader.loadClass(className);
                Class[] types = new Class[param.length];
                for (int i = 0; i < param.length; i++) {
                    types[i] = param[i].getClass();
                }
                Constructor con = clazz.getDeclaredConstructor(types);
                con.setAccessible(true);
                con.newInstance(param);
                m_ActMap.put(className, (Action) con.newInstance(param));
            } catch (InstantiationException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return m_ActMap.get(className);
    }

}
