package org.jboss.logmanager.handlers;

/**
 *
 * @author Haranath
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ErrorManager;
import org.jboss.logging.MDC;
import org.jboss.logmanager.ExtLogRecord;

public class CustomSizeRotatingFileHandler extends SizeRotatingFileHandler {

    private File internalFile;
    private String basePath;
    private String mdcKey;
    private String suffixFileName = "server.log";
    private final Map<String, File> fileGroup = new ConcurrentHashMap<>();

    /**
     * Construct a new instance with no formatter and no output file.
     */
    public CustomSizeRotatingFileHandler() {
    }

    /**
     * Construct a new instance with the given output file.
     *
     * @param file the file
     *
     * @throws java.io.FileNotFoundException if the file could not be found on
     * open
     */
    public CustomSizeRotatingFileHandler(final File file) throws FileNotFoundException {
        super(file);
        this.internalFile = file;
    }

    /**
     * Construct a new instance with the given output file and append setting.
     *
     * @param file the file
     * @param append {@code true} to append, {@code false} to overwrite
     *
     * @throws java.io.FileNotFoundException if the file could not be found on
     * open
     */
    public CustomSizeRotatingFileHandler(final File file, final boolean append) throws FileNotFoundException {
        super(file, append);
        this.internalFile = file;
    }

    /**
     * Construct a new instance with the given output file.
     *
     * @param fileName the file name
     *
     * @throws java.io.FileNotFoundException if the file could not be found on
     * open
     */
    public CustomSizeRotatingFileHandler(final String fileName) throws FileNotFoundException {
        super(fileName);
        this.internalFile = fileName == null ? null : new File(fileName);
    }

    /**
     * Construct a new instance with the given output file and append setting.
     *
     * @param fileName the file name
     * @param append {@code true} to append, {@code false} to overwrite
     *
     * @throws java.io.FileNotFoundException if the file could not be found on
     * open
     */
    public CustomSizeRotatingFileHandler(final String fileName, final boolean append) throws FileNotFoundException {
        super(fileName, append);
        this.internalFile = fileName == null ? null : new File(fileName);
    }

    /**
     * Construct a new instance with no formatter and no output file.
     * @param rotateSize
     * @param maxBackupIndex
     */
    public CustomSizeRotatingFileHandler(final long rotateSize, final int maxBackupIndex) {
        super(rotateSize, maxBackupIndex);
    }

    /**
     * Construct a new instance with the given output file.
     *
     * @param file the file
     * @param rotateSize
     * @param maxBackupIndex
     *
     * @throws java.io.FileNotFoundException if the file could not be found on
     * open
     */
    public CustomSizeRotatingFileHandler(final File file, final long rotateSize, final int maxBackupIndex) throws FileNotFoundException {
        super(file, rotateSize, maxBackupIndex);
        this.internalFile = file;
    }

    /**
     * Construct a new instance with the given output file and append setting.
     *
     * @param file the file
     * @param append {@code true} to append, {@code false} to overwrite
     *
     * @throws java.io.FileNotFoundException if the file could not be found on
     * open
     */
    public CustomSizeRotatingFileHandler(final File file, final boolean append, final long rotateSize, final int maxBackupIndex) throws FileNotFoundException {
        super(file, append, rotateSize, maxBackupIndex);
        this.internalFile = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFile(File file) throws FileNotFoundException {
        this.internalFile = file;
        super.setFile(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preWrite(final ExtLogRecord record) {
        File file = getFile();

        try {
            super.setFile(file);
        } catch (FileNotFoundException ex) {
            reportError("Unable to rotate log file", ex, ErrorManager.OPEN_FAILURE);

        }
        super.preWrite(record);

    }

    /**
     * Get the base path of the file
     *
     * @return base of the file
     */
    public String getBasePath() {
        if(this.basePath == null || this.basePath.trim().isEmpty()){
            this.basePath = getWorkingDir();
        }
        return basePath;
    }

    /**
     * Set the output file base path
     *
     * @param basePath the base path of logger file
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Get the Mapped Diagnostic Context (MDC) key
     *
     * @return MDC key
     */
    public String getMdcKey() {
        return mdcKey;
    }

    /**
     * Set the Mapped Diagnostic Context (MDC) key name
     *
     * @param mdcKey Mapped Diagnostic Context (MDC) key name
     */
    public void setMdcKey(String mdcKey) {
        this.mdcKey = mdcKey;
    }

    /**
     * Get the suffix file name.
     *
     * @return suffix file name
     */
    public String getSuffixFileName() {
        return suffixFileName;
    }

    /**
     * Sets the suffix file name for the log file
     *
     * @param suffixFileName the suffix file name
     */
    public void setSuffixFileName(String suffixFileName) {
        this.suffixFileName = suffixFileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
        synchronized (outputLock) {
            if (MDC.get(mdcKey) != null) {
                return getFileGroup(MDC.get(mdcKey) + "");
            }
            return internalFile;
        }
    }

    private File getFileGroup(String key) {

        if (!fileGroup.containsKey(key)) {
            fileGroup.put(key, new File(basePath + File.separator + MDC.get(mdcKey) + suffixFileName));
        }
        return fileGroup.get(key);
    }

    private String getWorkingDir() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
