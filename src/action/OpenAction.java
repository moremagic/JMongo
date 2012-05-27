/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package action;

import config.DBConnectionBean;
import gui.DBConectDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import jp.personal.jmongo.DBConContoroller;
import jp.personal.jmongo.MainController;

/**
 * 新規DBのオープンアクション
 * @author Administrator
 */
public class OpenAction extends AbstractAction {
    public OpenAction(){
        putValue(Action.NAME , "open");
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("OpenAction call!!");

        DBConectDialog dialog = new DBConectDialog();
        dialog.setVisible(true);

        String sConfigName = dialog.getConnectionConfingName();
        DBConnectionBean config = dialog.getConnectionConfing();
        dialog.dispose();

        if(config!= null){
            
            try{
                DBConContoroller con = new DBConContoroller(sConfigName, config.getM_host(), config.getM_port(), config.getM_DBName(), config.getM_DBUser(), config.getM_DBPass());
                MainController.getInstance().showDBConect(con);
            }catch(Exception err){
                err.printStackTrace();
            }
        }
    }

}
