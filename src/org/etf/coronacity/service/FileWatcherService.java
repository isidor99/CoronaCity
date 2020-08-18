package org.etf.coronacity.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import org.etf.coronacity.helper.Constants;
import org.etf.coronacity.helper.Utils;
import org.etf.coronacity.model.carrier.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * This class is for monitoring file which keeps first aid data
 * All job is done in method watch()
 */

public class FileWatcherService extends Thread {

    private static final Logger LOGGER = Logger.getLogger(FileWatcherService.class.getName());

    private final Object lock;
    private Consumer<Data> fileChangeListener;

    public FileWatcherService(Object lock) {

        this.lock = lock;

        if (LOGGER.getHandlers() == null || LOGGER.getHandlers().length == 0)
            Utils.createLoggerHandler(LOGGER);
    }

    @Override
    public void run() {
        watch();
    }

    public void setFileChangeListener(Consumer<Data> fileChangeListener) {
        this.fileChangeListener = fileChangeListener;
    }

    private void watch() {

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Path directory = Paths.get(Constants.FILE_PATH_FIRST_AID_DATA);
            directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            while (true) {

                WatchKey watchKey;

                try {
                    watchKey = watchService.take();
                } catch (InterruptedException ex) {
                    LOGGER.warning(ex.fillInStackTrace().toString());
                    return;
                }

                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> path = (WatchEvent<Path>) event;

                    Path fileName = path.context();

                    if (fileName.toString().trim().endsWith(Constants.EXTENSION_FIRST_AID_DATA) &&
                            (kind.equals(ENTRY_MODIFY) || kind.equals(ENTRY_CREATE))) {

                        synchronized (lock) {

                            byte[] bytes = Files.readAllBytes(directory.resolve(fileName));

                            try {

                                ObjectInputStream inputStream =
                                        new ObjectInputStream(new ByteArrayInputStream(bytes));

                                Data data = (Data) inputStream.readObject();

                                inputStream.close();

                                // update UI
                                // total number of infected
                                fileChangeListener.accept(data);

                            } catch (Exception ex) {
                                LOGGER.warning(ex.fillInStackTrace().toString());
                            }
                        }
                    }
                }

                if (!watchKey.reset())
                    break;
            }

        } catch (IOException ex) {
            LOGGER.warning(ex.fillInStackTrace().toString());
        }
    }
}
