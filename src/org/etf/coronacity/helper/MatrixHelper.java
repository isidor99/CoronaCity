package org.etf.coronacity.helper;

import org.etf.coronacity.gui.window.HomeDataWindow;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.building.Building;
import org.etf.coronacity.model.building.Checkpoint;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.Adult;
import org.etf.coronacity.model.person.Child;
import org.etf.coronacity.model.person.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

/*
    This is helper class
    Contains methods for setting buildings on matrix and arranging people by houses
 */
public class MatrixHelper {

    /**
     * Set hospitals in the corners of matrix
     * @param matrix matrix represented by two dimensional array Object[][]
     * @param capacity capacity of each hospital
     * @return ArrayList<Hospital> list of hospitals
     */
    public static ArrayList<Hospital> createHospitals(Object[][] matrix, int capacity) {

        int size = matrix.length;

        ArrayList<Hospital> hospitals = Generator.generateHospitals(size, capacity);

        matrix[0][0] = hospitals.get(0);
        matrix[0][size - 1] = hospitals.get(1);
        matrix[size - 1][0] = hospitals.get(2);
        matrix[size - 1][size - 1] = hospitals.get(3);

        return hospitals;
    }

    /**
     * Set homes and checkpoints on random positions on matrix
     * @param matrix matrix represented by two dimensional array Object[][]
     * @param buildings HashMap that contains buildings
     */
    public static void setHomesAndCheckpoints(Object[][] matrix, HashMap<Long, Building> buildings) {

        for (Building building : buildings.values()) {

            int rowPos = Utils.getRandomFromRange(1, matrix.length - 2);
            int colPos = Utils.getRandomFromRange(1, matrix[0].length - 2);

            while (matrix[rowPos][colPos] != null) {

                rowPos = Utils.getRandomFromRange(1, matrix.length - 2);
                colPos = Utils.getRandomFromRange(1, matrix[0].length - 2);

            }

            matrix[rowPos][colPos] = building;

            building.setPositionX(rowPos);
            building.setPositionY(colPos);
        }
    }

    /**
     * Create buttons that represents matrix
     * @param appData application data represented by object of type AppData
     * @param matrixPanel JPanel to place buttons on
     * @return two dimensional array of buttons
     */
    public static JButton[][] populateMatrixPanel(AppData appData, JPanel matrixPanel) {

        int length = appData.getMatrix().length;
        JButton[][] matrixButtons = new JButton[length][length];

        for (int i = 0; i < length + 1; i++)
            for (int j = 0; j < length + 1; j++) {

                if (i == 0 && j == 0)
                    matrixPanel.add(new JLabel());

                else if (i == 0)
                    matrixPanel.add(new JLabel(String.valueOf(j), SwingConstants.CENTER));

                else if (j == 0)
                    matrixPanel.add(new JLabel(String.valueOf(i), SwingConstants.CENTER));

                else {

                    matrixButtons[i - 1][j - 1] = new JButton();

                    // set background color
                    if (appData.getMatrix()[i - 1][j - 1] instanceof Hospital)
                        matrixButtons[i - 1][j - 1].setBackground(Color.decode(Colors.COLOR_HOSPITAL));
                    else if (appData.getMatrix()[i - 1][j - 1] instanceof Checkpoint)
                        matrixButtons[i - 1][j - 1].setBackground(Color.decode(Colors.COLOR_CHECKPOINT));
                    else if (appData.getMatrix()[i - 1][j - 1] instanceof Home)
                        matrixButtons[i - 1][j - 1].setBackground(Color.decode(Colors.COLOR_HOME));

                    matrixPanel.add(matrixButtons[i - 1][j - 1]);
                }
            }

        return matrixButtons;
    }

    /**
     * Arrange persons by houses
     * @param persons HashMap that contains buildings
     * @param buildings HashMap that contains buildings
     * @param size matrix size
     */
    public static void setPeopleInHouses(HashMap<Long, Person> persons, HashMap<Long, Building> buildings, int size) {

        ArrayList<Person> adultsAndOld = Utils.getAdultsAndOld(persons);
        ArrayList<Child> children = Utils.getChildren(persons);
        ArrayList<Home> homes = Utils.getHomes(buildings);

        setAdultsAndOld(adultsAndOld, homes, size);

        ArrayList<Home> homesWithHosts = Utils.getHomesWithHosts(homes);

        if (children.size() > 0)
            setChildren(children, homesWithHosts, size);
    }

    /**
     * Take current and previous position and determine the direction
     * @param posX current position x (row)
     * @param posY current position y (column)
     * @param nextX previous position x (row)
     * @param nextY previous position y (column)
     * @return direction
     */
    public static LocationData.Direction getPersonDirection(int posX, int posY, int nextX, int nextY) {

        if (posY == nextY) {

            if (posX == nextX + 1)
                return LocationData.Direction.TOP;
            else if (posX == nextX - 1)
                return LocationData.Direction.BOTTOM;

        } else if (posX == nextX) {

            if (posY == nextY + 1)
                return LocationData.Direction.LEFT;
            else if (posY == nextY - 1)
                return LocationData.Direction.RIGHT;

        } else if (posX == nextX + 1) {

            if (posY == nextY + 1)
                return LocationData.Direction.LEFT_TOP;
            else if (posY == nextY - 1)
                return LocationData.Direction.RIGHT_TOP;

        } else if (posX == nextX - 1) {

            if (posY == nextY + 1)
                return LocationData.Direction.LEFT_BOTTOM;
            else if (posY == nextY - 1)
                return LocationData.Direction.RIGHT_BOTTOM;

        }

        return null;
    }

