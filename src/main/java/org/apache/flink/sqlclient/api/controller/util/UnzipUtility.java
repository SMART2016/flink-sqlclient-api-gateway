package org.apache.flink.sqlclient.api.controller.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class UnzipUtility {
    public static Map<String,InputStream> GetMapofEnvFiles(InputStream zipFile, List<String> validFileNames) throws IOException{
        ZipInputStream zipInputStream = new ZipInputStream(zipFile);
        ZipEntry zipEntry = null;

        Map<String,InputStream> inputStreams = new HashMap<>();

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            if (validFileNames.contains(entryName)) {
                inputStreams.put(entryName,convertToInputStream(zipInputStream));
            }
        }
        return inputStreams;
    }

    private static InputStream convertToInputStream(final ZipInputStream inputStreamIn) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(inputStreamIn, out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static List<String> GetValidEnvFileLstNames() {
        List<String> validFilesLst = new ArrayList<>();
        validFilesLst.add("flink-conf.yaml");
        return validFilesLst;
    }
}