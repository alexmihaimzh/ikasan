package org.ikasan.dashboard.ui.mappingconfiguration.panel;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/04/2017.
 */
public class NewMappingConfigurationManyToOneSourceParamNamesPanel extends Panel
{
    private ArrayList<TextField> sourceParameterNamesTextField;
    private GridLayout layout = null;
    private VerticalLayout namesLayout = null;
    private int numSourceParameters = 0;

    public NewMappingConfigurationManyToOneSourceParamNamesPanel()
    {
        init();
    }


    private void init()
    {
        this.layout = new GridLayout(5, 6);
        this.layout.setSpacing(true);
        this.layout.setMargin(true);
        this.layout.setWidth("100%");

        this.namesLayout = new VerticalLayout();
        this.namesLayout.setSpacing(true);

        this.addStyleName(ValoTheme.PANEL_BORDERLESS);

        Label mappingConfigurationLabel = new Label("Source parameter names");
        mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
        this.layout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

        this.layout.addComponent(this.namesLayout, 0, 1);

        this.setContent(layout);

        this.setSizeFull();
    }

    public void enter(int numSourceParameters)
    {
        if(numSourceParameters != this.numSourceParameters)
        {
            this.numSourceParameters = numSourceParameters;

            this.sourceParameterNamesTextField = new ArrayList<TextField>();

            namesLayout.removeAllComponents();

            for (int i = 0; i < this.numSourceParameters; i++)
            {
                TextField nameField = new TextField();
                nameField.setCaption("Parameter Name " + (i+1));
                nameField.setWidth(150, Unit.PIXELS);
                nameField.removeAllValidators();
                nameField.addValidator(new StringLengthValidator("You must provide a parameter name!",1 , null, false));
                nameField.setValidationVisible(false);

                this.sourceParameterNamesTextField.add(nameField);

                namesLayout.addComponent(nameField);
            }
        }
    }

    public boolean isValid()
    {
        Set<String> setString = new HashSet<String>();

        try
        {
            for(TextField tf: this.sourceParameterNamesTextField)
            {
                setString.add(tf.getValue());
                tf.validate();
            }
        }
        catch (Validator.InvalidValueException e)
        {
            for(TextField tf: this.sourceParameterNamesTextField)
            {
                tf.setValidationVisible(true);
            }

            return false;
        }

        if(this.sourceParameterNamesTextField.size() != setString.size())
        {
            Notification.show("Parameter names must be unique!", Notification.Type.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
