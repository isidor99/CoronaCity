package org.etf.coronacity.helper;

import org.etf.coronacity.gui.window.CoronaMainWindow;
import org.etf.coronacity.model.carrier.Data;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.building.Building;
import org.etf.coronacity.model.building.Checkpoint;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**

    int getRandomFromRange(int, int);
    void getRandomPositionForHospital(int, int, int, int);
    double getBodyTemperatureFromRange(double, int);
    int getCapacityFromResidentsNumber(int);
    int getMovementRangeFromMatrixSize(int);
    int calculateMovingRange(LocationData, int, int);
    double distance(int, int, int, int);
    ArrayList<Person> getAdultsAndOld(HashMap<Long, Person>);
    ArrayList<Person> getChildren(HashMap<Long, Person>);
    ArrayList<Home> getHomes(HashMap<Long, Building>);
    ArrayList<Home> getHomesWithHosts(ArrayList<Home>);
    ArrayList<Checkpoint> getCheckpoints(HashMap<Long, Building>);
    ArrayList<Person> getHostsForHome(HashMap<Long, Person>, Home);
    ArrayList<Hospital> getHospitals(Object[][]);
    LocationData.Directions getRandomDirection();
    double average(LinkedList<Double>);
    void initDataFile();
    void createLoggerHandler(final Logger);

 */

