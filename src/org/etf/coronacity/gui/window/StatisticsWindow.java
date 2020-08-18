package org.etf.coronacity.gui.window;

import org.etf.coronacity.gui.model.DataTableModel;
import org.etf.coronacity.gui.tab.AgeGraphTab;
import org.etf.coronacity.gui.tab.GenderGraphTab;
import org.etf.coronacity.gui.tab.HospitalTab;
import org.etf.coronacity.gui.tab.TableTab;
import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.TableNames;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.carrier.Data;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.person.Person;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StatisticsWindow extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(StatisticsWindow.class.getName());

    private AppData appData;

    private Data data;

    private JTabbedPane containerTabbedPane;
    private JButton downloadCsvButton;
    private TableTab infectedTab, currentlyInfected, recoveredTab;

    private static final String TEXT_LABEL_WINDOW = "Statistika";

    private static final String BUTTON_NAME_DOWNLOAD_CSV = "Preuzmi CSV";

    private static final String TITLE = "Statisticki podaci";

    // tabs title
    private static final String TAB_INFECTED_TITLE = "Zarazeni";
    private static final String TAB_CURRENTLY_INFECTED_TITLE = "Trenutno zarazeni";
    private static final String TAB_RECOVERED_TITLE = "Oporavljeni";
    private static final String TAB_GENDER_INFECTED_TITLE = "Zarazeni po polu";
    private static final String TAB_GENDER_RECOVERED_TITLE = "Oporavljeni po polu";
    private static final String TAB_AGE_INFECTED_TITLE = "Zarazeni po starosti";
    private static final String TAB_AGE_RECOVERED_TITLE = "Oporavljeni po starosti";
    private static final String TAB_HOSPITALS_TITLE = "Bolnice";

    private static final String TEXT_GENDER_MALE = "Muski";
    private static final String TEXT_GENDER_FEMALE = "Zenski";

    public StatisticsWindow(AppData appData) {

        this.appData = appData;

        Utils.createLoggerHandler(LOGGER);

        readFileData();
        initComponents();
        setListeners();
    }


    //
    // private
    //
    private void readFileData() {

        try {

            ObjectInputStream inputStream =
                    new ObjectInputStream(new FileInputStream(
                            new File(Constants.FILE_PATH_FIRST_AID_DATA + Constants.DATA_FILE_NAME)
                    ));

            data = (Data) inputStream.readObject();

            inputStream.close();

        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }

    }

    private void initComponents() {

        // using GroupLayout
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        setLayout(groupLayout);

        // set auto gaps
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        //
        // init components
        //

        JLabel windowLabel = new JLabel(TEXT_LABEL_WINDOW);
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, Constants.DIMENSION_TITLE_FONT_SIZE));

        containerTabbedPane = new JTabbedPane();

        downloadCsvButton = new JButton(BUTTON_NAME_DOWNLOAD_CSV);

        setTabs();

        // horizontal tabs
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addComponent(containerTabbedPane)
                        .addComponent(downloadCsvButton)
        );

        // vertical group
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addComponent(containerTabbedPane)
                        .addComponent(downloadCsvButton)
        );


        setTitle(TITLE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        // setResizable(false);
        setLocationRelativeTo(null);
    }

    private void setListeners() {

        downloadCsvButton.addActionListener(e -> {

            // download data as csv file
            infectedTab.exportToCsv(Constants.STATISTICS_FILE_INFECTED_CSV);
            currentlyInfected.exportToCsv(Constants.STATISTICS_FILE_CURRENTLY_INFECTED_CSV);
            recoveredTab.exportToCsv(Constants.STATISTICS_FILE_RECOVERED_CSV);
        });
    }

    private void setTabs() {

        // first tab
        infectedTab = createInfectedTableTab();

        // currently infected
        currentlyInfected = createCurrentlyInfectedTab();

        // second tab
        recoveredTab = createRecoveredTableTab();

        // third tab
        GenderGraphTab genderInfectedGraphTab = createGenderGraphTab(true);
        GenderGraphTab genderRecoveredGraphTab = createGenderGraphTab(false);

        AgeGraphTab ageInfectedGraphTab = createAgeGraph(true);
        AgeGraphTab ageRecoveredGraphTab = createAgeGraph(false);

        HospitalTab hospitalTab = new HospitalTab(appData.getHospitals());

        // forth tab
        //AgeGraphTab ageGraphTab = createAgeGraphTab();


        containerTabbedPane.addTab(TAB_INFECTED_TITLE, infectedTab);
        containerTabbedPane.addTab(TAB_CURRENTLY_INFECTED_TITLE, currentlyInfected);
        containerTabbedPane.addTab(TAB_RECOVERED_TITLE, recoveredTab);
        containerTabbedPane.addTab(TAB_GENDER_INFECTED_TITLE, genderInfectedGraphTab);
        containerTabbedPane.addTab(TAB_GENDER_RECOVERED_TITLE, genderRecoveredGraphTab);
        containerTabbedPane.addTab(TAB_AGE_INFECTED_TITLE, ageInfectedGraphTab);
        containerTabbedPane.addTab(TAB_AGE_RECOVERED_TITLE, ageRecoveredGraphTab);
        containerTabbedPane.addTab(TAB_HOSPITALS_TITLE, hospitalTab);
    }


    private TableTab createInfectedTableTab() {

        ArrayList<Person> persons = (ArrayList<Person>)
                appData.getPersons().values()
                        .stream()
                        .filter(p ->
                                data.getInfectedIds().stream()
                                        .anyMatch(id -> p.getId() == id)
                        )
                        .collect(Collectors.toList());

        Object[][] data = new Object[persons.size()][];
        int index = 0;

        for (Person person : persons)
            data[index++] = createRow(index, person);

        String[] columnNames = new String[DataTableModel.INFECTED_COLUMNS];

        columnNames[0] = TableNames.COLUMN_NUM;
        columnNames[1] = TableNames.COLUMN_ID;
        columnNames[2] = TableNames.COLUMN_NAME;
        columnNames[3] = TableNames.COLUMN_SURNAME;
        columnNames[4] = TableNames.COLUMN_BIRTH_YEAR;
        columnNames[5] = TableNames.COLUMN_GENDER;
        columnNames[6] = TableNames.COLUMN_BODY_TEMPERATURE;
        columnNames[7] = TableNames.COLUMN_HOME_LOCATION;
        columnNames[8] = TableNames.COLUMN_NUMBER_OF_HOUSEHOLD;

        return new TableTab(new DataTableModel(data, columnNames));
    }

    private TableTab createCurrentlyInfectedTab() {

        ArrayList<Person> persons =
                appData.getPersons().values()
                        .stream()
                        .filter(Person::isInfected)
                        .collect(Collectors.toCollection(ArrayList::new));

        Object[][] data = new Object[persons.size()][];
        int index = 0;

        for (Person person : persons)
            data[index++] = createRow(index, person);

        String[] columnNames = new String[DataTableModel.INFECTED_COLUMNS];

        columnNames[0] = TableNames.COLUMN_NUM;
        columnNames[1] = TableNames.COLUMN_ID;
        columnNames[2] = TableNames.COLUMN_NAME;
        columnNames[3] = TableNames.COLUMN_SURNAME;
        columnNames[4] = TableNames.COLUMN_BIRTH_YEAR;
        columnNames[5] = TableNames.COLUMN_GENDER;
        columnNames[6] = TableNames.COLUMN_BODY_TEMPERATURE;
        columnNames[7] = TableNames.COLUMN_HOME_LOCATION;
        columnNames[8] = TableNames.COLUMN_NUMBER_OF_HOUSEHOLD;

        return new TableTab(new DataTableModel(data, columnNames));
    }

    private TableTab createRecoveredTableTab() {

        ArrayList<Person> persons = (ArrayList<Person>)
                appData.getPersons().values()
                        .stream()
                        .filter(p ->
                                data.getRecoveredIds().stream()
                                        .anyMatch(id -> p.getId() == id)
                        )
                        .collect(Collectors.toList());

        Object[][] data = new Object[persons.size()][];
        int index = 0;

        for (Person person : persons)
            data[index++] = createRow(index, person);

        String[] columnNames = new String[DataTableModel.INFECTED_COLUMNS];

        columnNames[0] = TableNames.COLUMN_NUM;
        columnNames[1] = TableNames.COLUMN_ID;
        columnNames[2] = TableNames.COLUMN_NAME;
        columnNames[3] = TableNames.COLUMN_SURNAME;
        columnNames[4] = TableNames.COLUMN_BIRTH_YEAR;
        columnNames[5] = TableNames.COLUMN_GENDER;
        columnNames[6] = TableNames.COLUMN_BODY_TEMPERATURE;
        columnNames[7] = TableNames.COLUMN_HOME_LOCATION;
        columnNames[8] = TableNames.COLUMN_NUMBER_OF_HOUSEHOLD;

        return new TableTab(new DataTableModel(data, columnNames));
    }

    private GenderGraphTab createGenderGraphTab(boolean infected) {

        ArrayList<Long> keys = new ArrayList<>(data.getMeasurementAtTime().keySet());

        Collections.sort(keys);

        long prevMale = 0, prevFemale = 0;

        if (infected) {

            ArrayList<Long> infectedMale = new ArrayList<>();
            ArrayList<Long> infectedFemale = new ArrayList<>();

            for (long key : keys) {

                long infMale = prevMale + data.getMeasurementAtTime().get(key).getInfectedMale();
                long infFemale = prevFemale + data.getMeasurementAtTime().get(key).getInfectedFemale();

                infectedMale.add(infMale);
                infectedFemale.add(infFemale);

                prevMale = infMale;
                prevFemale = infFemale;
            }

            return new GenderGraphTab(keys, infectedMale, infectedFemale, infected);

        } else {

            ArrayList<Long> recoveredMale = new ArrayList<>();
            ArrayList<Long> recoveredFemale = new ArrayList<>();

            for (long key : keys) {

                long recMale = prevMale + data.getMeasurementAtTime().get(key).getRecoveredMale();
                long recFemale = prevFemale + data.getMeasurementAtTime().get(key).getRecoveredFemale();

                recoveredMale.add(recMale);
                recoveredFemale.add(recFemale);

                prevMale = recMale;
                prevFemale = recFemale;
            }

            return new GenderGraphTab(keys, recoveredMale, recoveredFemale, infected);
        }
    }

    private AgeGraphTab createAgeGraph(boolean infected) {

        ArrayList<Long> keys = new ArrayList<>(data.getMeasurementAtTime().keySet());

        Collections.sort(keys);

        long prevChild = 0, prevAdult = 0, prevOld = 0;

        if (infected) {

            ArrayList<Long> infectedChild = new ArrayList<>();
            ArrayList<Long> infectedAdult = new ArrayList<>();
            ArrayList<Long> infectedOld = new ArrayList<>();

            for (long key : keys) {

                long infChild = prevChild + data.getMeasurementAtTime().get(key).getInfectedChild();
                long infAdult = prevAdult + data.getMeasurementAtTime().get(key).getInfectedAdult();
                long infOld = prevOld + data.getMeasurementAtTime().get(key).getInfectedOld();

                infectedChild.add(infChild);
                infectedAdult.add(infAdult);
                infectedOld.add(infOld);

                prevChild = infChild;
                prevAdult = infAdult;
                prevOld = infOld;
            }

            return new AgeGraphTab(keys, infectedChild, infectedAdult, infectedOld, true);

        } else {

            ArrayList<Long> recoveredChild = new ArrayList<>();
            ArrayList<Long> recoveredAdult = new ArrayList<>();
            ArrayList<Long> recoveredOld = new ArrayList<>();

            for (long key : keys) {

                long recChild = prevChild + data.getMeasurementAtTime().get(key).getRecoveredChild();
                long recAdult = prevAdult + data.getMeasurementAtTime().get(key).getRecoveredAdult();
                long recOld = prevOld + data.getMeasurementAtTime().get(key).getRecoveredOld();

                recoveredChild.add(recChild);
                recoveredAdult.add(recAdult);
                recoveredOld.add(recOld);

                prevChild = recChild;
                prevAdult = recAdult;
                prevOld = recOld;
            }

            return new AgeGraphTab(keys, recoveredChild, recoveredAdult, recoveredOld, infected);
        }
    }

    private Object[] createRow(int num, Person person) {

        Object[] row = new Object[DataTableModel.INFECTED_COLUMNS];

        row[0] = num;
        row[1] = person.getId();
        row[2] = person.getName();
        row[3] = person.getSurname();
        row[4] = person.getBirthYear();
        row[5] = person.getGender() == Person.Gender.MALE ? TEXT_GENDER_MALE : TEXT_GENDER_FEMALE;
        row[6] = String.format("%.2f", person.getBodyTemperature());
        row[7] = "(" + appData.getBuildings().get(person.getHomeId()).getPositionX() + ", " +
                appData.getBuildings().get(person.getHomeId()).getPositionY() + ")";
        row[8] = ((Home) appData.getBuildings().get(person.getHomeId())).getHosts() - 1;

        return row;
    }
}
