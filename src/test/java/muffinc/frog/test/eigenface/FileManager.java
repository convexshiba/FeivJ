package muffinc.frog.test.eigenface;

import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * FROG, a Face Recognition Gallery in Java
 * Copyright (C) 2015 Jun Zhou
 * <p/>
 * This file is part of FROG.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * zj45499 (at) gmail (dot) com
 */
public class FileManager {

    public static class ImageSearcher extends SimpleFileVisitor<Path> {
        private final PathMatcher matcher;
        private ArrayList<Path> files = new ArrayList<Path>();
        private int counter;
//        private Path dest = Paths.get("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/copy/");

        ImageSearcher(String pattern) {
            matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            counter = 0;
        }

        void find(Path file) {
            Path name = file.getFileName();
            if (matcher.matches(name)) {
                System.out.println("Matched" + name);
                files.add(file);
                counter++;
            }
        }

        void done() {
            System.out.println(counter);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            find(file);
            return FileVisitResult.CONTINUE;
        }

        public List<Path> getPaths() {
            return files;
        }

    }

    public void searchForImage(String directory) {
        final Path rootDir = Paths.get(directory);
//        final Path
    }

    public static SimpleMatrix convertIMGtoMatrix(String address) throws IOException {
        BufferedImage img = ImageIO.read(new File(address));
        byte[] imageBytes = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();


        FileInputStream fileInputStream = new FileInputStream(address);
        Scanner scan = new Scanner(fileInputStream);

        // Discard the magic number
        scan.nextLine();
        // Read pic width, height and max value
        int picWidth = scan.nextInt();
        int picHeight = scan.nextInt();
        int maxvalue = scan.nextInt();

        fileInputStream.close();

        // Now parse the file as binary data
        fileInputStream = new FileInputStream(address);
        DataInputStream dis = new DataInputStream(fileInputStream);

        // look for 4 lines (i.e.: the header) and discard them
        int numnewlines = 3;
        while (numnewlines > 0) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
            } while (c != '\n');
            numnewlines--;
        }

        // read the image data
        double[][] data2D = new double[picHeight][picWidth];
        for (int row = 0; row < picHeight; row++) {
            for (int col = 0; col < picWidth; col++) {
                data2D[row][col] = dis.readUnsignedByte();
            }
        }

        return new SimpleMatrix(data2D);
    }

    // Convert Matrix to PGM with numbers of row and column
    public static SimpleMatrix normalize(SimpleMatrix input){
        int row = input.numRows();

        for(int i = 0; i < row; i ++){
            input.set(i, 0, 0-input.get(i, 0));

        }

        double max = input.get(0, 0);
        double min = input.get(0, 0);

        for(int i = 1; i < row; i ++){
            if(max < input.get(i,0))
                max = input.get(i, 0);

            if(min > input.get(i, 0))
                min = input.get(i, 0);

        }

        SimpleMatrix result = new SimpleMatrix(112,92);
        for(int p = 0; p < 92; p ++){
            for(int q = 0; q < 112; q ++){
                double value = input.get(p*112+q, 0);
                value = (value - min) *255 /(max - min);
                result.set(q, p, value);
            }
        }

        return result;

    }

    //convert matrices to images
    public static void convertMatricetoImage(SimpleMatrix x, int featureMode) throws IOException{
        int row = x.numRows();
        int column = x.numCols();

        for(int i = 0; i < column; i ++){
            SimpleMatrix eigen = normalize(x.extractVector(false, i));


            BufferedImage img = new BufferedImage(92,112,BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = img.getRaster();

            for(int m = 0; m < 112; m ++ ){
                for(int n = 0; n < 92; n ++){
                    int value = (int)eigen.get(m, n);
                    raster.setSample(n,m,0,value);
                }
            }

            File file = null;
            if(featureMode == 0)
                file = new File("Eigenface"+i+".bmp");
            else if(featureMode == 1)
                file = new File("Fisherface"+i+".bmp");
            else if(featureMode == 2)
                file = new File("Laplacianface"+i+".bmp");

            if(!file.exists())
                file.createNewFile();

            ImageIO.write(img,"bmp",file);
        }

    }


    //convert single matrix to an image
    public static void convertToImage(SimpleMatrix input, int name) throws IOException{
        File file = new File(name+" dimensions.bmp");
        if(!file.exists())
            file.createNewFile();

        BufferedImage img = new BufferedImage(92,112,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();

        for(int m = 0; m < 112; m ++ ){
            for(int n = 0; n < 92; n ++){
                int value = (int)input.get(n*112+m, 0);
                raster.setSample(n,m,0,value);
            }
        }

        ImageIO.write(img,"bmp",file);

    }

    public static void main(String[] args) {
        try {
            SimpleMatrix a = convertIMGtoMatrix("/Users/Meth/Documents/FROG/src/test/faces/s1/1.pgm");
            normalize(a.extractVector(false, 0)).print();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
