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
public class GFSOpenAction extends AbstractAction {
    public GFSOpenAction(){
        putValue(Action.NAME , "gfs open");
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("GridFS DBOpenAction call!!");

        DBConectDialog dialog = new DBConectDialog();
        dialog.setVisible(true);

        String sConfigName = dialog.getConnectionConfingName();
        DBConnectionBean config = dialog.getConnectionConfing();
        dialog.dispose();

        if(config!= null){
            
            try{
                DBConContoroller con = new DBConContoroller(sConfigName, config.getM_host(), config.getM_port(), config.getM_DBName(), config.getM_DBUser(), config.getM_DBPass());
                MainController.getInstance().showGFSDBConect(con);
            }catch(Exception err){
                err.printStackTrace();
            }
        }
    }

}
