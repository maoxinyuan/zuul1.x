package com.pacific.apigetway.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import java.io.*;
import java.nio.charset.Charset;

/**
 * @description:
 * @author: yekai
 * @create: 2019-11-28 16:41
 * @since:
 **/
public class TwStreamUtils{

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return new String(sb.toString().getBytes(charset));
    }
}
