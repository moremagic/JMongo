/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package action;

import javax.swing.Action;
import jp.personal.jmongo.DBConContoroller;

/**
 * DB単位でのActionManagerです
 * ActionからどのActionManagerで管理されているかを取得することができます。
 *
 * @author mitsu
 */
public class DBActionManager extends AbstractActionManager{
    /**
     * アクションマネージャの参照キー
     */
    private static final String ACT_MANAGER = "ACT_MANAGER";
    
    /**
     * ActionManagerを管理するコントローラ
     */
    private DBConContoroller m_controller = null;

    /**
     * コンストラクタ
     * @param controller このActionManagerを管理するコントローラ
     */
    public DBActionManager(DBConContoroller controller){
        m_controller = controller;
    }

    @Override
    public Action getAction(String className, Object[] param){
        Action ret = super.getAction(className, param);
        ret.putValue(ACT_MANAGER, this);

        return ret;
    }

    /**
     * アクションクラスからアクションマネージャを取得する
     *
     * @param act アクションクラス
     * @return アクションを管理しているアクションマネージャ
     */
    public static DBActionManager getManager(Action act){
        return (DBActionManager)act.getValue(ACT_MANAGER);
    }

    /**
     * アクションマネージャを管理するコントローラを取得する
     *
     * @return このアクションマネージャを管理するコントローラ
     */
    public DBConContoroller getController(){
        return m_controller;
    }
}
