package tw.edu.ntut.sce.java18.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.xml.bind.DatatypeConverter;

public class GlobalService {
  public static final String SYSTEM_NAME = "山水苑";
  public static final String SYSTEM_TITLE = "山水苑-租屋首選";
  public static final String KEY = "KittySnoopyGarfieldMicky"; // 16, 24, 32

  public String getSystemName() {
    return SYSTEM_NAME;
  }

  public String getSystemTitle() {
    return SYSTEM_TITLE;
  }

  /* MD5 編碼 */
  public static String getMD5Endocing(String message) {
    final StringBuffer buffer = new StringBuffer();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(message.getBytes());
      byte[] digest = md.digest();

      for (int i = 0; i < digest.length; ++i) {
        final byte b = digest[i];
        final int value = Byte.toUnsignedInt(b);
        buffer.append(value < 16 ? "0" : "");
        buffer.append(Integer.toHexString(value));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
    return buffer.toString();
  }

  /* SHA1 編碼 */
  public static String getSHA1Endocing(String message) {
    final StringBuffer buffer = new StringBuffer();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      md.update(message.getBytes());
      byte[] digest = md.digest();

      for (int i = 0; i < digest.length; ++i) {
        final byte b = digest[i];
        final int value = Byte.toUnsignedInt(b);
        buffer.append(value < 16 ? "0" : "");
        buffer.append(Integer.toHexString(value));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
    return buffer.toString();
  }

  /**
   * 依MD5演算法將檔案file轉換為128位元(16個位元組)的資料。
   * 
   * @param message : 要加密的字串
   * @return : 128位元資料的16進位表示法所構成的字串
   */

  public static String getMD5Endocing(File file) throws NoSuchAlgorithmException, IOException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    try (FileInputStream fis = new FileInputStream(file);) {
      byte[] ba = new byte[1024];
      int len = 0;
      while ((len = fis.read(ba)) != -1) {
        md.update(ba, 0, len);
      }
    }
    byte[] digest = md.digest();
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < digest.length; ++i) {
      byte b = digest[i];
      final int value = Byte.toUnsignedInt(b);
      buffer.append(value < 16 ? "0" : "");
      buffer.append(Integer.toHexString(value));

    }
    return buffer.toString();
  }


  public static String getSHA1Endocing(File file) throws NoSuchAlgorithmException, IOException {
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    try (FileInputStream fis = new FileInputStream(file);) {
      byte[] ba = new byte[1024];
      int len = 0;
      while ((len = fis.read(ba)) != -1) {
        md.update(ba, 0, len);
      }
    }
    byte[] digest = md.digest();
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < digest.length; ++i) {
      byte b = digest[i];
      // int value = (b & 0x7F) + (b < 0 ? 128 : 0);
      final int value = Byte.toUnsignedInt(b);
      buffer.append(value < 16 ? "0" : "");
      buffer.append(Integer.toHexString(value));

    }
    return buffer.toString();
  }

  /**
   * 本方法可對字串message(Plaintext, 明文)加密，然後將加密後的字串 (Ciphertext, 密文)傳回。
   * 
   * @param key : 加密金鑰
   * @param message : 明文，即要加密的字串
   * @return 加密後的
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws Throwable
   */
  public static String encryptString(String message) {
    // DES : Data Encryption Standard, 一種對稱式加密演算法。
    // 美國聯邦政府於1976年定為聯邦資料處理標準(FIPS)，它的
    // 金鑰則必須是7個位元組、加密區塊(Cipher Block)固定為8個位元組。
    // DES目前已被視為是一種不安全的演算法。
    // AES : Advanced Encryption Standard, 一種對稱式加密演算法。
    // (美國聯邦政府於2001年納入FIPS 140-2標準)，此種演算法
    // 的Cipher Block固定為16個位元組。金鑰則必須是16個位元組、
    // 24個位元組或32個位元組(即128個位元、192個位元或256個位元)。
    // ECB : Electronic CookBook, 一種資料的加密方式，這種加密方式採取
    // 每個區塊(如8個或16個位元組)獨立加密，即加密任ㄧ區塊時與其它區塊
    // 無關。獨立壓縮有優點也有缺點。
    // 優點為可以由多個處理器來平行處理ㄧ個很大的資料。缺點為如果資料
    // 的內容有重複出現的部分，而且重複資料的長度剛好與加密區塊等長，
    // 則這些重複出現的部分經過加密後會出現相同的結果。
    // PKCS5Padding: 如果要加密的資料不是8個(如DES加密演算法)或
    // 16個(如AES加密演算法)位元組的整數倍，則必須在欲加密資料的
    // 尾端加入若干個位元組來湊成整數倍。PKCS5Padding是一種
    // 補足不足位元組的方法。
    String encryptedString = "";
    try {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), "AES");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      encryptedString = DatatypeConverter.printBase64Binary(cipher.doFinal(message.getBytes()));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return encryptedString;
  }

  /**
   * 本方法可對加密之字串(Ciphertext)解密，key為當初加密時的金鑰 傳回值為解密後的字串(Plaintext)
   * 
   */
  public static String decryptString(String key, String stringToDecrypt) {
    String decryptedString = "";
    try {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] b = DatatypeConverter.parseBase64Binary(stringToDecrypt);
      decryptedString = new String(cipher.doFinal(b));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return decryptedString;
  }

  public static Blob fileToBlob(String imageFileName) throws IOException, SQLException {
    File imageFile = new File(imageFileName);
    long size = imageFile.length();
    byte[] b = new byte[(int) size];
    SerialBlob sb = null;
    try (FileInputStream fis = new FileInputStream(imageFile);) {
      fis.read(b);
      sb = new SerialBlob(b);
    }
    return sb;
  }

  public static Clob fileToClob(String textFileName) throws IOException, SQLException {
    Clob clob = null;
    try (InputStreamReader isr =
        new InputStreamReader(new FileInputStream(textFileName), "UTF-8");) {
      char[] c = new char[8192];
      StringBuffer buf = new StringBuffer();
      int len = 0;
      while ((len = isr.read(c)) != -1) {
        buf.append(new String(c, 0, len));
      }
      char[] ca = buf.toString().toCharArray();
      clob = new SerialClob(ca);
    }
    return clob;
  }

  public static void clobToFile(Clob clob, File file, String encoding)
      throws IOException, SQLException {
    try (Reader reader = clob.getCharacterStream();
        BufferedReader br = new BufferedReader(reader);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
        PrintWriter out = new PrintWriter(osw);) {
      String line = null;
      while ((line = br.readLine()) != null) {
        out.println(line);
      }
    }
  }

  public static Blob fileToBlob(InputStream is, long size) throws IOException, SQLException {
    byte[] b = new byte[(int) size];
    SerialBlob sb = null;
    is.read(b);
    sb = new SerialBlob(b);
    return sb;
  }

  public static String extractFileName(String pathName) throws IOException, SQLException {
    return pathName.substring(pathName.lastIndexOf("/") + 1);
  }


}
