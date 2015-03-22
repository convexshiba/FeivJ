package muffinc.frog.test.filereader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

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
public class FileSearch {

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




    public static void main(String[] args) throws IOException {

//        String pattern = "[0-9]{5}f(a|b)[0-9]{3}d";
        String pattern = "?????f????d*.tif";
//        String pattern = "123";

        Path searchDir = Paths.get("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/FERET1");

        ImageSearcher searcher = new ImageSearcher(pattern);
        Files.walkFileTree(searchDir, searcher);
        searcher.done();
    }

    public void searchForImage(String directory) {
        final Path rootDir = Paths.get(directory);
//        final Path


    }

}