public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    /*
        Returns random number for given range
     */
    public static int getRandomFromRange(int from, int to) {

        Random random = new Random();

        return random.nextInt(to + 1 - from) + from;
    }

    /*
        When new hospital is created, calculate new position for it
     */
    public static void getRandomPositionForHospital(int size, short direction, Hospital hospital) {

        int[] firstAndLast = {0, size - 1};

        if (direction == CoronaMainWindow.DIRECTION_ROW) {

            hospital.setPositionX(firstAndLast[getRandomFromRange(0, 1)]);
            hospital.setPositionY(getRandomFromRange(0, size - 1));

        } else if (direction == CoronaMainWindow.DIRECTION_COLUMN) {

            hospital.setPositionX(getRandomFromRange(0, size - 1));
            hospital.setPositionY(firstAndLast[getRandomFromRange(0, 1)]);
        }
    }

    /*
        Generate body temperature
     */
    public static double getBodyTemperatureFromRange(double currentTemperature, int sgn) {

        Random random = new Random();

        double newTemp = .1 + (.6 - .1) * random.nextDouble();

        // temperature must be between 35.0 and 40.0
        // this piece of code enables this feature
        if (currentTemperature - newTemp < Constants.TEMPERATURE_LOWEST)
            sgn = 1;
        else if (currentTemperature + newTemp > Constants.TEMPERATURE_HIGHEST)
            sgn = -1;

        return currentTemperature + sgn * newTemp;
    }


    /*
        Capacity for ambulants
     */
    public static int getCapacityFromResidentsNumber(int residents) {

        double from = .1 * residents;
        double to = .15 * residents;

        String[] fromString = String.valueOf(from).split("\\.");

        int fromInt = from > Integer.parseInt(fromString[0]) ?
                Integer.parseInt(fromString[0]) + 1 :
                Integer.parseInt(fromString[0]);

        int toInt = Integer.parseInt(String.valueOf(to).split("\\.")[0]);

        return getRandomFromRange(fromInt, toInt);
    }


    /*
        Calculate movement range for Adults
     */
    public static int getMovementRangeFromMatrixSize(int size) {

        return (int) Math.round(.25 * size);
    }

    /*
        Calculate movement range in all directions for Adults and Old
     */
    public static void calculateMovingRange(LocationData locationData, int offset, int size) {

        int minX = locationData.getPositionX() - offset;
        int maxX = locationData.getPositionX() + offset;
        int minY = locationData.getPositionY() - offset;
        int maxY = locationData.getPositionY() + offset;

        locationData.setDirection(getRandomDirection());

        // set X
        if (minX >= 0 && maxX <= size - 1) {

            locationData.setMinX(minX);
            locationData.setMaxX(maxX);
        } else if (minX < 0) {

            locationData.setMaxX(maxX - minX);
            locationData.setMinX(0);
        } else {

            locationData.setMinX(minX - (maxX - (size - 1)));
            locationData.setMaxX(size - 1);
        }


        // set Y
        if (minY >= 0 && maxY <= size - 1) {

            locationData.setMinY(minY);
            locationData.setMaxY(maxY);
        } else if (minY < 0) {

            locationData.setMaxY(maxY - minY);
            locationData.setMinY(0);
        } else {

            locationData.setMinY(minY - (maxY - (size - 1)));
            locationData.setMaxY(size - 1);
        }
    }


    /*
        Gets coordinates and calculate distance between points
     */
    public static double distance(int x1, int y1, int x2, int y2) {

        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /*
        Return adults and old from persons list
     */
    public static ArrayList<Person> getAdultsAndOld(HashMap<Long, Person> persons) {

        return (ArrayList<Person>)
                persons.values().stream()
                        .filter(person -> person instanceof Adult || person instanceof Old)
                        .collect(Collectors.toList());

        /*ArrayList<Person> adultsOld = new ArrayList<>();

        for (Person p : persons.values())
            if (p instanceof Adult || p instanceof Old)
                adultsOld.add(p);*/
    }


    /*
        Get children from persons list
     */
    public static ArrayList<Child> getChildren(HashMap<Long, Person> persons) {

        return (ArrayList<Child>)
                persons.values().stream()
                        .filter(person -> person instanceof Child)
                        .map(Child.class::cast)
                        .collect(Collectors.toList());

        /*ArrayList<Child> children = new ArrayList<>();

        for (Person p : persons.values())
            if (p instanceof Child)
                children.add((Child) p);

        return children;*/
    }


    /*
        Get homes from buildings list
     */
    public static ArrayList<Home> getHomes(HashMap<Long, Building> buildings) {

        return (ArrayList<Home>)
                buildings.values().stream()
                        .filter(building -> building instanceof Home)
                        .map(Home.class::cast)
                        .collect(Collectors.toList());

        /*ArrayList<Home> homes = new ArrayList<>();

        for (Building b : buildings.values())
            if (b instanceof Home)
                homes.add((Home) b);

        return homes;*/
    }


    /*
        Get homes which are not empty
     */
    public static ArrayList<Home> getHomesWithHosts(ArrayList<Home> homes) {

        return (ArrayList<Home>)
                homes.stream()
                        .filter(home -> home.getHosts() > 0)
                        .collect(Collectors.toList());
        /*ArrayList<Home> homesWithHosts = new ArrayList<>();

        for (Home h : homes)
            if (h.getHosts() > 0)
                homesWithHosts.add(h);

        return homesWithHosts;*/
    }

    /*
        Return checkpoints from buildings
     */
    public static ArrayList<Checkpoint> getCheckpoints(HashMap<Long, Building> buildings) {

        return (ArrayList<Checkpoint>)
                buildings.values().stream()
                        .filter(building -> building instanceof Checkpoint)
                        .map(Checkpoint.class::cast)
                        .collect(Collectors.toList());
    }

    /*
        Return data for persons which live in given house
     */
    public static ArrayList<Person> getHostsForHome(HashMap<Long, Person> persons, Home home) {

        return (ArrayList<Person>)
                persons.values().stream()
                        .filter(person -> person.getHomeId() == home.getId())
                        .collect(Collectors.toList());

        /*ArrayList<Person> hosts = new ArrayList<>();

        for (Person p : persons.values()) {

            if (p.getHomeId() == home.getId())
                hosts.add(p);

            if (home.getHosts() == hosts.size())
                break;
        }

        return hosts;*/
    }

    /*
        Return hospitals from matrix which represents city
     */
    public static ArrayList<Hospital> getHospitals(Object[][] matrix) {

        ArrayList<Hospital> hospitals = new ArrayList<>();

        for (Object[] arr : matrix)
            for (Object object : arr)
                if (object instanceof Hospital)
                    hospitals.add((Hospital) object);

        return hospitals;
    }

    public static LocationData.Direction getRandomDirection() {

        Random random = new Random();

        int pick = random.nextInt(LocationData.Direction.values().length);
        return LocationData.Direction.values()[pick];
    }

    public static double average(LinkedList<Double> temperatures) {

        double sum = 0;
        for (double temp : temperatures)
            sum += temp;

        return sum / temperatures.size();
    }

    /**
     *  Init data file
     *  In this file, data about number of infected, recovered and some additional data are stored
     */
    public static void initDataFile() {

        try {

            ObjectOutputStream outputStream =
                    new ObjectOutputStream(
                            new FileOutputStream(Constants.FILE_PATH_FIRST_AID_DATA + Constants.DATA_FILE_NAME));

            Data data = new Data();

            outputStream.writeObject(data);

            outputStream.flush();
            outputStream.close();

        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }

    public static void createLoggerHandler(final Logger LOGGER) {

        try {
            FileHandler handler = new FileHandler(Constants.FILE_PATH_LOG_FILE);
            LOGGER.addHandler(handler);
        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }

}

