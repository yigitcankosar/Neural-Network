package MnistDb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public abstract class MnistDb extends RandomAccessFile {
    private int count;
    

    public MnistDb(String name, String mode) throws IOException {
        super(name, mode);
        if (getMagic () != readInt()) {
            throw new RuntimeException(" MNIST file " + name + "  start number " + getMagic () + ".");
        }
        count = readInt();
    }

     abstract int getMagic ();

 
    public long getCurrent() throws IOException {
        return (getFilePointer() - getHeaderSize()) / getEntryLength() + 1;
    }

    public void setCurrentIndex(long curr) {
        try {
            if (curr < 0 || curr > count) {
                throw new RuntimeException(curr + " " + count);
            }
            seek(getHeaderSize() + (curr - 1) * getEntryLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHeaderSize() {
        return 8; 
    }

    public int getEntryLength() {
        return 1;
    }

    public void next() throws IOException {
        if (getCurrent() < count) {
            skipBytes(getEntryLength());
        }
    }

    public void prev() throws IOException {
        if (getCurrent() > 0) {
            seek(getFilePointer() - getEntryLength());
        }
    }

    public int getCount() {
        return count;
    }
}


