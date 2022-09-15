package proj10LoverudeTymkiwCorrell.bantam.util;

import java.io.IOException;

public class FileExtensionChanger {

    public static String fileWithChangedExtension(String file) throws IOException {
        String targetExtension = ".java";
        StringBuilder newName = new StringBuilder(file);
        newName.replace(file.length()-4, file.length(), targetExtension);

        return newName.toString();
    }
}
