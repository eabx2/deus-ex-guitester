package com.me.deusexguitester.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by ersinn on 20.07.2020.
 */
public class TestInfoProperty {

    private SimpleStringProperty name;
    private SimpleStringProperty testedWindow;
    private SimpleStringProperty description;

    public TestInfoProperty(TestInfo testInfo){
        this.name = new SimpleStringProperty(testInfo.name);
        this.testedWindow = new SimpleStringProperty(testInfo.testedWindow);
        this.description = new SimpleStringProperty(testInfo.description);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getTestedWindow() {
        return testedWindow.get();
    }

    public SimpleStringProperty testedWindowProperty() {
        return testedWindow;
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

}
