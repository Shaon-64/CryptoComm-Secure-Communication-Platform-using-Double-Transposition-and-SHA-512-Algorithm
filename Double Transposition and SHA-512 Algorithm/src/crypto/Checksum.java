package crypto;

import java.security.MessageDigest;

public class Checksum {

    // calculate hash value for checksum
    public   String calculateHash(String message) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hashBytes = md.digest(message.getBytes());
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    // verify checksum
    public   boolean verifyHash(String message, String providedHash) throws Exception {
        return calculateHash(message).equals(providedHash);
    }
}
