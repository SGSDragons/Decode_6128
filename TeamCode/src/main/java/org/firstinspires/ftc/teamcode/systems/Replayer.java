package org.firstinspires.ftc.teamcode.systems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Replayer {

    private final Driver replayedDriver;
    private final Operator replayedOperator;
    private final LinearOpMode OpMode;
    private final Gamepad gamepad1;

    public Replayer(Gamepad gamepad, Driver driver, Operator operator, LinearOpMode linearOpMode) {
        gamepad1 = gamepad;
        replayedDriver = driver;
        replayedOperator = operator;
        OpMode = linearOpMode;
    }

    public void ReplayRecording(long id) {
        Path recordingsPath = Paths.get("recordings/");
        Path filePath = recordingsPath.resolve(id + ".dat");
        List<byte[]> gamepadStates = new ArrayList<>();

        // --- Load recording using binary-safe reading ---
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        Files.newInputStream(filePath.toFile().toPath())))) {

            while (in.available() > 0) {
                int len = in.readInt();
                byte[] bytes = new byte[len];
                in.readFully(bytes);
                gamepadStates.add(bytes);
            }

        } catch (IOException e) {
            OpMode.telemetry.addData("Replay", "Error reading file: %s", e.getMessage());
            OpMode.telemetry.update();
            return;
        }

        // --- Replay each recorded gamepad state ---
        OpMode.telemetry.addData("Replay", "Running");
        OpMode.telemetry.update();

        for (byte[] gamepadState : gamepadStates) {
            gamepad1.fromByteArray(gamepadState);

            replayedDriver.Drive(gamepad1);
            replayedOperator.Operate(gamepad1, gamepad1);

            OpMode.sleep(100); // match original timing roughly
        }

        OpMode.telemetry.addData("Replay", "Done");
        OpMode.telemetry.update();
        OpMode.requestOpModeStop();
    }
}
