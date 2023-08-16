package com.muggle.psf.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具
 *
 * @author nick
 */
public class ZipUtils {

    public static class ZipItem {
        /**
         * 文件名，包含目录形式：用/隔开，例如a/b/c.txt
         */
        public String fileName;
        /**
         * 文件对应的输入流
         */
        public InputStream in;
    }

    public static class Iterator {
        private ZipInputStream zin;

        private Iterator(final ZipInputStream zin) {
            this.zin = zin;
        }

        /**
         * 获得下一个ZipItem，当返回null则表示读取完。【重要】ZipItem中的输入流in必须在本次读取完。
         *
         * @return
         * @throws IOException
         */
        public ZipItem next() throws IOException {
            final ZipEntry ze = zin.getNextEntry();
            if (ze == null) {
                return null;
            }
            final ZipItem zipItem = new ZipItem();
            zipItem.fileName = ze.getName();
            zipItem.in = zin;
            return zipItem;
        }
    }

    /**
     * 压缩文件
     *
     * @param zipItems
     * @param out      不会自动close
     */
    public static void zip(final List<ZipItem> zipItems, final OutputStream out) throws IOException {
        final ZipOutputStream zipOut = new ZipOutputStream(out);
        if (zipItems != null) {
            for (final ZipItem zipItem : zipItems) {
                zipOut.putNextEntry(new ZipEntry(zipItem.fileName));
                int len = -1;
                final byte[] buff = new byte[4096];
                while ((len = zipItem.in.read(buff)) != -1) {
                    zipOut.write(buff, 0, len);
                }
                zipItem.in.close();
            }
        }
        zipOut.close();
    }

    /**
     * 解压文件，节省内存方式
     *
     * @param in 不会自动close
     * @return
     */
    public static Iterator unzip(final InputStream in) throws Exception {
        final ZipInputStream zin = new ZipInputStream(in);
        return new Iterator(zin);
    }

    /**
     * 解压文件，会将文件解压到内存中，该方式会占用较多内存
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static List<ZipItem> unzipAll(final InputStream in) throws Exception {
        final Iterator it = unzip(in);
        ZipItem zipItem = null;
        final List<ZipItem> zipItems = new ArrayList<>();
        while ((zipItem = it.next()) != null) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
//			IOUtils.copy(zipItem.in, out);
            zipItem.in = new ByteArrayInputStream(out.toByteArray());
            zipItems.add(zipItem);
        }
        return zipItems;
    }

}
