package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Autonomous(name="Record Singleplayer TeleOp", group="OpMode")
public class Recorder extends LinearOpMode {

    @Override
    public void runOpMode() {
        ElapsedTime runtime = new ElapsedTime();
        Path recordingsPath = Paths.get("recordings/");
        long recordingsDone;
        Path filePath;
        DataOutputStream out = null;
        boolean fileReady = false;
        boolean writeError = false;

        // --- Count existing recordings to pick next file name ---
        try (Stream<Path> files = Files.list(recordingsPath)) {
            recordingsDone = files.count();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list recordings directory", e);
        }

        filePath = recordingsPath.resolve(recordingsDone + ".dat");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final Driver driver = new Driver(hardwareMap);
        final Operator operator = new Operator(hardwareMap);

        // Wait for the game to start
        waitForStart();
        runtime.reset();

        telemetry.addData("Status", "Recording...");
        telemetry.update();

        // --- Try opening file for buffered binary writing ---
        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(
                            Files.newOutputStream(filePath)));
            fileReady = true;
        } catch (IOException e) {
            telemetry.addData("Error", "Could not open file: %s", e.getMessage());
            telemetry.update();
        }

        // --- Passive recording loop ---
        while (opModeIsActive()) {
            // Capture current gamepad state (assuming your Gamepad class has toByteArray)
            byte[] data = gamepad1.toByteArray();

            // If data is available and file is ready, try to write
            if (data != null && fileReady && !writeError) {
                try {
                    out.writeInt(data.length);
                    out.write(data);
                } catch (IOException e) {
                    telemetry.addData("Error", "Failed to write: %s", e.getMessage());
                    telemetry.update();
                    writeError = true;
                }
            }

            // Update robot behavior while recording
            driver.Drive(gamepad1);
            operator.Operate(gamepad1, gamepad1);

            // Passive delay (FTC-safe)
            sleep(100);
        }

        // --- Clean up after stop ---
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