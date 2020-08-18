package org.etf.coronacity.helper;

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
import java.util.ArrayList;
import java.util.HashMap;

public class MatrixHelper {

    /*
        Setup First Aid stations
     */
    public static ArrayList<Hospital> createHospitals(Object[][] matrix, int size, int capacity) {

        ArrayList<Hospital> hospitals = Generator.generateHospitals(size, capacity);

        matrix[0][0] = hospitals.get(0);
        matrix[0][size - 1] = hospitals.get(1);
        matrix[size - 1][0] = hospitals.get(2);
        matrix[size - 1][size - 1] = hospitals.get(3);

        return hospitals;
    }

    /*
        Set Homes and Checkpoints on matrix
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

    public static JButton[][] populateMatrixPanel(AppData appData, JPanel matrixPanel) {

        int length = appData.getMatrix().length;
        JButton[][] matrixButtons = new JButton[length][length];

        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++) {

                matrixButtons[i][j] = new JButton();

                // set background color
                if (appData.getMatrix()[i][j] instanceof Hospital)
                    matrixButtons[i][j].setBackground(Color.decode(Colors.COLOR_HOSPITAL));
                else if (appData.getMatrix()[i][j] instanceof Checkpoint)
                    matrixButtons[i][j].setBackground(Color.decode(Colors.COLOR_CHECKPOINT));
                else if (appData.getMatrix()[i][j] instanceof Home)
                    matrixButtons[i][j].setBackground(Color.decode(Colors.COLOR_HOME));

                matrixPanel.add(matrixButtons[i][j]);
            }

        return matrixButtons;
    }

    /*
        Set People in houses
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

    /*
        Algorithm for setting adults and old people in houses
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

    /*
        Set children in homes
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
}