    /**
     * For each button determine the tool tip text (data about persons on that position)
     * @param appData application data represented by object of type AppData
     * @param matrixButtons two dimensional array of buttons
     */
    public static void setMatrixButtonsToolTipTexts(AppData appData, JButton[][] matrixButtons) {

        for (int i = 0; i < matrixButtons.length; i++)
            for (int j = 0; j < matrixButtons.length; j++) {

                if (!(appData.getMatrix()[i][j] instanceof Home) && !(appData.getMatrix()[i][j] instanceof Hospital)) {

                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append("<html>");

                    ArrayList<Person> persons = Utils.getPersonsOnPosition(appData.getPersons().values(), i, j);

                    persons.forEach(person ->
                            stringBuilder.append(person.getId())
                                    .append(", ")
                                    .append(person.getName())
                                    .append(" ")
                                    .append(person.getSurname())
                                    .append(", ")
                                    .append(person.getBirthYear())
                                    .append(", ")
                                    .append(person.getHomeId())
                                    .append("<br>")
                    );

                    stringBuilder.append("</html>");

                    matrixButtons[i][j].setToolTipText(stringBuilder.toString());

                } else if (appData.getMatrix()[i][j] instanceof Hospital) {

                    // find hospital on this position and get its name
                    // its name will be the tool tip text

                    matrixButtons[i][j].setToolTipText(getHospitalName(appData.getHospitals(), i, j));
                }
            }
    }

    /**
     * For buttons that represents position on which home are place, add action listeners
     * @param appData application data represented by object of type AppData
     * @param matrixButtons two dimensional array of buttons
     */
    public static void setMatrixButtonsListeners(AppData appData, JButton[][] matrixButtons) {

        for (int i = 0; i < matrixButtons.length; i++)
            for (int j = 0; j < matrixButtons.length; j++) {

                final Object obj = appData.getMatrix()[i][j];

                if (obj instanceof Home)
                    matrixButtons[i][j].addActionListener(event -> new HomeDataWindow(appData, ((Home) obj).getId()));

                matrixButtons[i][j].addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ToolTipManager.sharedInstance().setDismissDelay(100_000);
                    }
                });
            }
    }

    //
    // private

    /**
     * Arrange adults and old in homes
     * @param persons list of persons
     * @param homes list of homes
     * @param size size of matrix
     */
    private static void setAdultsAndOld(ArrayList<Person> persons, ArrayList<Home> homes, int size) {

        int maleIndex = 0, femaleIndex = 0;
        for (Person p : persons) {

            Home home;

            if (p.getGender() == Person.Gender.MALE) {

                home = homes.get(maleIndex++);

                if (maleIndex == homes.size())
                    maleIndex = 0;

            } else {

                home = homes.get(femaleIndex++);

                if (femaleIndex == homes.size())
                    femaleIndex = 0;
            }

            int offset;
            if (p instanceof Adult)
                offset = Utils.getMovementRangeFromMatrixSize(size);
            else
                offset = Constants.OLD_MOVEMENT_RANGE;


            LocationData locationData = new LocationData(home.getPositionX(), home.getPositionY());
            Utils.calculateMovingRange(locationData, offset, size);

            p.setHomeId(home.getId());
            p.setLocationData(locationData);
            home.addOneHost();
        }
    }

    /**
     * Set children in homes
     * Child can't be alone in home
     * @param children list of children
     * @param homes list of non-empty homes
     * @param size size of matrix
     */
    private static void setChildren(ArrayList<Child> children, ArrayList<Home> homes, int size) {

        int childrenPerHome = children.size() / homes.size();
        int childIndex = 0;

        for (Home h : homes) {

            int numberOfChildren = 0;
            while (numberOfChildren <= childrenPerHome) {

                Child child = children.get(childIndex++);

                LocationData locationData = new LocationData(h.getPositionX(), h.getPositionY());
                locationData.setMinY(0);
                locationData.setMaxX(size - 1);
                locationData.setMinY(0);
                locationData.setMaxY(size - 1);
                locationData.setDirection(Utils.getRandomDirection());

                child.setHomeId(h.getId());
                child.setLocationData(locationData);
                h.addOneHost();

                numberOfChildren++;

                if (childIndex == children.size())
                    return;
            }
        }
    }

    /**
     * Loop through hospitals, find hospital on given position and return its name
     */
    private static String getHospitalName(ArrayList<Hospital> hospitals, int posX, int posY) {

        for (Hospital hospital : hospitals)
            if (hospital.getPositionX() == posX && hospital.getPositionY() == posY)
                return hospital.getName();

        return "";
    }
}
