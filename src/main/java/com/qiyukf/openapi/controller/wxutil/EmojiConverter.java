package com.qiyukf.openapi.controller.wxutil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhoujianghua on 2016/7/20.
 */
@Component
public class EmojiConverter {

    private Map<String, String> nim2Wx = new HashMap<>();
    private Map<String, String> wx2Nim = new HashMap<>();

    private boolean inited = false;

    private Pattern nimPattern = Pattern.compile("\\[[^\\[\\]]{1,10}\\]");

    private Set<Character> patternKeys = new HashSet<>();
    private Pattern wxPattern;

    public String convertWx(String message) {
        if (setup() && message != null) {
            return convert(message, wxPattern, wx2Nim);
        } else {
            return message;
        }
    }

    public String convertNim(String message) {
        if (setup() && message != null) {
            return convert(message, nimPattern, nim2Wx);
        } else {
            return message;
        }
    }

    private String convert(String message, Pattern pattern, Map<String, String> mapping) {
        Matcher matcher = pattern.matcher(message);
        int offset = 0;

        StringBuilder result = null;
        while (matcher.find()) {
            if (result == null) {
                result = new StringBuilder(message);
            }
            int start = matcher.start();
            int end = matcher.end();
            String src = message.substring(start, end);
            String target = mapping.get(src);
            if (target != null) {
                result.replace(start + offset, end + offset, target);
                offset += (target.length() - src.length());
            }
        }
        return result == null ? message : result.toString();
    }

    private boolean setup() {
        if (inited) {
            return true;
        }

        final char[] keys = {'|', '(', ')', '[', ']', '{', '}', '<', '>' , '.', '*', '\\', '^', '$', '+', '-', ',', '?', '='};
        for (char key : keys) {
            patternKeys.add(key);
        }

        try {
            StringBuilder wxEmojis = new StringBuilder();
            File file = ResourceUtils.getFile("classpath:config/emoji_mapping.json"); // new File("e:/emoji_mapping.json"); //
            String content = FileUtils.readFileToString(file, Charset.forName("utf-8"));
            JSONArray pairs = JSONArray.parseArray(content);
            for (int i = 0; i < pairs.size(); ++i) {
                JSONObject item = pairs.getJSONObject(i);
                String nim = item.getString("nim");
                String wx = item.getString("wx");

                // 微信的表情没有停止符，做全匹配比较好
                if (i > 0) {
                    wxEmojis.append('|');
                }
                wxEmojis.append(escapePattern(wx));

                // 存入map
                if (nim.startsWith("[")) {
                    nim2Wx.put(nim, wx);
                }
                wx2Nim.put(wx, nim);
            }
            wxPattern = Pattern.compile(wxEmojis.toString(), Pattern.UNICODE_CASE);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        inited = true;
        return true;
    }

    private String escapePattern(String emoji) {
        StringBuilder sb = new StringBuilder(emoji);
        int offset = 0;
        for (int i = 0; i < emoji.length(); ++i) {
            if (patternKeys.contains(emoji.charAt(i))) {
                sb.insert(i + offset, "\\");
                offset += 1;
            }
        }
        return offset == 0 ? emoji : sb.toString();
    }

    public static void main(String[] args) {
        EmojiConverter converter = new EmojiConverter();
        String wxMessage = "fadfad/::Dfdfert/::~/::|-----/:81-   /:,@-D" + "\ue412" + "wx: \uD83D\uDE14" + ", \u0001\uF633 ";
        String nimMessage = converter.convertWx(wxMessage);
        System.out.println(nimMessage);
        wxMessage = converter.convertNim(nimMessage);
        System.out.println(wxMessage);
    }
}
