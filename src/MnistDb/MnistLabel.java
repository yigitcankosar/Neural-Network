package MnistDb;

import java.io.FileNotFoundException;
import java.io.IOException;



public class MnistLabel extends MnistDb {

    public MnistLabel(String name, String way) throws IOException {
        super(name, way);
    }

 
    public int readLabel() throws IOException {
        return readByte();
    }

    public int[] readLabels(int num) throws IOException {
        int[] out = new int[num];
        for( int i=0; i<num; i++ ) out[i] = readLabel();
        return out;
    }

    @Override
    protected int getMagic() {
        return 2049;
    }
}