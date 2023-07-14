/*
 * Copyright (C) 2023 Franco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lanCom;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author Franco
 */
public class FTPclient {

    String server = "www.yourserver.com";
    int port = 21;
    String user = "username";
    String pass = "password";

    FTPClient ftpClient = new FTPClient();

    public boolean connect(String Server, int Port, String User, String Pass) {
        server = Server;
        port = Port;
        user = User;
        pass = Pass;

        try {

            ftpClient.connect(server, port);
            showServerReply(ftpClient);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Connect failed");
                return false;
            }

            boolean success = ftpClient.login(user, pass);
            showServerReply(ftpClient);

            if (!success) {
                System.out.println("Could not login to the server");
                return false;
            }

            // logs out
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
        return true;
    }

    public boolean changeDir(String path) {
        // Changes working directory
        boolean success = false;
        try {
            success = ftpClient.changeWorkingDirectory(path);
            showServerReply(ftpClient);

            if (success) {
                System.out.println("Successfully changed working directory.");
            } else {
                System.out.println("Failed to change working directory. See server's reply.");
            }
        } catch (IOException ex) {
            Logger.getLogger(FTPclient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    public void disconnect() {
        try {
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException ex) {
            Logger.getLogger(FTPclient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String[] getFilesList() {
        String[] names = null;
        try {
            names = ftpClient.listNames();
        } catch (IOException ex) {
            Logger.getLogger(FTPclient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return names;
    }

    public OutputStream downloadFile(String filename) {

        try ( OutputStream os = new FileOutputStream(filename)) {
            // Download file from FTP server.
            boolean status = ftpClient.retrieveFile(filename, os);
            System.out.println("status = " + status);
            System.out.println("reply  = " + ftpClient.getReplyString());
            return os;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> readCSV(String host, int port, String username, String password, String mFilePath) throws Exception {
        System.out.println("_________READLINES");
        ArrayList<String> lines = new ArrayList();
        String ftpUrl = "ftp://%s:%s@%s/%s";
        //String host = "www.myserver.com";

        String dirPath =  mFilePath;

        ftpUrl = String.format(ftpUrl, username, password, host, dirPath);
        System.out.println("URL: " + ftpUrl);

        try {
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
//            System.out.println("--- START ---");
            int ln = 0;
            while ((line = reader.readLine()) != null) {
                ln++;
//                System.out.println(ln + "]" + line);
                lines.add(line);
            }
//            System.out.println("--- END ---");

            inputStream.close();
            conn = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }

    public ArrayList<String> readLines(String mFilePath) throws Exception {
        System.out.println("_________READLINES");
        ArrayList<String> lines = new ArrayList();

        String s = "";
        int fileSize = (int) getFileSize(ftpClient, mFilePath);
        InputStream is = ftpClient.retrieveFileStream(mFilePath);
        int reply = ftpClient.getReplyCode();
        if (is == null
                || (!FTPReply.isPositivePreliminary(reply) && !FTPReply.isPositiveCompletion(reply))) {
            System.out.println("_________ERROR____________");
            throw new Exception(ftpClient.getReplyString());
        }
        //byte[] content = downloadFile(is, (int) fileSize);
        byte[] content = new byte[fileSize];
        if (is.read(content, 0, content.length) == -1) {
            return null;
        }
        s = new String(content, StandardCharsets.UTF_8);

        System.out.println("> " + s);

        is.close();
        String xlines[] = s.split("\\r?\\n");
        lines = new ArrayList<String>(Arrays.asList(xlines));

////////        InputStream is = ftpClient.retrieveFileStream(mFilePath);
////////        int reply = ftpClient.getReplyCode();
////////        System.out.println("reply:"+reply);
////////        if (is == null
////////                || (!FTPReply.isPositivePreliminary(reply)
////////                && !FTPReply.isPositiveCompletion(reply))) {
////////            System.out.println("_________ERROR____________");
////////            throw new Exception(ftpClient.getReplyString());
////////        } else {
////////            lines = new ArrayList();
////////            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
////////            while (reader.ready()) {
////////                String line = reader.readLine();
////////                lines.add(line);
////////            }
////////            reader.close();
////////            System.out.println("_________OK");
////////        }
////////
////////        if (!ftpClient.completePendingCommand()) {
////////            System.out.println("_________ERROR____________");
////////            throw new Exception("Pending command failed: " + ftpClient.getReplyString());
////////        }
////////        is.close();
        //return s;
        return lines;
    }

    public String transferFile(String mFilePath) throws Exception {
        String s = "";
        long fileSize = getFileSize(ftpClient, mFilePath);
        InputStream is = retrieveFileStream(mFilePath);
        byte[] content = downloadFile(is, (int) fileSize);
        s = new String(content, StandardCharsets.UTF_8);
        is.close();

        if (!ftpClient.completePendingCommand()) {
            throw new Exception("Pending command failed: " + ftpClient.getReplyString());
        }

        return s;

    }

    private InputStream retrieveFileStream(String filePath)
            throws Exception {
        InputStream is = ftpClient.retrieveFileStream(filePath);
        int reply = ftpClient.getReplyCode();
        if (is == null
                || (!FTPReply.isPositivePreliminary(reply)
                && !FTPReply.isPositiveCompletion(reply))) {
            throw new Exception(ftpClient.getReplyString());
        }
        return is;
    }

    private ArrayList<String> downloadLines(String mFilePath) throws Exception {
        ArrayList<String> lines;
        try ( InputStream is = retrieveFileStream(mFilePath)) {
            lines = new ArrayList();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (reader.ready()) {
                String line = reader.readLine();
                lines.add(line);
            }
        }
        return lines; // <-- Here is your file's contents !!!
    }

    private byte[] downloadFile(InputStream is, int fileSize)
            throws Exception {
        byte[] buffer = new byte[fileSize];
        if (is.read(buffer, 0, buffer.length) == -1) {
            return null;
        }
        return buffer; // <-- Here is your file's contents !!!
    }

    private long getFileSize(FTPClient ftp, String filePath) throws Exception {
        long fileSize = 0;
        FTPFile[] files = ftp.listFiles(filePath);
        if (files.length == 1 && files[0].isFile()) {
            fileSize = files[0].getSize();
        }
        System.out.println("File size = " + fileSize);
        return fileSize;
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
}
