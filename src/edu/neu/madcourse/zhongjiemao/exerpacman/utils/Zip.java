package edu.neu.madcourse.zhongjiemao.exerpacman.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
	
	static final int BUF_SIZE = 1024;
	
	private static byte[] buf = new byte[3812587];
	private static int[] intBuf = new int[952990]; // 952890
	
	/**
	 * For pre-processing sp data, compress a single file into zip, for PC only
	 * @param file
	 */
	public final static void zipFile(File file) {
		try {
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath() + ".zip");
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fos));
            byte data[] = new byte[BUF_SIZE];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream origin = new BufferedInputStream(fis, BUF_SIZE);
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUF_SIZE)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * For pre-processing sp data, decompress a single file into unzip, for PC only
	 * @param file
	 */
	public final static void unzipFile(File file) {
		try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration emu = zipFile.entries();
            while(emu.hasMoreElements()){
                ZipEntry entry = (ZipEntry) emu.nextElement();
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File outFile = new File(file.getAbsolutePath() + ".de");
                FileOutputStream fos = new FileOutputStream(outFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos,BUF_SIZE);           
                int count;
                byte data[] = new byte[BUF_SIZE];
                while ((count = bis.read(data, 0, BUF_SIZE)) != -1) { 
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public final static int[] getUnzipData(InputStream rawIn) {
		try {// 3811587
			ZipInputStream zipIn = new ZipInputStream(rawIn);
			if (zipIn.getNextEntry() == null)
				return null;
			int count;
			int totalCount = 0;
			byte[] tmp = new byte[BUF_SIZE];
			while ((count = zipIn.read(tmp, 0, BUF_SIZE)) != -1) {
				System.arraycopy(tmp, 0, buf, totalCount, count);
				totalCount += count;
			}
			zipIn.close();
            ByteArrayInputStream unzipIn = new ByteArrayInputStream(buf);
            DataInputStream in = new DataInputStream(unzipIn);
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
            	intBuf[i] = in.readInt();
            }
            in.close();
            return intBuf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public final static byte[] compress(byte[] raw, String entryName) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(buffer);
		ZipEntry entry = new ZipEntry(entryName);
		try {
			out.putNextEntry(entry);
			out.write(raw);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}
	
}