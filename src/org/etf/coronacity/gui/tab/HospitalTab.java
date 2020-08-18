package org.etf.coronacity.gui.tab;

import org.etf.coronacity.gui.model.DataTableModel;
import org.etf.coronacity.helper.TableNames;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.Person;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class HospitalTab extends JPanel {

    private ArrayList<Hospital> hospitals;

    private static final String TEXT_GENDER_MALE = "Muski";
    private static final String TEXT_GENDER_FEMALE = "Zenski";

    public HospitalTab(ArrayList<Hospital> hospitals) {
        this.hospitals = hospitals;

        //
        initComponents();
    }

    //
    // private
    //
    private void initComponents() {

        setLayout(new GridLayout(1, 1));

        JTabbedPane containerTabbedPane = new JTabbedPane();

        for (Hospital hospital : hospitals) {

            Object[][] data = new Object[hospital.getInfectedCount()][];
            int index = 0;

            for (Person person : hospital.getInfected().values())
                data[index++] = createRow(index, person, hospital);

            String[] columnsNames = new String[DataTableModel.HOSPITAL_PERSON_COLUMNS];
            columnsNames[0] = TableNames.COLUMN_NUM;
            columnsNames[1] = TableNames.COLUMN_ID;
            columnsNames[2] = TableNames.COLUMN_NAME;
            columnsNames[3] = TableNames.COLUMN_SURNAME;
            columnsNames[4] = TableNames.COLUMN_BIRTH_YEAR;
            columnsNames[5] = TableNames.COLUMN_GENDER;
            columnsNames[6] = TableNames.COLUMN_BODY_TEMPERATURE;
            columnsNames[7] = TableNames.COLUMN_BODY_TEMPERATURE_LAST_THREE;
            columnsNames[8] = TableNames.COLUMN_BODY_TEMPERATURE_AVERAGE;

            TableTab tableTab = new TableTab(new DataTableModel(data, columnsNames));

            containerTabbedPane.addTab(hospital.getName(), tableTab);
        }

        add(containerTabbedPane);
    }

    private Object[] createRow(int num, Person person, Hospital hospital) {

        Object[] row = new Object[DataTableModel.HOSPITAL_PERSON_COLUMNS];

        row[0] = num;
        row[1] = person.getId();
        row[2] = person.getName();
        row[3] = person.getSurname();
        row[4] = person.getBirthYear();
        row[5] = person.getGender() == Person.Gender.MALE ? TEXT_GENDER_MALE : TEXT_GENDER_FEMALE;
        row[6] = String.format("%.2f", person.getBodyTemperature());
        row[7] = temperaturesToString(hospital.getTemperaturesList(person.getId()));
        row[8] = String.format("%.2f", Utils.average(hospital.getTemperaturesList(person.getId())));

        return row;
    }

    private String temperaturesToString(LinkedList<Double> temperatures) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Double temp : temperatures)
            stringBuilder.append(String.format("%.2f", temp)).append("; ");

        return stringBuilder.toString();
    }
}
