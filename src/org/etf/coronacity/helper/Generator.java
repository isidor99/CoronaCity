package org.etf.coronacity.helper;

import org.etf.coronacity.model.Ambulance;
import org.etf.coronacity.model.building.*;
import org.etf.coronacity.model.person.Adult;
import org.etf.coronacity.model.person.Child;
import org.etf.coronacity.model.person.Old;
import org.etf.coronacity.model.person.Person;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Generator {

    private static final Logger LOGGER = Logger.getLogger(Generator.class.getName());

    public static HashMap<Long, Building> generateBuildings(int hSize, int cSize) {

        HashMap<Long, Building> buildings = new HashMap<>();

        for (int i = 0; i < hSize; i++) {
            Building home = new Home();
            buildings.put(home.getId(), home);
        }

        for (int i = 0; i < cSize; i++) {
            Building checkpoint = new Checkpoint();
            buildings.put(checkpoint.getId(), checkpoint);
        }

        return buildings;
    }


    /*public static ArrayList<Checkpoint> generateCheckpoints(int size) {

        ArrayList<Checkpoint> checkpoints = new ArrayList<>();

        for (int i = 0; i < size; i++)
            checkpoints.add(new Checkpoint());

        return checkpoints;
    }*/


    /*
        Generate Persons
     */
    public static HashMap<Long, Person> generatePersons(int cSize, int aSize, int oSize) {

        HashMap<Long, Person> persons = new HashMap<>(cSize + aSize + oSize);

        ArrayList<String> maleNames = readTextFile(new File(Constants.FILE_PATH_MALE_NAMES));
        ArrayList<String> femaleNames = readTextFile(new File(Constants.FILE_PATH_FEMALE_NAMES));
        ArrayList<String> surnames = readTextFile(new File(Constants.FILE_PATH_SURNAMES));

        int year = Calendar.getInstance().get(Calendar.YEAR);

        int cCount = 0, aCount = 0, oCount = 0;

        while (cCount < cSize || aCount < aSize || oCount < oSize) {

            Person person;
            int from = calculateAgeFrom(cCount, aCount, cSize, aSize);
            int to = calculateAgeTo(aCount, oCount, aSize, oSize);

            int age = Utils.getRandomFromRange(from, to);

            if (cCount < cSize && age <= Constants.AGE_CHILD_HIGH) {

                person = new Child();
                cCount++;

            } else if (aCount < aSize && age <= Constants.AGE_ADULT_HIGH) {

                person = new Adult();
                aCount++;

            } else {

                person = new Old();
                oCount++;
            }

            person.setGender(Person.Gender.values()[Utils.getRandomFromRange(0, 1)]);

            int namePos = person.getGender() == Person.Gender.MALE ?
                    Utils.getRandomFromRange(0, maleNames.size() - 1) :
                    Utils.getRandomFromRange(0, femaleNames.size() - 1);

            int surnamePos = Utils.getRandomFromRange(0, surnames.size() - 1);

            person.setName(person.getGender() == Person.Gender.MALE ? maleNames.get(namePos) : femaleNames.get(namePos));
            person.setSurname(surnames.get(surnamePos));
            person.setBirthYear(year - age);
            person.setBodyTemperature(Utils.getBodyTemperatureFromRange(Constants.TEMPERATURE_LOW, 1));

            persons.put(person.getId(), person);
        }

        /*
        // generate children
        for (int i = 0; i < cSize; i++) {

            Person child = new Child();

            child.setGender((short) Utils.getRandomFromRange(Constants.GENDER_MALE, Constants.GENDER_FEMALE));

            int namePos = child.getGender() == Constants.GENDER_MALE ?
                    Utils.getRandomFromRange(0, maleNames.size() - 1) :
                    Utils.getRandomFromRange(0, femaleNames.size() - 1);

            int surnamePos = Utils.getRandomFromRange(0, surnames.size() - 1);

            int age = Utils.getRandomFromRange(Constants.AGE_CHILD_LOW, Constants.AGE_CHILD_HIGH);

            child.setName(child.getGender() == Constants.GENDER_MALE ? maleNames.get(namePos) : femaleNames.get(namePos));
            child.setSurname(surnames.get(surnamePos));
            child.setBirthYear(year - age);

            persons.add(child);
        }

        // generate adults
        for (int i = 0; i < aSize; i++) {

            Person adult = new Adult();

            adult.setGender((short) Utils.getRandomFromRange(Constants.GENDER_MALE, Constants.GENDER_FEMALE));

            int namePos = adult.getGender() == Constants.GENDER_MALE ?
                    Utils.getRandomFromRange(0, maleNames.size() - 1) :
                    Utils.getRandomFromRange(0, femaleNames.size() - 1);

            int surnamePos = Utils.getRandomFromRange(0, surnames.size() - 1);

            int age = Utils.getRandomFromRange(Constants.AGE_ADULT_LOW, Constants.AGE_ADULT_HIGH);

            adult.setName(adult.getGender() == Constants.GENDER_MALE ? maleNames.get(namePos) : femaleNames.get(namePos));
            adult.setSurname(surnames.get(surnamePos));
            adult.setBirthYear(year - age);

            persons.add(adult);
        }

        // generate old
        for (int i = 0; i < oSize; i++) {

            Person old = new Old();

            old.setGender((short) Utils.getRandomFromRange(Constants.GENDER_MALE, Constants.GENDER_FEMALE));

            int namePos = old.getGender() == Constants.GENDER_MALE ?
                    Utils.getRandomFromRange(0, maleNames.size() - 1) :
                    Utils.getRandomFromRange(0, femaleNames.size() - 1);

            int surnamePos = Utils.getRandomFromRange(0, surnames.size() - 1);

            int age = Utils.getRandomFromRange(Constants.AGE_OLD_LOW, Constants.AGE_OLD_HIGH);

            old.setName(old.getGender() == Constants.GENDER_MALE ? maleNames.get(namePos) : femaleNames.get(namePos));
            old.setSurname(surnames.get(surnamePos));
            old.setBirthYear(year - age);

            persons.add(old);
        }*/

        return persons;
    }


    public static ArrayList<Ambulance> generateAmbulances(int numberOfAmbulances) {

        ArrayList<Ambulance> ambulances = new ArrayList<>();

        for (int i = 0; i < numberOfAmbulances; i++)
            ambulances.add(new Ambulance());

        return ambulances;
    }


    public static ArrayList<Hospital> generateHospitals(int size, int capacity) {

        ArrayList<String> names = readTextFile(new File(Constants.FILE_PATH_HOSPITAL_NAMES));

        ArrayList<Hospital> hospitals = new ArrayList<>();

        int[] namesInd = { -1, -1, -1, -1 };
        int count = 0;
        while (count < 4) {

            int rand = Utils.getRandomFromRange(0, names.size() - 1);

            boolean isInArray = Arrays.stream(namesInd).anyMatch(i -> i == rand);
            if (!isInArray)
                namesInd[count++] = rand;
        }

        hospitals.add(new Hospital(names.get(namesInd[0]), capacity, 0, 0));
        hospitals.add(new Hospital(names.get(namesInd[1]), capacity, 0, size - 1));
        hospitals.add(new Hospital(names.get(namesInd[2]), capacity, size - 1, 0));
        hospitals.add(new Hospital(names.get(namesInd[3]), capacity, size - 1, size - 1));

        return hospitals;
    }


    //
    // Private Methods
    //


    /*
        Reading given text file
     */
    private static ArrayList<String> readTextFile(File file) {

        if (LOGGER.getHandlers() == null || LOGGER.getHandlers().length == 0)
            Utils.createLoggerHandler(LOGGER);

        ArrayList<String> data = new ArrayList<>();
        BufferedReader inputReader = null;

        try {

            inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            String line;
            while ((line = inputReader.readLine()) != null)
                data.add(line);

        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        } finally {

            if (inputReader != null)
                try {
                    inputReader.close();
                } catch (IOException ex) {
                    LOGGER.warning(ex.fillInStackTrace().toString());
                }
        }

        return data;
    }

    /*
        Calculate minimum age
     */
    private static int calculateAgeFrom(int cCount, int aCount, int cSize, int aSize) {

        if (cCount < cSize)
            return Constants.AGE_CHILD_LOW;
        else if (aCount < aSize)
            return Constants.AGE_ADULT_LOW;

        return Constants.AGE_OLD_LOW;
    }

    /*
        Calculate maximum age
     */
    private static int calculateAgeTo(int aCount, int oCount, int aSize, int oSize) {

        if (oCount < oSize)
            return Constants.AGE_OLD_HIGH;
        else if (aCount < aSize)
            return Constants.AGE_ADULT_HIGH;

        return Constants.AGE_CHILD_HIGH;
    }
}
