package org.glotaran.core.models.structures;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Sergey
 */
abstract public class BaseStructure {

    public final void fread(ImageInputStream f) throws IOException, IllegalAccessException, InstantiationException {
        Field[] fields = getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
//            System.out.println(f.getStreamPosition());
            Field fl = fields[i];
            Class t = fl.getType();
            if (t.equals(long.class)) { //temporary workaround; more permanent solution would be to define a base structure for every binary data type
                if (getClass().getName().substring(0, 41).compareTo("org.glotaran.sdtdataloader.sdtstructures.") == 0) {
                    fl.set(this, f.readUnsignedInt()); //technically incorrect, in Java a long should be 8 bytes whereas readUnsignedInt() only reads 4 bytes
                } else {
                    fl.set(this, f.readLong());
                }
            } else if (t.equals(int.class)) {
                fl.set(this, f.readInt());
            } else if (t.equals(short.class)) {
                fl.set(this, f.readShort());
            } else if (t.equals(float.class)) {
                fl.set(this, f.readFloat());
            } else if (t.equals(byte.class)) {
                fl.set(this, f.readByte());
            } else if (t.equals(char.class)) {
                fl.set(this, f.readChar());
            } else if (t.equals(double.class)) {
                fl.set(this, f.readDouble());
            } else if (t.equals(byte[].class)) {
                byte[] temp = (byte[]) fl.get(this);
                f.readFully(temp, 0, temp.length);
                fl.set(this, temp);
            } else if (BaseStructure.class.isAssignableFrom(t)) {
                BaseStructure inner = (BaseStructure) t.newInstance();
                inner.fread(f);
                fl.set(this, inner);
            }
        }
    }

    public final long readUInt32(ImageInputStream f) throws IOException {
        byte[] b = new byte[4];
        f.read(b, 0, 4);
        ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getLong();
    }
}
