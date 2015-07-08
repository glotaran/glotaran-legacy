/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.comparetracesfx.fxcomponents;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Sergey
 */
public class CompareTracesCseneController implements Initializable {

    private RotateTransition rt;
    @FXML
    private AnchorPane pnScene;
    @FXML
    private Label lbHello;
    @FXML
    private Text txTestText;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rt = new RotateTransition(Duration.millis(3000), lbHello);
        rt.setToAngle(180);
        rt.setFromAngle(0);
        rt.setAutoReverse(true);
        rt.setCycleCount(4);
    }  
    
    @FXML
    private void snMouseClick(MouseEvent evt){
        rt.play();
        
    }
    
}
