package org.etf.coronacity.gui.window;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * StartWindow
 *
 * This window is launcher.
 * In this window data from user is required.
 * Number of children, adults and old as well as number of homes and checkpoints.
 * Hospitals capacity is calculated in this class and must meet the requirement to be at least 1.
 * Number of residents must be greater than (.1 * numOfRes).
 *
 */
public class StartWindow extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(StartWindow.class.getName());

    private JTextField numOfChildrenTextField, numOfAdultsTextField, numOfOldTextField,
                        numOfHomesTextField, numOfCheckpointsTextField, numOfHospitalsTextField;

    private JButton submitButton;

    // size of matrix of city
    // generated here because of limiting user input
    int size;

    //
    //
    //
    private static final String TEXT_LABEL_WINDOW = "Unos podataka";
    private static final String TEXT_LABEL_RESIDENTS = "Stanovnici";
    private static final String TEXT_LABEL_OBJECTS_VEHICLES = "Objekti i vozila";
    private static final String TEXT_LABEL_CHILDREN = "Broj djece*";
    private static final String TEXT_LABEL_ADULTS = "Broj odraslih*";
    private static final String TEXT_LABEL_OLD = "Broj starih*";
    private static final String TEXT_LABEL_HOMES = "Broj kuca*";
    private static final String TEXT_LABEL_CHECKPOINTS = "Broj cekpointa*";
    private static final String TEXT_LABEL_AMBULANCES = "Broj ambulanti*";
    private static final String TEXT_LABEL_START = "Start";

    private static final String ERROR_WRONG_INPUT = "Morate unijeti sva polja.\nSva polja moraju biti brojne vrijednosti.";
    private static final String ERROR_FEW_RESIDENTS = "Broj stanovnika mora biti veci";

    private static final String TITLE = "Pocetni ekran";

    // Regex Pattern
    private static final String REGEX_NUMBER = "^\\d*$";


    public StartWindow() {

        size = Utils.getRandomFromRange(15, 30);

        Utils.createLoggerHandler(LOGGER);
        initComponents();
        setListeners();
    }


    //
    // private methods
    //


    // OPTION 1
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

        // init label
        JLabel residentsLabel = new JLabel(TEXT_LABEL_RESIDENTS);
        residentsLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel objectsVehiclesLabel = new JLabel(TEXT_LABEL_OBJECTS_VEHICLES);
        objectsVehiclesLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel windowLabel = new JLabel(TEXT_LABEL_WINDOW);
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, Constants.DIMENSION_TITLE_FONT_SIZE));


        JLabel numOfChildrenLabel = new JLabel(TEXT_LABEL_CHILDREN);
        JLabel numOfAdultsLabel = new JLabel(TEXT_LABEL_ADULTS);
        JLabel numOfOldLabel = new JLabel(TEXT_LABEL_OLD);
        JLabel numOfHomesLabel = new JLabel(TEXT_LABEL_HOMES);
        JLabel numOfCheckpointsLabel = new JLabel(TEXT_LABEL_CHECKPOINTS);
        JLabel numOfAmbulancesLabel = new JLabel(TEXT_LABEL_AMBULANCES);

        // init text fields
        numOfChildrenTextField = new JTextField();
        numOfAdultsTextField = new JTextField();
        numOfOldTextField = new JTextField();
        numOfHomesTextField = new JTextField();
        numOfCheckpointsTextField = new JTextField();
        numOfHospitalsTextField = new JTextField();

        // init button
        submitButton = new JButton(TEXT_LABEL_START);

        //

        // horizontal groups
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(residentsLabel)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(numOfChildrenLabel)
                                                    .addComponent(numOfAdultsLabel)
                                                    .addComponent(numOfOldLabel)
                                                )
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(numOfChildrenTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                                    .addComponent(numOfAdultsTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                                    .addComponent(numOfOldTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                                )
                                        )
                                )
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(objectsVehiclesLabel)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(numOfHomesLabel)
                                                    .addComponent(numOfCheckpointsLabel)
                                                    .addComponent(numOfAmbulancesLabel)
                                                )
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(numOfHomesTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                                    .addComponent(numOfCheckpointsTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                                    .addComponent(numOfHospitalsTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                                )
                                        )
                                )
                        )
                        .addComponent(submitButton, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
        );

        // vertical groups
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(residentsLabel)
                                .addComponent(objectsVehiclesLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfChildrenLabel)
                                .addComponent(numOfChildrenTextField)
                                .addComponent(numOfHomesLabel)
                                .addComponent(numOfHomesTextField)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfAdultsLabel)
                                .addComponent(numOfAdultsTextField)
                                .addComponent(numOfCheckpointsLabel)
                                .addComponent(numOfCheckpointsTextField)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfOldLabel)
                                .addComponent(numOfOldTextField)
                                .addComponent(numOfAmbulancesLabel)
                                .addComponent(numOfHospitalsTextField)
                        )
                        .addComponent(submitButton)
        );

        groupLayout.linkSize(SwingConstants.HORIZONTAL, numOfChildrenTextField, numOfAdultsTextField, numOfOldTextField,
                numOfHomesTextField, numOfCheckpointsTextField, numOfHospitalsTextField);

        setTitle(TITLE);
        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }


    /*
    // OPTION 2
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

        // init label
        JLabel residentsLabel = new JLabel(TEXT_LABEL_RESIDENTS);
        residentsLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel objectsVehiclesLabel = new JLabel(TEXT_LABEL_OBJECTS_VEHICLES);
        objectsVehiclesLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        JLabel windowLabel = new JLabel(TEXT_LABEL_WINDOW);
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 16));

        JLabel numOfChildrenLabel = new JLabel(TEXT_LABEL_CHILDREN);
        JLabel numOfAdultsLabel = new JLabel(TEXT_LABEL_ADULTS);
        JLabel numOfOldLabel = new JLabel(TEXT_LABEL_OLD);
        JLabel numOfHomesLabel = new JLabel(TEXT_LABEL_HOMES);
        JLabel numOfCheckpointsLabel = new JLabel(TEXT_LABEL_CHECKPOINTS);
        JLabel numOfAmbulancesLabel = new JLabel(TEXT_LABEL_AMBULANCES);

        // init text fields
        numOfChildrenTextField = new JTextField();
        numOfAdultsTextField = new JTextField();
        numOfOldTextField = new JTextField();
        numOfHomesTextField = new JTextField();
        numOfCheckpointsTextField = new JTextField();
        numOfAmbulancesTextField = new JTextField();

        // init button
        submitButton = new JButton(TEXT_LABEL_START);

        // horizontal groups
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(residentsLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfChildrenLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfChildrenTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfAdultsLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfAdultsTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfOldLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfOldTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                )
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(objectsVehiclesLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfHomesLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfHomesTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfCheckpointsLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfCheckpointsTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfAmbulancesLabel, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                        .addComponent(numOfAmbulancesTextField, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
                                )
                        )
                        .addComponent(submitButton, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH, Constants.DIMENSION_DEFAULT_WIDTH)
        );

        // vertical groups
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(residentsLabel)
                                .addComponent(objectsVehiclesLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfChildrenLabel)
                                .addComponent(numOfHomesLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfChildrenTextField)
                                .addComponent(numOfHomesTextField)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfAdultsLabel)
                                .addComponent(numOfCheckpointsLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfAdultsTextField)
                                .addComponent(numOfCheckpointsTextField)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfOldLabel)
                                .addComponent(numOfAmbulancesLabel)
                        )
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(numOfOldTextField)
                                .addComponent(numOfAmbulancesTextField)
                        )
                        .addComponent(submitButton)
        );


        groupLayout.linkSize(SwingConstants.HORIZONTAL, numOfChildrenTextField, numOfAdultsTextField, numOfOldTextField,
                numOfHomesTextField, numOfCheckpointsTextField, numOfAmbulancesTextField);

        setTitle(TITLE);
        pack();
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
    }

     */


    private void setListeners() {

        submitButton.addActionListener(action -> {

            int capacity = 0;

            if (checkInput())
                // show message to user that input is not valid
                JOptionPane.showMessageDialog(
                        this,
                        ERROR_WRONG_INPUT,
                        Constants.ERROR_MESSAGE,
                        JOptionPane.ERROR_MESSAGE
                );

            else if (checkResidentsNumber())
                // show message to user that input is not valid
                JOptionPane.showMessageDialog(
                        this,
                        ERROR_FEW_RESIDENTS,
                        Constants.ERROR_MESSAGE,
                        JOptionPane.ERROR_MESSAGE
                );

            else {

                // grab data
                HashMap<String, Integer> data = grabData();

                // proceed to next frame
                // startWindowListener.onStartPressed(data);
                new CoronaMainWindow(data);
                dispose();
            }

        });
    }


    private boolean checkInput() {

        // check if input is number

        return
                numOfChildrenTextField.getText().isEmpty() || !Pattern.matches(REGEX_NUMBER, numOfChildrenTextField.getText()) ||
                        numOfAdultsTextField.getText().isEmpty() || !Pattern.matches(REGEX_NUMBER, numOfAdultsTextField.getText()) ||
                        numOfOldTextField.getText().isEmpty() || !Pattern.matches(REGEX_NUMBER, numOfOldTextField.getText()) ||
                        numOfHomesTextField.getText().isEmpty() || !Pattern.matches(REGEX_NUMBER, numOfHomesTextField.getText()) ||
                        numOfCheckpointsTextField.getText().isEmpty() || !Pattern.matches(REGEX_NUMBER, numOfCheckpointsTextField.getText()) ||
                        numOfHospitalsTextField.getText().isEmpty() || !Pattern.matches(REGEX_NUMBER, numOfHospitalsTextField.getText());
    }

    private boolean checkResidentsNumber() {

        int numOfChildren = Integer.parseInt(numOfChildrenTextField.getText());
        int numOfAdults = Integer.parseInt(numOfAdultsTextField.getText());
        int numOfOld = Integer.parseInt(numOfOldTextField.getText());

        return .1 * (numOfChildren + numOfAdults + numOfOld) < 1;
    }

    //
    private HashMap<String, Integer> grabData() {

        HashMap<String, Integer> data = new HashMap<>();

        int numOfChildren = Integer.parseInt(numOfChildrenTextField.getText());
        int numOfAdults = Integer.parseInt(numOfAdultsTextField.getText());
        int numOfOld = Integer.parseInt(numOfOldTextField.getText());

        int capacity = Utils.getCapacityFromResidentsNumber(numOfChildren + numOfAdults + numOfOld);

        try {

            data.put(Constants.KEY_MATRIX_SIZE, size);
            data.put(Constants.KEY_NUM_OF_CHILDREN, Integer.parseInt(numOfChildrenTextField.getText()));
            data.put(Constants.KEY_NUM_OF_ADULTS, Integer.parseInt(numOfAdultsTextField.getText()));
            data.put(Constants.KEY_NUM_OF_OLD, Integer.parseInt(numOfOldTextField.getText()));
            data.put(Constants.KEY_NUM_OF_HOMES, Integer.parseInt(numOfHomesTextField.getText()));
            data.put(Constants.KEY_NUM_OF_CHECKPOINTS, Integer.parseInt(numOfCheckpointsTextField.getText()));
            data.put(Constants.KEY_NUM_OF_AMBULANCES, Integer.parseInt(numOfHospitalsTextField.getText()));
            data.put(Constants.KEY_CAPACITY, capacity);

        } catch (Exception ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }

        return data;
    }
}