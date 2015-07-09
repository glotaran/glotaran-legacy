/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.comparetracesfx.fxcomponents;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Andras
 */
public class CompareTracesCseneController implements Initializable {
    @FXML
    private Button tbClear;
    @FXML
    private AnchorPane graphPaneMain;
    @FXML
    private Button tbExportTraces;
    @FXML
    private Button tbOverlayTracess;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void tbMouseExited(MouseEvent event) {
        Object src = event.getSource();
        if (src instanceof Button){
            ((Button)src).setEffect(null);
        }
    }

    @FXML
    private void tbMouseEntered(MouseEvent event) {
        DropShadow shadow = new DropShadow();
        Object src = event.getSource();
        if (src instanceof Button){
            ((Button)src).setEffect(shadow);
        }
    }

    @FXML
    private void tbClearAction(ActionEvent event) {
    }
    
   
}
