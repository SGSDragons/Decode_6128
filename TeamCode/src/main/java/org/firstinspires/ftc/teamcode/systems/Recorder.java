package org.firstinspires.ftc.teamcode.systems;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Autonomous(name="Record Singleplayer TeleOp", group="OpMode")
public class Recorder extends LinearOpMode {

    @Override
    public void runOpMode() {
        ElapsedTime runtime = new ElapsedTime();
        long recordingsDone = 0;
        DataOutputStream out = null;
        boolean fileReady = false;
        boolean writeError = false;

        // Use java.io.File for better Android compatibility
        File recordingsDir = new File(Environment.getExternalStorageDirectory(), "recordings");

        if (!recordingsDir.exists()) {
            if (!recordingsDir.mkdirs()) {
                telemetry.addData("Error", "Could not create recordings directory");
                telemetry.update();
            }
        }

        // Count existing recordings
        File[] existingFiles = recordingsDir.listFiles();
        if (existingFiles != null) {
            recordingsDone = existingFiles.length;
        }

        File outputFile = new File(recordingsDir, recordingsDone + ".dat");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final Driver driver = new Driver(hardwareMap);
        final Operator operator = new Operator(hardwareMap);

        // Wait for the game to start
        waitForStart();
        runtime.reset();

        telemetry.addData("Status", "Recording...");
        telemetry.update();

        // Try opening file for buffered binary writing
        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(outputFile)));
            fileReady = true;
        } catch (IOException e) {
            telemetry.addData("Error", "Could not open file: %s", e.getMessage());
            telemetry.update();
        }

        int recordingCount = 0;

        // Passive recording loop
        while (opModeIsActive()) {
            // Capture current gamepad state (assuming Gamepad class has toByteArray)
            byte[] data = gamepad1.toByteArray();

            if (data != null && fileReady && !writeError) {
                try {
                    out.writeInt(data.length);
                    out.write(data);
                    out.flush();
                    recordingCount++;
                    telemetry.addData("Recordings", recordingCount);
                    telemetry.update();
                } catch (IOException e) {
                    telemetry.addData("Error", "Failed to write: %s", e.getMessage());
                    telemetry.update();
                    writeError = true;
                }
            } else {
                telemetry.addData("Data Null?", data == null);
                telemetry.addData("File Ready?", fileReady);
                telemetry.addData("Write Err?", writeError);
                telemetry.update();
            }

            // Update robot behavior while recording
            driver.Drive(gamepad1);
//            operator.Operate(gamepad1);

            sleep(100); // FTC-safe delay
        }

        // Clean up after stop
        if (fileReady) {
            try {
                out.close();
            } catch (IOException e) {
                telemetry.addData("Error", "Failed to close file: %s", e.getMessage());
                telemetry.update();
            }
        }

        telemetry.addData("Status", "Recording Stopped");
        telemetry.update();
    }
}
