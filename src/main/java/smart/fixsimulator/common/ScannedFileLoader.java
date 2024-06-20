package smart.fixsimulator.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Desc:scan and load if file modified
 *
 * @author Leedeper
 */
@Slf4j
public class ScannedFileLoader {
    private String filePath;
    private int refreshInterval=5000;
    private FileTime lastModifiedTime;
    private String fileString;
    private ChangeListener listener;
    public ScannedFileLoader(String filePath){
        this(filePath,null);
    }

    public ScannedFileLoader(String filePath, ChangeListener listener){
        this.filePath = filePath;
        this.listener = listener;
        load();
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                load();
            }
        }, refreshInterval, refreshInterval);
    }
    public String get(){
        return fileString;
    }

    private void load(){
        Path path = Paths.get(filePath);
        try {
            FileTime lastTime = Files.getLastModifiedTime(path);
            if(lastModifiedTime==null){
                lastModifiedTime = lastTime;
                readFile(path);
            }else{
                if(lastTime.compareTo(lastModifiedTime)==0){
                    return;
                }
                lastModifiedTime = lastTime;
                readFile(path);
                if(listener!=null){
                    listener.changed(this.fileString);
                }
            }
        } catch (IOException e) {
            log.error("Can't load the file {}",filePath,e);
        }
    }
    private void readFile(Path path) throws IOException {
        fileString =new String(Files.readAllBytes(path));
        log.info("Load a file from {}, the content: {}",filePath, fileString);
    }
    public interface ChangeListener{
        void changed(String newStr);
    }
}
