package access_manager;

import db_storage.DBFilePassword;
import handler.FileCreator;
import handler.InvalidIdException;
import org.apache.commons.codec.digest.DigestUtils;

public class AccessManager {

    public static void setPassword(String password, FileCreator fileCreator) throws InvalidIdException {
        var passwordHash = DigestUtils.md5Hex(password);
        DBFilePassword.setPassword(passwordHash, fileCreator.getId());
    }

    public static boolean checkPassword(String password, FileCreator fileCreator) throws InvalidIdException {
        var okPassword = DBFilePassword.getPassword(fileCreator.getId());
        var newPasswordHash = DigestUtils.md5Hex(password);

        return okPassword.equals(newPasswordHash);
    }
}
