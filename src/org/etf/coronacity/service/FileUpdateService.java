package org.etf.coronacity.service;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.carrier.Data;
import org.etf.coronacity.model.carrier.PersonCounter;
import org.etf.coronacity.model.person.Adult;
import org.etf.coronacity.model.person.Child;
import org.etf.coronacity.model.person.Person;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class FileUpdateService extends Thread {

    private static final Logger LOGGER = Logger.getLogger(FileUpdateService.class.getName());

    private final Object lock;
    private Person person;
    private long time;
    private boolean infected;

    public FileUpdateService(Object lock, Person person, long time, boolean infected) {
        this.lock = lock;
        this.person = person;
        this.time = time;
        this.infected = infected;

        if (LOGGER.getHandlers() == null || LOGGER.getHandlers().length == 0)
            Utils.createLoggerHandler(LOGGER);
    }

    @Override
    public void run() {

        synchronized (lock) {

            try {

                ObjectInputStream inputStream =
                        new ObjectInputStream(
                                new FileInputStream(Constants.FILE_PATH_FIRST_AID_DATA + Constants.DATA_FILE_NAME));

                Data data = (Data) inputStream.readObject();

                inputStream.close();

                PersonCounter personCounter = data.getMeasurementAtTime().get(time);

                if (personCounter == null)
                    personCounter = new PersonCounter();

                if (person != null) {
                    if (infected) {

                        data.incrementInfected();
                        data.getInfectedIds().add(person.getId());

                        if (person.getGender() == Person.Gender.MALE) {
                            data.incrementInfectedMale();
                            personCounter.incrementInfectedMale();
                        } else {
                            data.incrementInfectedFemale();
                            personCounter.incrementInfectedFemale();
                        }

                        if (person instanceof Child) {
                            data.incrementInfectedChild();
                            personCounter.incrementInfectedChild();
                        } else if (person instanceof Adult) {
                            data.incrementInfectedAdult();
                            personCounter.incrementInfectedAdult();
                        } else {
                            data.incrementInfectedOld();
                            personCounter.incrementInfectedOld();
                        }

                    } else {

                        data.incrementRecovered();
                        data.getRecoveredIds().add(person.getId());

                        if (person.getGender() == Person.Gender.MALE) {
                            data.incrementRecoveredMale();
                            personCounter.incrementRecoveredMale();
                        } else {
                            data.incrementRecoveredFemale();
                            personCounter.incrementRecoveredFemale();
                        }

                        if (person instanceof Child) {
                            data.incrementRecoveredChild();
                            personCounter.incrementRecoveredChild();
                        } else if (person instanceof Adult) {
                            data.incrementRecoveredAdult();
                            personCounter.incrementRecoveredAdult();
                        } else {
                            data.incrementRecoveredOld();
                            personCounter.incrementRecoveredOld();
                        }
                    }
                }

                data.addMeasurementAtTime(time, personCounter);

                ObjectOutputStream outputStream =
                        new ObjectOutputStream(
                                new FileOutputStream(Constants.FILE_PATH_FIRST_AID_DATA + Constants.DATA_FILE_NAME, false));

                outputStream.writeObject(data);
                outputStream.flush();
                outputStream.close();

            } catch (Exception ex) {
                LOGGER.warning(ex.fillInStackTrace().toString());
            }
        }
    }
}
