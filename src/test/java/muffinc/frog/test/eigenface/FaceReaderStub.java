package muffinc.frog.test.eigenface;

import muffinc.frog.test.displayio.Display;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaceReaderStub {

    public static final int ROWS = 384;
    public static final int COLS = 256;

    public static List<BufferedImage> readFile(String folderDir) {
        ArrayList<BufferedImage> files = new ArrayList<BufferedImage>();

        File dir = new File(folderDir);
        try {
            if (dir.isDirectory()) {
                for (File fileEntry : dir.listFiles()) {
                    if (!fileEntry.isHidden()) {
                        files.add(ImageIO.read(fileEntry));
                    }
                }
            } else if (dir.isFile()) {
                files.add(ImageIO.read(dir));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

//    public static byte[] toByteArray(double[] doubleArray){
//        int times = Double.SIZE / Byte.SIZE;
//        byte[] bytes = new byte[doubleArray.length * times];
//        for(int i=0;i<doubleArray.length;i++){
//            ByteBuffer.wrap(bytes, i*times, times).putDouble(doubleArray[i]);
//        }
//        return bytes;
//    }
//
//    public static double[] toDoubleArray(byte[] byteArray){
//        int times = Double.SIZE / Byte.SIZE;
//        double[] doubles = new double[byteArray.length / times];
//        for(int i=0;i<doubles.length;i++){
//            doubles[i] = ByteBuffer.wrap(byteArray, i*times, times).getDouble();
//        }
//        return doubles;
//    }
//
//    public static byte[] toByteArray(int[] intArray){
//        int times = Integer.SIZE / Byte.SIZE;
//        byte[] bytes = new byte[intArray.length * times];
//        for(int i=0;i<intArray.length;i++){
//            ByteBuffer.wrap(bytes, i*times, times).putInt(intArray[i]);
//        }
//        return bytes;
//    }
//
//    public static int[] toIntArray(byte[] byteArray){
//        int times = Integer.SIZE / Byte.SIZE;
//        int[] ints = new int[byteArray.length / times];
//        for(int i=0;i<ints.length;i++){
//            ints[i] = ByteBuffer.wrap(byteArray, i*times, times).getInt();
//        }
//        return ints;
//    }

    public static double[] toDoubleArray(byte[] bytes) {
        double[] doubles = new double[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            doubles[i] = (double) bytes[i];
        }
        return doubles;
    }

    public static byte[] toByteArray(double[] doubles) {
        byte[] bytes = new byte[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            bytes[i] = (byte) doubles[i];
        }
        return bytes;
    }

    public static void main(String[] args) {

        DenseMatrix64F faceMatrix = new DenseMatrix64F(ROWS, COLS);

        List<BufferedImage> images = readFile("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/FERET1");


        for (BufferedImage foo : images) {
            DenseMatrix64F fooMatrix = new DenseMatrix64F(ROWS, COLS);

            System.arraycopy(toDoubleArray(faceTo1D(foo)), 0, fooMatrix.getData(), 0, faceTo1D(foo).length);

            CommonOps.add(faceMatrix, fooMatrix, faceMatrix);
            System.out.println(fooMatrix.get(150,150));
        }
        CommonOps.divide(faceMatrix, images.size());
        System.out.println(faceMatrix.get(150,150));

        List<BufferedImage> output = readFile("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/testtesttest/output.tif");
        System.arraycopy(toByteArray(faceMatrix.getData()), 0, faceTo1D(output.get(0)), 0, faceMatrix.getData().length);

        Display.display(output.get(0));


//        try {
//            ImageIO.write(output.get(0), "tiff", new File("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/output.tiff"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static byte[] faceTo1D(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    public static double[][] faceTo2D(BufferedImage image) {
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        double[][] result = new double[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }
}
