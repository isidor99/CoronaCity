package org.etf.coronacity.helper;

import org.etf.coronacity.gui.window.CoronaMainWindow;
import org.etf.coronacity.model.carrier.AppData;
import org.etf.coronacity.model.carrier.Data;
import org.etf.coronacity.model.LocationData;
import org.etf.coronacity.model.building.Building;
import org.etf.coronacity.model.building.Checkpoint;
import org.etf.coronacity.model.building.Home;
import org.etf.coronacity.model.building.Hospital;
import org.etf.coronacity.model.person.*;

import java.io.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
    This is helper class
    This class contains some helper static functions which are used in other classes
 */
public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    /**
     * Random from given range
     * @param from lower limit including that number
     * @param to upper limit including that number
     * @return random from given range
     */
    public static int getRandomFromRange(int from, int to) {

        Random random = new Random();

        return random.nextInt(to + 1 - from) + from;
    }

    /**
     * When new hospital is created, get random position from first or last row/column
     * @param size size of matrix
     * @param direction specifies row or column
     * @param hospital new hospital which is created
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

    /**
     * Calculate new random body temperature
     * Body temperature can change by .1 to .6 relative to the previous value
     * @param currentTemperature current body temperature
     * @param sgn indicates whether the body temperature will rise or fall
     * @return double, new value of body temperature
     */
    public static double getBodyTemperatureFromRange(double currentTemperature, int sgn) {

        Random random = new Random();

        double newTemp = .1 + (.6 - .1) * random.nextDouble();

        // temperature must be between 35.0 and 40.0
        if (currentTemperature - newTemp < Constants.TEMPERATURE_LOWEST)
            sgn = 1;
        else if (currentTemperature + newTemp > Constants.TEMPERATURE_HIGHEST)
            sgn = -1;

        return currentTemperature + sgn * newTemp;
    }

    /**
     * Random between 10% and 15% of number of residents
     * @param residents number of residents
     * @return capacity of hospital
     */
    public static int getCapacityFromResidentsNumber(int residents) {
        return getRandomFromRange((int) Math.round(.1 * residents), (int) Math.round(.15 * residents));
    }

    /**
     * Calculate 25% of size of matrix
     * @param size size of matrix
     * @return 25 % of size
     */
    public static int getMovementRangeFromMatrixSize(int size) {

        return (int) Math.round(.25 * size);
    }

    /**
     * Calculate moving range for person based on current position and offset
     * @param locationData location data of person (contains number of row and columns of person in matrix)
     * @param offset number of fields from home in one direction that person can go
     * @param size size of matrix
     */
    public static void calculateMovingRange(LocationData locationData, int offset, int size) {

        int minX = locationData.getPositionX() - offset;
        int maxX = locationData.getPositionX() + offset;
        int minY = locationData.getPositionY() - offset;
        int maxY = locationData.getPositionY() + offset;

        // set random direction
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

    /**
     * Get objects of type Adult and Old from objects list of type Person
     * @param persons list of persons
     * @return list of adults and old
     */
    public static ArrayList<Person> getAdultsAndOld(HashMap<Long, Person> persons) {

        return (ArrayList<Person>)
                persons.values().stream()
                        .filter(person -> person instanceof Adult || person instanceof Old)
                        .collect(Collectors.toList());
    }

    /**
     * Get objects of type Child from objects list of type Person
     * @param persons list of persons
     * @return list of children
     */
    public static ArrayList<Child> getChildren(HashMap<Long, Person> persons) {

        return (ArrayList<Child>)
                persons.values().stream()
                        .filter(person -> person instanceof Child)
                        .map(Child.class::cast)
                        .collect(Collectors.toList());
    }

    /**
     * Get objects of type Home from objects list of type Building
     * @param buildings list of buildings
     * @return list of homes
     */
    public static ArrayList<Home> getHomes(HashMap<Long, Building> buildings) {

        return (ArrayList<Home>)
                buildings.values().stream()
                        .filter(building -> building instanceof Home)
                        .map(Home.class::cast)
                        .collect(Collectors.toList());
    }

    /**
     * Get non-empty homes
     * @param homes list of homes
     * @return list of non-empty homes
     */
    public static ArrayList<Home> getHomesWithHosts(ArrayList<Home> homes) {

        return (ArrayList<Home>)
                homes.stream()
                        .filter(home -> home.getHosts() > 0)
                        .collect(Collectors.toList());
    }

    /**
     * Get objects of type Checkpoint from objects list of type Building
     * @param buildings list of buildings
     * @return list of checkpoints
     */
    public static ArrayList<Checkpoint> getCheckpoints(HashMap<Long, Building> buildings) {

        return (ArrayList<Checkpoint>)
                buildings.values().stream()
                        .filter(building -> building instanceof Checkpoint)
                        .map(Checkpoint.class::cast)
                        .collect(Collectors.toList());
    }

    /**
     * Get list of persons from home with given id
     * @param persons list of all persons
     * @param homeId id of home
     * @return list of persons in home with given id (id is unique)
     */
    public static ArrayList<Person> getHostsForHome(HashMap<Long, Person> persons, long homeId) {

        return (ArrayList<Person>)
                persons.values().stream()
                        .filter(person -> person.getHomeId() == homeId)
                        .collect(Collectors.toList());
    }

    /**
     * For given position return list of persons on that position
     * Position is represented with X and Y (row and column in matrix, x is number of row, y is number of column)
     * @param persons list of all persons
     * @param posX number of row in matrix
     * @param posY number of column in matrix
     * @return list of persons on given position
     */
    public static ArrayList<Person> getPersonsOnPosition(Collection<Person> persons, int posX, int posY) {

        return (ArrayList<Person>) persons.stream()
                .filter(person ->
                        person.getLocationData().getPositionX() == posX && person.getLocationData().getPositionY() == posY)
                .collect(Collectors.toList());
    }

    /**
     * Pick one of eight possible directions
     * @return random direction
     */
    public static LocationData.Direction getRandomDirection() {

        Random random = new Random();

        int pick = random.nextInt(LocationData.Direction.values().length);
        return LocationData.Direction.values()[pick];
    }

    /**
     * Calculate average from LinkedList values
     * @param temperatures LinkedList with double values
     * @return double, average value of LinkedList values
     */
    public static double average(LinkedList<Double> temperatures) {

        double sum = 0;
        for (double temp : temperatures)
            sum += temp;

        return sum / temperatures.size();
    }

    /**
     * Write simulation data to .txt file when simulation is ended
     * @param writer FileWriter object
     * @param appData application data
     * @param time simulation duration
     * @throws IOException FileWriter object can throw this error
     * @throws ClassNotFoundException This error may be thrown when casting data from file to object of type Data
     */
    public static void writeDataToTxtFile(FileWriter writer, AppData appData, long time) throws IOException, ClassNotFoundException {

        long seconds = time / 1000 % 60;
        long minutes = time / (1000 * 60) % 60;
        long hours = time / (1000 * 60 * 60);

        HashMap<String, Integer> data = appData.getData();

        writer.write("Vrijeme trajanja simulacije " +
                (hours > 0 ? hours + " sati" : "") + ", " +
                (minutes > 0 ? minutes + " minuta" : "") + ", " +
                seconds + " sekundi\n");

        writer.write("Broj djece " + data.get(Constants.KEY_NUM_OF_CHILDREN) + "\n");
        writer.write("Broj odraslih " + data.get(Constants.KEY_NUM_OF_ADULTS) + "\n");
        writer.write("Broj staraca " + data.get(Constants.KEY_NUM_OF_OLD) + "\n");
        writer.write("Broj kuća " + data.get(Constants.KEY_NUM_OF_HOMES) + "\n");
        writer.write("Broj kontrolnih punktova " + data.get(Constants.KEY_NUM_OF_CHECKPOINTS) + "\n");
        writer.write("Broj bolnica " + appData.getHospitals().size() + "\n");
        writer.write("Broj ambulantnih vozila " + data.get(Constants.KEY_NUM_OF_AMBULANCES) + "\n");

        ObjectInputStream objectInputStream =
                new ObjectInputStream(new FileInputStream(new File(Constants.FILE_PATH_DATA + Constants.DATA_FILE_NAME)));

        Data stats = (Data) objectInputStream.readObject();

        objectInputStream.close();

        writer.write("Ukupan broj zaraženih " + stats.getInfected() + "\n");
        writer.write("Ukupan broj oporavljenih " + stats.getRecovered() + "\n");
        writer.write("Broj zaražene djece " + stats.getRecovered() + "\n");
        writer.write("Broj oporavljene djece " + stats.getRecovered() + "\n");
        writer.write("Broj zaraženih osoba srednje životne dobi " + stats.getRecovered() + "\n");
        writer.write("Broj oporavljenih osoba srednje životne dobi " + stats.getRecovered() + "\n");
        writer.write("Broj zaraženih osoba starije životne dobi " + stats.getRecovered() + "\n");
        writer.write("Broj oporaveljih osoba starije životne dobi " + stats.getRecovered() + "\n");
        writer.write("Broj zaraženih muškaraca " + stats.getRecovered() + "\n");
        writer.write("Broj oporavljenih muškaraca " + stats.getRecovered() + "\n");
        writer.write("Broj zaraženih žena " + stats.getRecovered() + "\n");
        writer.write("Broj oporavljenih žena " + stats.getRecovered() + "\n");
    }

    /**
     *  Init data file
     *  In this file, data about number of infected, recovered and some additional data are stored
     */
    public static void initDataFile() {

        try {

            ObjectOutputStream outputStream =
                    new ObjectOutputStream(
                            new FileOutputStream(Constants.FILE_PATH_DATA + Constants.DATA_FILE_NAME));

            Data data = new Data();

            outputStream.writeObject(data);

            outputStream.flush();
            outputStream.close();

        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }

    /**
     * Init Logger Handler
     * @param LOGGER Logger to init
     */
    public static void createLoggerHandler(final Logger LOGGER) {

        try {
            FileHandler handler = new FileHandler(Constants.FILE_PATH_LOG_FILE);
            LOGGER.addHandler(handler);
        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }

}

