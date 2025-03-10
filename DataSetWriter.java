
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataSetWriter {
    private final Map<String,FileOutputStream> streams;
    private final String path;
    public DataSetWriter(String path) {
        this.path = path;
        this.streams = new HashMap<>();
    }
    public void open(String dataSet,boolean append) throws IOException{
        close(dataSet);
        streams.put(dataSet, new FileOutputStream(path+"/"+dataSet,append));
    }
    public void write(String dataSet,boolean t, int ...v) throws IOException{
        FileOutputStream fos = streams.get(dataSet);
        for(int num:v) {
            fos.write((byte)(num & 0xff));
            fos.write((byte)((num >> 8) & 0xff));
        }
        fos.write((byte) (t?1:0));
    }
    public void close(String dataSet) throws IOException{
        if(streams.containsKey(dataSet)) {
            streams.get(dataSet).flush();
            streams.get(dataSet).close();
            streams.remove(dataSet);
        }
    }
    public void closeAll() throws IOException{
        for(String dataSet: streams.keySet()) {
            close(dataSet);
        }
    }

    public void openAll(boolean append, String ...dataSets) throws IOException {
        for(String dataSet: dataSets) {
            open(dataSet, append);
        }
    }
    public void openAll( String ...dataSets) throws IOException {openAll(false,dataSets);}
}
