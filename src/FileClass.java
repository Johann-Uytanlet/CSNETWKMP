import java.io.File;
import java.io.Serializable;

public class FileClass implements Serializable {
    private File file;
    private String uploader;

    public FileClass(File file, String uploader) {
        this.file = file;
        this.uploader = uploader;
    }

    public File getFile() {
        return file;
    }

    public String getUploader() {
        return uploader;
    }

    @Override
    public String toString() {
        return "FileClass{" + file + "/" + uploader + '}';
    }
}
