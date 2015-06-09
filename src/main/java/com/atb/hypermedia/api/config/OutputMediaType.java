package com.atb.hypermedia.api.config;

import org.springframework.http.MediaType;

public class OutputMediaType {

    public static final String APPLICATION_HAL_JSON_VALUE = "application/hal+json;charset=UTF-8";
    public static final MediaType APPLICATION_HAL_JSON = MediaType.parseMediaType(APPLICATION_HAL_JSON_VALUE);

    public static final String APPLICATION_PROTOBUF_VALUE = "application/x-protobuf";
    public static final MediaType APPLICATION_PROTOBUF = MediaType.parseMediaType(APPLICATION_PROTOBUF_VALUE);

    public static final String APPLICATION_TEXT_PROTOBUF_VALUE = "text/plain;charset=UTF-8";
    public static final MediaType APPLICATION_TEXT_PROTOBUF = MediaType.parseMediaType(APPLICATION_TEXT_PROTOBUF_VALUE);

    // Alias the Spring XHTML MediaType so we don't have to import two MediaType classes
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml;charset=UTF-8";
    public static final MediaType APPLICATION_XHTML_XML = MediaType.parseMediaType(APPLICATION_XHTML_XML_VALUE);

    public static final String APPLICATION_MPEG_URL_VALUE = "application/x-mpegURL";
    public static final MediaType APPLICATION_MPEG_URL = MediaType.parseMediaType(APPLICATION_MPEG_URL_VALUE);

    public static final String APPLICATION_HLS_VALUE = APPLICATION_MPEG_URL_VALUE;
    public static final MediaType APPLICATION_HLS = MediaType.parseMediaType(APPLICATION_HLS_VALUE);

    public static final String APPLICATION_HDS_XML_VALUE = "application/f4m+xml";
    public static final MediaType APPLICATION_HDS_XML = MediaType.parseMediaType(APPLICATION_HDS_XML_VALUE);

    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final MediaType APPLICATION_OCTET_STREAM = MediaType.parseMediaType(APPLICATION_OCTET_STREAM_VALUE);

    public static final String TEXT_CSV_VALUE = "text/csv";
    public static final MediaType TEXT_CSV = MediaType.parseMediaType(TEXT_CSV_VALUE);

    public static final String APPLICATION_JSON_VALUE = "application/json;charset=UTF-8";
    public static final MediaType APPLICATION_JSON = MediaType.parseMediaType(APPLICATION_JSON_VALUE);

    public static final String APPLICATION_TEXT_PLAIN_VALUE = "text/plain;charset=UTF-8";
    public static final MediaType APPLICATION_TEXT_PLAIN = MediaType.parseMediaType(APPLICATION_TEXT_PLAIN_VALUE);

    private OutputMediaType(){}
}
