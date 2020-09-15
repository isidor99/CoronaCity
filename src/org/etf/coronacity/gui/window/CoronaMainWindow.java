package org.etf.coronacity.gui.window;

import org.etf.coronacity.helper.*;
import org.etf.coronacity.interfaces.*;
import org.etf.coronacity.model.*;
import org.etf.coronacity.model.building.Checkpoint;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.carrier.AmbulanceMovementData;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.carrier.Data;
import org.etf.coronacity.model.carrier.UrgentMovementData;
import org.etf.coronacity.model.person.Person;
import org.etf.coronacity.service.*;
import org.etf.coronacity.service.movement.*;
import org.etf.coronacity.service.timer.TemperatureTimerTask;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/*
 * Main Window
 * The main logic of the application is executed in this window
 */
public class CoronaMainWindow extends JFrame implements TemperatureUpdateListener, MovementListener,
        AmbulanceMovementListener {

    private static final Logger LOGGER = Logger.getLogger(CoronaMainWindow.class.getName());

    private SupervisionSystem supervisionSystem;

    private JButton[] optionButtons;
    private JButton[][] matrixButtons;
    private JPanel buttonsPanel;
    private JTextArea dataTextArea;
    private JLabel infectedLabel, recoveredLabel;

    private MovementThread movementThread;
    private MeasurementThread measurementThread;
    private Timer temperatureTimer;

    private long startTime;
    private long delayTime;

    private AppData appData;
    private Object lock;
    private ConcurrentHashMap<Long, AmbulanceMovementThread> ambulanceThreads;
    private HospitalThread hospitalThread;

    // button names
    private static final String BUTTON_NAME_DISABLE_MOVEMENT = "OMOGUCI KRETANJE";
    private static final String BUTTON_NAME_SEND_AMBULANCE = "POSALJI AMBULANTNO VOZILO";
    private static final String BUTTON_NAME_CHECK_AMBULANCES = "PREGLEDAJ STANJE AMBULANTI";
    private static final String BUTTON_NAME_CHECK_STATISTICS = "PREGLEDAJ STATISTICKE PODATKE";
    private static final String BUTTON_NAME_STOP_SIMULATION = "ZAUSTAVI SIMULACIJU";
    private static final String BUTTON_NAME_START_SIMULATION_AGAIN = "POKRENU SIMULACIJU PONOVO";
    private static final String BUTTON_NAME_END_SIMULATION = "ZAVRSI SIMULACIJU";

    private static final String TEXT_LABEL_WINDOW = "Glavni prozor";
    private static final String TEXT_LABEL_OPTIONS = "Opcije";
    private static final String TEXT_LABEL_INFECTED_COUNT = "Ukupno zarazenih:";
    private static final String TEXT_LABEL_RECOVERED_COUNT = "Ukupno oporavljenih:";

    private static final String TITLE = "JavaKov-20";

    private static final String NO_AMBULANCES_MESSAGE = "Nema slobodnih ambulantnih vozila";
    private static final String NO_HOSPITALS_MESSAGE = "Nema slobodnog mjesta niti u jednoj bolnici";
    private static final String NO_INFECTED_PERSONS_MESSAGE = "Nema zarazenih osoba";

    // test
    private int num = 1;

    // constants for generating new hospital position
    public static final short DIRECTION_ROW = 0;
    public static final short DIRECTION_COLUMN = 1;


    public CoronaMainWindow(HashMap<String, Integer> data) {

        appData = new AppData(data);
        lock = new Object();
        ambulanceThreads = new ConcurrentHashMap<>(data.get(Constants.KEY_NUM_OF_AMBULANCES) + 1, 1);

        Utils.createLoggerHandler(LOGGER);

        // init supervision system
        supervisionSystem = new SupervisionSystem();
        supervisionSystem.setAlarmListener(this::alarm);

        FileWatcherService watcherService = new FileWatcherService(lock);
        watcherService.setFileChangeListener(this::onFileChanged);
        watcherService.start();

        Utils.initDataFile();

        // temperature timer
        temperatureTimer = new Timer(true);

        // movement thread
        movementThread = new MovementThread(appData, this);

        // Measurement thread
        measurementThread = new MeasurementThread(appData, supervisionSystem);

        initComponents();
        setListeners();
    }

    /**
     * Invoked whenever temperature is changed (every 30 seconds)
     */
    @Override
    public void onTemperatureUpdated() {

        System.out.println("Temperature updated!");

        /*
         *  Error prevention method
         *  If after the temperature change in the previous cycle
         *  there were neither infected nor recovered then PersonCounter
         *  will be null and that can cause serious error.
         *  To prevent that scenario this thread needs to be invoked after every
         *  temperature update.
         */
        new FileUpdateService(lock, null, appData.getTime(), true).start();

        delayTime = 30_000;
        appData.setTempTimerTickTime(System.currentTimeMillis());
        appData.setTime(appData.getTime() + 30);

        // invoke hospital thread
        // check all infected in hospitals
        hospitalThread = new HospitalThread(appData.getHospitals());
        hospitalThread.setRecoverListener(this::onInfectedRecovered);
        hospitalThread.start();
    }

    /**
     * Invoked when person movement is preformed
     * Displays message in JTextArea
     * @param message String that needs to be displayed
     */
    @Override
    public synchronized void onMovementPerformed(String message, boolean highlight) {
        dataTextArea.append(num++ + ", " + message);
        MatrixHelper.setMatrixButtonsToolTipTexts(appData, matrixButtons);
    }

    /**
     * Invoked when ambulance movement is performed
     * Sets color of button which represents current ambulance position
     * @param posX current ambulance position x (row in matrix)
     * @param posY current ambulance position y (column in matrix)
     * @param prevX previous ambulance position x (row in matrix)
     * @param prevY previous ambulance postion y (columns in matrix)
     */
    @Override
    public void onMovementPerformed(int posX, int posY, int prevX, int prevY) {

        if (appData.getMatrix()[prevX][prevY] == null)
            matrixButtons[prevX][prevY].setBackground(null);
        else if (appData.getMatrix()[prevX][prevY] instanceof Checkpoint)
            matrixButtons[prevX][prevY].setBackground(Color.decode(Colors.COLOR_CHECKPOINT));
        else if (appData.getMatrix()[prevX][prevY] instanceof Home)
            matrixButtons[prevX][prevY].setBackground(Color.decode(Colors.COLOR_HOME));

        if (!(appData.getMatrix()[posX][posY] instanceof Hospital))
            matrixButtons[posX][posY].setBackground(Color.decode(Colors.COLOR_AMBULANCE));
    }

    //
    // private
    //

    // consumer methods

    /**
     * Invoked when file data is changed
     * File contains data about total number of infected and recovered
     * This methods updates infected and recovered labels
     */
    private void onFileChanged(Data data) {

        if (infectedLabel != null && recoveredLabel != null) {

            // update UI
            String infectedCountText = TEXT_LABEL_INFECTED_COUNT + " " + data.getInfected();
            String recoveredCountText = TEXT_LABEL_RECOVERED_COUNT + " " + data.getRecovered();

            infectedLabel.setText(infectedCountText);
            recoveredLabel.setText(recoveredCountText);
        }
    }

    /**
     * This method is passed to SupervisionSystem as consumer function
     * When infected person is detected, put Alarm on stack, stop that person
     * And notify other from the same house to go home
     */
    private void alarm(long personId) {

        Person person = appData.getPersons().get(personId);

        person.setMove(false);
        person.setInfected(true);

        notifyHousehold(person);

        // update file with data about infected
        // this file is used for statistics too
        new FileUpdateService(lock, person, appData.getTime(), true).start();
    }

    /**
     * This method is passed to HospitalThread as consumer function
     * This method is called when person in hospital gets recover
     */
    private void onInfectedRecovered(Person person) {

        person.getLocationData().setPosition(
                appData.getBuildings().get(person.getHomeId()).getPositionX(),
                appData.getBuildings().get(person.getHomeId()).getPositionY()
        );

        person.setInfected(false);

        // if there are no infected people in the house, then allow all residents to move
        // check if there is any infected from the same house as recovered person is
        boolean infected =
                appData.getPersons().values().stream()
                        .filter(p -> p.getHomeId() == person.getHomeId())
                        .anyMatch(Person::isInfected);

        // if not, enable moving of all persons from that house
        if (!infected)
            appData.getPersons().values()
                    .stream()
                    .filter(p -> p.getHomeId() == person.getHomeId())
                    .forEach(p -> p.setMove(true));

        // update file with data about recovered
        // this file is used for statistics too
        new FileUpdateService(lock, person, appData.getTime(), false).start();
    }

    /**
     * All components and layout manager are created here and set properly
     */
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

        createButtons();

        JLabel windowLabel = new JLabel(TEXT_LABEL_WINDOW);
        windowLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, Dimensions.TITLE_FONT_SIZE));

        JLabel optionsLabel = new JLabel(TEXT_LABEL_OPTIONS);
        optionsLabel.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 14));

        // Labels
        String infectedCountText = TEXT_LABEL_INFECTED_COUNT + " 0";
        String recoveredCountText = TEXT_LABEL_RECOVERED_COUNT + " 0";
        infectedLabel = new JLabel(infectedCountText);
        recoveredLabel = new JLabel(recoveredCountText);

        JPanel matrixPanel = new JPanel(new GridLayout(appData.getMatrix().length + 1, appData.getMatrix().length + 1));

        buttonsPanel = new JPanel(new GridLayout(optionButtons.length, 1, 0, 8));

        dataTextArea = new JTextArea(10, 10);
        dataTextArea.setLineWrap(true);
        dataTextArea.setFont(new Font(Constants.DEFAULT_FONT, Font.BOLD, 12));

        ((DefaultCaret) dataTextArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane dataScrollPane = new JScrollPane(
                dataTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        dataTextArea.setBorder(
                new CompoundBorder(
                        new EmptyBorder(0, 0, 0, 0),
                        new EmptyBorder(4, 4, 4, 4)
                )
        );

        matrixButtons = MatrixHelper.populateMatrixPanel(appData, matrixPanel);
        MatrixHelper.setMatrixButtonsToolTipTexts(appData, matrixButtons);
        Arrays.stream(optionButtons).forEach(button -> buttonsPanel.add(button));

        //

        // horizontal groups
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(windowLabel)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(infectedLabel)
                                        .addComponent(recoveredLabel)
                                        .addComponent(matrixPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                )
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(optionsLabel)
                                        .addGap(Dimensions.MIN_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE)
                                        .addComponent(buttonsPanel, Dimensions.MIN_BUTTON_WIDTH, Dimensions.PREFERRED_BUTTON_WIDTH, Dimensions.PREFERRED_BUTTON_WIDTH)
                                        .addGap(Dimensions.MIN_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE)
                                )
                        )
                        .addComponent(dataScrollPane)
        );

        // vertical group
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(windowLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(infectedLabel)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(optionsLabel)
                                .addComponent(recoveredLabel)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(matrixPanel)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(Dimensions.MIN_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE)
                                        .addComponent(buttonsPanel)
                                        .addGap(Dimensions.MIN_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE, Dimensions.PREFERRED_GAP_SIZE)
                                )
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dataScrollPane)
        );

        setTitle(TITLE);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // if matrix is large, then resizable is enabled
        if (appData.getMatrix().length < 24)
            setResizable(false);
    }

    /**
     * Create option buttons and store it in an array
     */
    private void createButtons() {

        optionButtons = new JButton[] {
                new JButton(BUTTON_NAME_DISABLE_MOVEMENT),
                new JButton(BUTTON_NAME_SEND_AMBULANCE),
                new JButton(BUTTON_NAME_CHECK_AMBULANCES),
                new JButton(BUTTON_NAME_CHECK_STATISTICS),
                new JButton(BUTTON_NAME_STOP_SIMULATION),
                new JButton(BUTTON_NAME_START_SIMULATION_AGAIN),
                new JButton(BUTTON_NAME_END_SIMULATION)
        };

        optionButtons[4].setEnabled(false);
        optionButtons[5].setEnabled(false);
    }

    /**
     * Add action listeners for option buttons
     */
    private void setListeners() {

        optionButtons[0].addActionListener(event -> {

            // enable moving
            enableMoving();
            optionButtons[0].setEnabled(false);
            optionButtons[4].setEnabled(true);
        });

        optionButtons[1].addActionListener(event -> {

            // send ambulance
            sendAmbulance();
        });

        optionButtons[2].addActionListener(event -> {

            // check hospitals
            openHospitalsWindow();
        });

        optionButtons[3].addActionListener(event -> {

            // check statistics
            // maybe to stop simulation here
            SwingUtilities.invokeLater(() -> new StatisticsWindow(appData));
        });

        optionButtons[4].addActionListener(event -> {

            // serialization

            serialize();
            optionButtons[1].setEnabled(false);
            optionButtons[4].setEnabled(false);
            optionButtons[5].setEnabled(true);
        });

        optionButtons[5].addActionListener(event -> {

            // deserialization

            deserialize();

            optionButtons[1].setEnabled(true);
            optionButtons[4].setEnabled(true);
            optionButtons[5].setEnabled(false);
        });

        optionButtons[6].addActionListener(event -> {

            // end simulation
            // save data
            saveData();

            dispose();
            System.exit(0);
        });

        // set mouse and action listener for matrix buttons
        MatrixHelper.setMatrixButtonsListeners(appData, matrixButtons);
    }


    /**
     *  This method is invoked when user presses button for enabling moving
     */
    private void enableMoving() {

        startTime = System.currentTimeMillis();
        delayTime = 30_000;
        appData.setTempTimerTickTime(System.currentTimeMillis());
        temperatureTimer.schedule(new TemperatureTimerTask(this, appData.getPersons()), delayTime, 30_000);
        movementThread.start();
        measurementThread.start();
    }

    /**
     *  This method is invoked when user presses button for sending ambulance
     */
    private void sendAmbulance() {

        if (supervisionSystem.isStackEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    NO_INFECTED_PERSONS_MESSAGE,
                    Constants.NOTIFICATION_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        if (appData.getHospitals().stream().allMatch(hospital -> hospital.getInfectedCount() == hospital.getCapacity())) {

            JOptionPane.showMessageDialog(
                    this,
                    NO_HOSPITALS_MESSAGE,
                    Constants.NOTIFICATION_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        if (appData.getAmbulances().stream().allMatch(Ambulance::isBusy)) {

            JOptionPane.showMessageDialog(
                    this,
                    NO_AMBULANCES_MESSAGE,
                    Constants.NOTIFICATION_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        appData.getAmbulances()
                .forEach(ambulance -> {

                    if (!ambulance.isBusy() && !supervisionSystem.isStackEmpty()) {

                        Alarm alarm = supervisionSystem.pop();

                        ambulance.setBusy(true);
                        ambulance.setPersonId(alarm.getPersonId());

                        AmbulanceMovementThread ambulanceMovementThread =
                                new AmbulanceMovementThread(appData, ambulance, this);
                        ambulanceThreads.put(ambulance.getId(), ambulanceMovementThread);
                        ambulanceMovementThread.setMovementListener(id -> ambulanceThreads.remove(id));
                        ambulanceMovementThread.start();
                    }
                });

        /*Ambulance ambulance =
                ambulances.stream()
                        .filter(amb -> !amb.isBusy())
                        .findFirst()
                        .orElse(null);*/

        /*Ambulance ambulance =
                appData.getAmbulances().stream()
                        .filter(amb -> !amb.isBusy())
                        .findFirst()
                        .orElse(null);

        if (ambulance == null) {

            JOptionPane.showMessageDialog(
                    this,
                    NO_AMBULANCES_MESSAGE,
                    Constants.NOTIFICATION_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        if (!supervisionSystem.isStackEmpty()) {

            Alarm alarm = supervisionSystem.pop();

            ambulance.setBusy(true);
            ambulance.setPersonId(alarm.getPersonId());

            /*new AmbulanceMovementThread(
                    matrix,
                    ambulance,
                    hospitals,
                    persons.get(alarm.getPersonId()),
                    this
            ).start();

            new AmbulanceMovementThread(
                    // appData.getMatrix(),
                    appData,
                    ambulance,
                    // appData.getHospitals(),
                    appData.getPersons().get(alarm.getPersonId()),
                    this
            ).start();

        } else
            JOptionPane.showMessageDialog(
                    this,
                    NO_INFECTED_PERSONS_MESSAGE,
                    Constants.NOTIFICATION_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE
            );*/
    }

    /**
     * Opens new window which shows data for each hospital
     */
    private void openHospitalsWindow() {

        int residents =
                appData.getData().get(Constants.KEY_NUM_OF_CHILDREN) + appData.getData().get(Constants.KEY_NUM_OF_ADULTS) +
                        appData.getData().get(Constants.KEY_NUM_OF_OLD);

        SwingUtilities.invokeLater(() -> {

            HospitalsWindow hospitalsWindow = new HospitalsWindow(appData.getHospitals(), residents);
            hospitalsWindow.setHospitalCreateListener(hospital -> {

                short direction = DIRECTION_ROW;

                Utils.getRandomPositionForHospital(appData.getMatrix().length, direction, hospital);

                while (appData.getMatrix()[hospital.getPositionX()][hospital.getPositionY()] != null) {

                    direction = (short) (1 - direction);
                    Utils.getRandomPositionForHospital(appData.getMatrix().length, direction, hospital);
                }

                appData.getHospitals().add(hospital);
                appData.getMatrix()[hospital.getPositionX()][hospital.getPositionY()] = hospital;
                matrixButtons[hospital.getPositionX()][hospital.getPositionY()].setBackground(Color.decode(Colors.COLOR_HOSPITAL));
            });
        });
    }

    /**
     * Serialize data and stop simulation
     * Job is done on other thread
     * UI is not blocked, only disabled until job is done
     */
    private void serialize() {

        setEnabled(false);

        new Thread(() -> {

            // save data using separate thread
            // in this way blocking of UI is avoided

            try {

                if (hospitalThread != null && hospitalThread.isAlive())
                    try {
                        hospitalThread.join();
                    } catch (InterruptedException ex) {
                        LOGGER.warning(ex.fillInStackTrace().toString());
                    }

                for (Iterator<AmbulanceMovementThread> i = ambulanceThreads.values().iterator(); i.hasNext();) {

                    AmbulanceMovementThread ambulanceMovementThread = i.next();
                    ambulanceMovementThread.stopRunning();

                    try {
                        ambulanceMovementThread.join();
                    } catch (InterruptedException ex) {
                        LOGGER.warning(ex.fillInStackTrace().toString());
                    }
                }


                movementThread.stopRunning();
                try {
                    movementThread.join();
                } catch (InterruptedException ex) {
                    LOGGER.warning(ex.fillInStackTrace().toString());
                }

                measurementThread.stopRunning();

                appData.setSerializationTime(System.currentTimeMillis());
                temperatureTimer.cancel();

                ObjectOutputStream outputStream =
                        new ObjectOutputStream(new FileOutputStream(new File(Constants.FILE_PATH_SERIALIZATION_DATA)));

                outputStream.writeObject(appData);
                outputStream.flush();
                outputStream.close();

            } catch (IOException ex) {
                LOGGER.warning(ex.fillInStackTrace().toString());
            } finally {
                setEnabled(true);
            }

        }).start();
    }

    /**
     * Run simulation again
     * Retrieve data from file and start all threads again
     */
    private void deserialize() {

        try {

            ObjectInputStream inputStream =
                    new ObjectInputStream(new FileInputStream(new File(Constants.FILE_PATH_SERIALIZATION_DATA)));

            appData = null;
            appData = (AppData) inputStream.readObject();

            inputStream.close();

            // restore every thread
            runThreadsAgain();

        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }

    }

    /**
     * When user clicks Run Simulation Again, then threads needs to be activated again
     * And continue to work
     */
    private void runThreadsAgain() {

        for (AmbulanceMovementData data : appData.getAmbulanceMovementData().values()) {

            Ambulance ambulance =
                    appData.getAmbulances()
                            .stream()
                            .filter(a -> a.getId() == data.getAmbulanceId())
                            .findFirst()
                            .orElse(null);

            AmbulanceMovementThread ambulanceMovementThread =
                    new AmbulanceMovementThread(appData, ambulance, this);
            ambulanceThreads.put(data.getAmbulanceId(), ambulanceMovementThread);
            ambulanceMovementThread.setMovementListener(id -> ambulanceThreads.remove(id));
            ambulanceMovementThread.start();
        }

        movementThread = new MovementThread(appData, this);
        movementThread.start();

        measurementThread = new MeasurementThread(appData, supervisionSystem);
        measurementThread.start();

        delayTime = delayTime - (appData.getSerializationTime() - appData.getTempTimerTickTime());
        temperatureTimer = new Timer(true);
        temperatureTimer.schedule(new TemperatureTimerTask(this, appData.getPersons()), delayTime, 30_000);
        appData.setTempTimerTickTime(System.currentTimeMillis());
    }

    /**
     * For each person in same house as person passed as argument
     */
    private void notifyHousehold(Person person) {

        appData.getPersons().values().stream()
                .filter(p -> p.getHomeId() == person.getHomeId())
                .forEach(p -> {

                    if (p != person) {

                        p.setMove(false);

                        Home home = (Home) appData.getBuildings().get(p.getHomeId());

                        if (!isInHospital(p) && !isInAmbulance(p) && !isInHome(p) && !p.isInfected()) {

                            LocationData homePosition =
                                    new LocationData(
                                            appData.getBuildings().get(person.getHomeId()).getPositionX(),
                                            appData.getBuildings().get(person.getHomeId()).getPositionY()
                                    );

                            LinkedList<ShortestPath.Node> path =
                                    ShortestPath.shortestPath(
                                            appData.getMatrix(),
                                            person.getLocationData(),
                                            homePosition
                                    );

                            appData.getUrgentMovementData().put(p.getId(), new UrgentMovementData(p.getId(), path));

                            /*UrgentMovementThread urgentMovementThread =
                                    new UrgentMovementThread(appData, p, this);
                            urgentThreads.put(p.getId(), urgentMovementThread);
                            urgentMovementThread.setUrgentMovementListener(id -> urgentThreads.remove(id));
                            urgentMovementThread.start();*/
                        }
                    }
                });
    }


    /**
     * Checks if person is in hospital
     */
    private boolean isInHospital(Person person) {

        for (Hospital hospital : appData.getHospitals())
            if (hospital.getInfected().values().stream().anyMatch(p -> p.getId() == person.getId()))
                return true;

        return false;
    }

    /**
     * Checks if person is in ambulance (currently transporting to hospital)
     */
    private boolean isInAmbulance(Person person) {
        return appData.getAmbulances().stream().anyMatch(ambulance -> ambulance.getPersonId() == person.getId());
    }

    /**
     * Check if person is in home
     */
    private boolean isInHome(Person person) {

        Home home = (Home) appData.getBuildings().get(person.getHomeId());

        return person.getLocationData().getPositionX() == home.getPositionX() &&
                person.getLocationData().getPositionY() == home.getPositionY();
    }

    /**
     * Save simulation data to file when simulation is ended
     */
    private void saveData() {

        long endTime = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("HH_mm_ss_dd_MMMM_yyyy");

        String filename = Constants.FILE_PATH_SIMULATION_DATA + Constants.SIMULATION_DATA_FILE_NAME_PREFIX +
                formatter.format(new Date()) + Constants.EXTENSION_TEXT_FILE;

        try {

            FileWriter writer = new FileWriter(new File(filename));

            long time = endTime - startTime;

            Utils.writeDataToTxtFile(writer, appData, time);

            writer.flush();
            writer.close();

        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }
}