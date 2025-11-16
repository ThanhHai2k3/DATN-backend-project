package com.backend.cv_service.util;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

public class CvTextExtractor {

    public static String extractText(InputStream inputStream, String fileName) throws Exception {
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        if (fileName != null) {
            metadata.set("resourceName", fileName);
        }

        // -1 = không giới hạn số ký tự
        ContentHandler handler = new BodyContentHandler(-1);
        parser.parse(inputStream, handler, metadata);
        return handler.toString();
    }
}
