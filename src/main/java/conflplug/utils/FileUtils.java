package conflplug.utils;

import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.util.AttachmentUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

public class FileUtils {
    private static FileUtils instance;

    public static synchronized FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    private FileUtils() {
    }

    public boolean isPdfMimetype(String mimetype) {
        return mimetype.equalsIgnoreCase("application/pdf");
    }

    public String readAttachment(Attachment attachment) {
        File file = getAttachmentFile(attachment);
        return readFile(file);
    }

    public File getAttachmentFile(Attachment attachment) {
        return AttachmentUtils.getAttachmentFile(attachment);
    }

    public String readFile(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            byte[] pdfBytes = new byte[(int) file.length()];
            in.read(pdfBytes);
            in.close();
            return Base64.getEncoder().encodeToString(pdfBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
