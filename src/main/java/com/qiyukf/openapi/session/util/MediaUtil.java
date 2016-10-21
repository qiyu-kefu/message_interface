package com.qiyukf.openapi.session.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaUtil {

    /**
     * 计算图片宽高的简单代码，仅支持主流的文件格式
     */
    public static int[] querySize(String path) throws IOException {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(path);
            return querySize(fis);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int[] querySize(InputStream is) throws IOException {
        int c1 = is.read();
        int c2 = is.read();
        int c3 = is.read();
        int width = 0;
        int height = 0;
        if (c1 == 'G' && c2 == 'I' && c3 == 'F') { // GIF
            is.skip(3);
            width = readInt(is,2,false);
            height = readInt(is,2,false);
        } else if (c1 == 0xFF && c2 == 0xD8) { // JPG
            while (c3 == 255) {
                int marker = is.read();
                int len = readInt(is,2,true);
                if (marker == 192 || marker == 193 || marker == 194) {
                    is.skip(1);
                    width = readInt(is,2,true);
                    height = readInt(is,2,true);
                    break;
                }
                is.skip(len - 2);
                c3 = is.read();
            }
        } else if (c1 == 137 && c2 == 80 && c3 == 78) { // PNG
            is.skip(15);
            width = readInt(is,2,true);
            is.skip(2);
            height = readInt(is,2,true);
        } else if (c1 == 66 && c2 == 77) { // BMP
            is.skip(15);
            width = readInt(is,2,false);
            is.skip(2);
            height = readInt(is,2,false);
        } else {
            int c4 = is.read();
            if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42)
                    || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0)) { //TIFF
                boolean bigEndian = c1 == 'M';
                int ifd = 0;
                int entries;
                ifd = readInt(is,4,bigEndian);
                is.skip(ifd - 8);
                entries = readInt(is,2,bigEndian);
                for (int i = 1; i <= entries; i++) {
                    int tag = readInt(is,2,bigEndian);
                    int fieldType = readInt(is,2,bigEndian);
                    int valOffset;
                    if ((fieldType == 3 || fieldType == 8)) {
                        valOffset = readInt(is,2,bigEndian);
                        is.skip(2);
                    } else {
                        valOffset = readInt(is,4,bigEndian);
                    }
                    if (tag == 256) {
                        width = valOffset;
                    } else if (tag == 257) {
                        height = valOffset;
                    }
                    if (width != -1 && height != -1) {
                        break;
                    }
                }
            }
        }
        return new int[] {width, height};
    }

    /**
     * 读取amr数据流的音频长度
     * @param data 数据
     * @return 音频长度，单位为ms
     */
    public static long queryAmrDuration(byte[] data) {
        int[] frameSize = { 13, 14, 16, 18, 20, 21, 27, 32, 6, 1, 1, 1, 1, 1, 1, 1 };

        int pos = 6; // 跳过文件头
        int frameCount = 0;
        int codecMode = -1;

        while (pos < data.length) {
            codecMode = (data[pos] >> 3) & 0x0F;
            pos += frameSize[codecMode];
            frameCount++;
        }

        return frameCount * 20;// 帧数*20
    }

    public static long queryAudioDuration(String path) throws IOException {
        return queryAudioDurationInner(path);
    }

    public static long queryAudioDuration(InputStream is) throws IOException {
        return queryAudioDurationInner(is);
    }

    /**
     * 这个接口只有在系统支持的格式下，才会有返回
     * @param input 输入，可以是文件路径或者InputStream
     * @return 音频长度，单位ms
     * @throws IOException
     */
    private static long queryAudioDurationInner(Object input) throws IOException {
        AudioInputStream ais = null;
        try {
            Clip clip = AudioSystem.getClip();
            if (input instanceof InputStream) {
                ais = AudioSystem.getAudioInputStream((InputStream) input);
            } else if (input instanceof File) {
                ais = AudioSystem.getAudioInputStream((File) input);
            }
            clip.open(ais);
            return clip.getMicrosecondLength();
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            return 0;
        }  finally {
            if (ais != null) {
                try {
                    ais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
        int cnt = bigEndian ? -8 : 8;
        for(int i=0;i<noOfBytes;i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }
}