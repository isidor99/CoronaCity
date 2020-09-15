package org.etf.coronacity.helper;

/*
    This is helper class
    Contains various constant values used in the application
 */
public class Constants {

    // file paths
    public static final String FILE_PATH_MALE_NAMES = "./res/male_names.txt";
    public static final String FILE_PATH_FEMALE_NAMES = "./res/female_names.txt";
    public static final String FILE_PATH_SURNAMES = "./res/surnames.txt";
    public static final String FILE_PATH_HOSPITAL_NAMES = "./res/hospital_names.txt";
    public static final String FILE_PATH_STATISTICS = "./res/stats.csv";
    public static final String FILE_PATH_LOG_FILE = "./res/logs/logs.log";
    public static final String FILE_PATH_DATA = "./res/data/";
    public static final String FILE_PATH_SIMULATION_DATA = "./res/simulation_data/";
    public static final String FILE_PATH_SERIALIZATION_DATA = "./res/serialization/data.jcs"; // java cov simulation

    public static final String DATA_FILE_NAME = "data.fad";

    public static final String SIMULATION_DATA_FILE_NAME_PREFIX = "SIM-JavaKov-20-";

    public static final String STATISTICS_FILE_INFECTED_CSV = "./res/stats/infected.csv";
    public static final String STATISTICS_FILE_CURRENTLY_INFECTED_CSV = "./res/stats/currently_infected.csv";
    public static final String STATISTICS_FILE_RECOVERED_CSV = "./res/stats/recovered.csv";

    // Ages
    public static final int AGE_CHILD_LOW = 0;
    public static final int AGE_CHILD_HIGH = 17;
    public static final int AGE_ADULT_LOW = 18;
    public static final int AGE_ADULT_HIGH = 64;
    public static final int AGE_OLD_LOW = 65;
    public static final int AGE_OLD_HIGH = 100;


    // Temperature
    public static final double TEMPERATURE_LOWEST = 35.0;
    public static final double TEMPERATURE_LOW = 36.0;
    public static final double TEMPERATURE_NORMAL = 37.0;
    public static final double TEMPERATURE_HIGHEST = 40.0;

    // Old movement range
    public static final int OLD_MOVEMENT_RANGE = 3;


    // First Aid File Extension
    public static final String EXTENSION_FIRST_AID_DATA = ".fad";
    public static final String EXTENSION_TEXT_FILE = ".txt";

    // message
    public static final String ERROR_MESSAGE = "Greska";
    public static final String NOTIFICATION_MESSAGE = "Obavjestenje";

    // font
    public static final String DEFAULT_FONT = "Dialog";

    // HashMap key for data
    public static final String KEY_MATRIX_SIZE = "matrix_size";
    public static final String KEY_NUM_OF_CHILDREN = "num_of_children";
    public static final String KEY_NUM_OF_ADULTS = "num_of_adults";
    public static final String KEY_NUM_OF_OLD = "num_of_old";
    public static final String KEY_NUM_OF_CHECKPOINTS = "num_of_checkpoints";
    public static final String KEY_NUM_OF_HOMES = "num_of_homes";
    public static final String KEY_NUM_OF_AMBULANCES = "num_of_ambulances";
    public static final String KEY_CAPACITY = "capacity";

    // test for now
    public static final long PERSON_THREAD_SLEEP_TIME = 500;
    public static final long AMBULANCE_THREAD_SLEEP_TIME = 200;

}
