/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package action;

/**
 * アプリケーションで唯一のアクションクラスを
 * このクラスで一元管理します
 *
 * @author Administrator
 */
public class ActionManager extends AbstractActionManager{
    private static ActionManager m_instance = null;
    
    public static ActionManager getInstance(){
        if(m_instance == null){
            m_instance = new ActionManager();
        }
        return m_instance;
    }

    private ActionManager(){
    }
}
