package com.cn.spring.util;

import static com.cn.spring.util.CollectionUtil.map;
import static com.cn.spring.util.StringUtil.EMPTY_STRING;
import static com.cn.spring.util.StringUtil.isBlank;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.getFilenameExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;


public class FileUtil {
    public static final String FILE_SEPERATOR = System.getProperty("file.separator");
    public static final String URL_SEPERATOR = "/";
    public static final char URL_SEPERATOR_CHAR = '/';
    public static final String UNIX_SEPERATOR = "/";
    public static final String WINDOWS_SEPERATOR = "\\";
    public static final String EXTENSION_SEPERATOR = ".";
    public static final String PACKAGE_SEPERATOR_STR = ".";
    public static final String XML_EXTENSION = "xml";
    public static final String JSON_EXTENSION = "json";
    public static final String VIEW_EXTENSION = "view";
    public static final String VIEW_FILE_EXTENSION = "." + VIEW_EXTENSION;
    public static final String DELIMITER_COMMA = ",";
    public static final FileExtentionFilter JSON_EXTENSION_FILTER = new FileExtentionFilter(false, "json");
    public static final FileExtentionFilter CASE_SENSITIVE_JSON_EXTENSION_FILTER = new FileExtentionFilter(false, "json");
    private static String mountPath = null;
    private Map<String, String> extensionContentTypeMap = new HashMap<>();// we could have use tikka library too; but that is heavy for simple content types

    private FileUtil() {
        extensionContentTypeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, String> contentTypeRawMap = (Map<String, String>) map("application/atom+xml", "atom", APPLICATION_JSON_VALUE, "json,jsonc,api", "application/msword", "doc", "application/pdf", "pdf", "application/postscript", "ai,eps,ps", "application/rss+xml", "rss", "application/vnd.ms-excel", "xls", "application/vnd.ms-powerpoint", "ppt", "application/xhtml+xml", "xhtml,xht", "text/javascript", "js", "application/xml", "xml,xsl", "application/xml-dtd", "dtd", "application/xslt+xml", "xslt", "application/x-tar", "tgz,tar", "application/zip", "zip", "audio/mpeg", "mpga,mp2,mp3", "audio/x-mpegurl", "m3u", "audio/x-wav", "wav", "image/bmp", "bmp", "image/gif", "gif", "image/jpeg", "jpeg,jpg,jpe", "image/png", "png", "image/svg+xml", "svg", "image/tiff", "tiff,tif", "image/x-icon", "ico", "text/calendar", "ics,ifb", "text/css", "css", "text/html", "shtml,html,htm,view,web,rhtml,rhtm", "text/plain", "asc,txt,log,csv", "text/richtext", "rtx", "text/rtf", "rtf", "text/sgml", "sgml,sgm", "text/tab-separated-values", "tsv", "video/mpeg", "mpeg,mpg,mpe", "video/quicktime", "qt,mov", "video/vnd.mpegurl", "mxu", "video/x-msvideo", "avi", "video/x-sgi-movie", "movie", "application/font-sfnt",
                "ttf,otf", "application/vnd.ms-fontobject", "eot", "application/font-woff", "woff", "application/font-woff2", "woff2");
        for (Entry<String, String> entry : contentTypeRawMap.entrySet()) {
            String contentType = entry.getKey().trim();
            String[] extensions = entry.getValue().trim().split(",");
            for (String extension : extensions)
                extensionContentTypeMap.put(extension.toLowerCase(), contentType);
        }
    }

    public static String extension(File file) throws Exception {
        return getFilenameExtension(file.getCanonicalPath());
    }

    public static String fileName(String filePath) {
        filePath = unixFormat(filePath);
        int index = filePath.lastIndexOf('/');
        if (index < 0) return filePath;
        return filePath.substring(index + 1);
    }

    public static String unixFormat(String path) {
        return path.replaceAll("\\\\", UNIX_SEPERATOR);
    }

    public static String relativePath(File file, File directory) throws Exception {
        return relativePath(file, directory, true);
    }

