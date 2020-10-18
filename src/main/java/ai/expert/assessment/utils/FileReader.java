package ai.expert.assessment.utils;

import java.io.File;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author oserret
 */
public class FileReader {

    private static final Logger FILE_READER_LOGGER = Logger.getLogger(FileReader.class);
    private ArrayList<File> files = new ArrayList<>();

    public FileReader() {
       
    }

    public void readFiles(String inputFolder) {

        File folder = new File(inputFolder);
        File[] listOfFiles = null;

        listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                FILE_READER_LOGGER.info("\t\tReading file: " + file.getName());
                getFiles().add(file);
            } else {
                FILE_READER_LOGGER.info("\t\tBegin directory reading: " + file.getPath());
                readFiles(file.getPath());
            }
        }
    }

    /**
     * @return the files
     */
    public ArrayList<File> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

}
