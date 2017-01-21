package helpers;

import play.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
  /**
   * http://www.sha1-online.com/sha1-java/
   */
  public static String sha1(String input) {
    MessageDigest mDigest = null;
    StringBuilder sb = new StringBuilder();
    try {
      mDigest = MessageDigest.getInstance("SHA1");
      byte[] result = mDigest.digest(input.getBytes());
      for (byte aResult : result) {
        sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException e) {
      Logger.error(e.getMessage());
    }
    return sb.toString();
  }
}
