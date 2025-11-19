//package org.firstinspires.ftc.teamcode;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//public class RecorderSystem {
//
//    private final InputStream istream;
//    private final OutputStream ostream;
//
//
//    public RecorderSystem(File fname, boolean readingMode) throws IOException {
//        if (readingMode) {
//            ostream = null;
//            istream = new DataInputStream(new BufferedInputStream(new FileInputStream(fname)));
//        } else {
//            istream = null;
//            ostream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fname)));
//        }
//    }
//
//    public void write(byte[] gamepadData) {
//        if (ostream == null) {
//            return; // Ignore it if we're not meant to write
//        }
//
//        // Write the data to the output file.
//    }
//    public byte[] read() {
//        if (istream == null) {
//            return new byte[0]; // Ignore it if we're not meant to write
//        }
//
//        // Read the bytes from istream
//        return new byte[0];
//    }
//}