    public static String relativePath(File file, File directory, boolean includeExtension) throws Exception {
        String path = unixFormat(file.getCanonicalPath()).replaceFirst(unixFormat(directory.getCanonicalPath()), EMPTY_STRING);
        if (path.startsWith("/") || path.endsWith("\\")) path = path.substring(1);
        if (includeExtension) return path;
        return path.replaceFirst(EXTENSION_SEPERATOR + extension(file.getName()), EMPTY_STRING);
    }

    public static String extension(String path) {
        return getFilenameExtension(path);
    }

    public static String validatedUnixFormat(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            String filePath = unixFormat(file.getCanonicalPath());
            if (filePath.endsWith("/")) filePath = filePath.substring(0, filePath.length() - 2);
            return filePath;
        } catch (Exception e) {
            return null;
        }
    }

    public static String finalPathWithSeparator(String... paths) {
        String path = pathWithSeparator(paths[0]);
        for (int i = 1; i < paths.length; i++) {
            path = pathWithSeparator(path + paths[1]);
        }
        return path;
    }

    public static String pathWithSeparator(String path) {
        if (path.endsWith("/") || path.endsWith("\\")) return path;
        return path + FILE_SEPERATOR;
    }

    // Thread Safe getInstance() Method Implementation
    public static FileUtil getInstance() {
        return _instance.instance;
    }

    public static void appendStringToFile(String path, String contents) throws IOException {
        Files.write(Paths.get(path), contents.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static void writeStringToFile(String path, String contents) throws IOException {
        Files.write(Paths.get(path), contents.getBytes());
    }

    public static String pathWithoutSeparator(String path) {
        if (path.endsWith("/")) return path.substring(0, path.length() - 1);
        else if (path.endsWith("\\")) return path.substring(0, path.length() - 2);
        else return path;
    }

    public static String writeResourceToFile(String resourcePath, String targetPath) throws Exception {
        try (InputStream inStream = FileUtil.class.getResourceAsStream(resourcePath)) {
            try (FileOutputStream outStream = new FileOutputStream(targetPath)) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = inStream.read(buf)) > 0) {
                    outStream.write(buf, 0, bytesRead);
                }
            }
        }
        return targetPath;
    }

    public static String writeResourceToFile(String resourcePath, String targetPath, ServletContext container) throws Exception {
        try (InputStream inStream = container.getResourceAsStream(resourcePath)) {
            try (FileOutputStream outStream = new FileOutputStream(targetPath)) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = inStream.read(buf)) > 0) {
                    outStream.write(buf, 0, bytesRead);
                }
            }
        }
        return targetPath;
    }


    public static String mountPath(String setPath) {
        if (mountPath == null) mountPath = setPath;
        return mountPath;
    }

    public static String getParentFolder(String filePath) {
        if (filePath == null) return null;
        int separatorIndex = filePath.lastIndexOf(UNIX_SEPERATOR);
        if (separatorIndex <= -1) separatorIndex = filePath.lastIndexOf(WINDOWS_SEPERATOR);
        if (separatorIndex <= -1) return UNIX_SEPERATOR;
        return filePath.substring(0, separatorIndex);
    }

    public static FileExtentionFilter fileExtentionFilter(boolean caseSensitive, String... validExtensions) {
        return new FileExtentionFilter(caseSensitive, validExtensions);
    }

    private static class _instance {
        public static final FileUtil instance = new FileUtil();
    }

    public static final class FileExtentionFilter implements FilenameFilter {

        String[] validExtensions = null;
        boolean caseSensitive = false;

        public FileExtentionFilter(String... validExtensions) {
            this(false, validExtensions);
        }

        public FileExtentionFilter(boolean caseSensitive, String... validExtensions) {
            this.validExtensions = validExtensions;
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean accept(File dir, String name) {
            String extension = getFilenameExtension(name);
            if (isBlank(extension)) return false;
            for (String validExtension : validExtensions) {
                if (caseSensitive) {
                    if (validExtension.equalsIgnoreCase(extension)) return true;
                } else {
                    if (validExtension.equals(extension)) return true;
                }
            }
            return false;
        }
    }

}
