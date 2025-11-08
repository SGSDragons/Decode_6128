package org.firstinspires.ftc.teamcode.systems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Replayer {

    private final Driver replayedDriver;
    private final Operator replayedOperator;
    private final LinearOpMode OpMode;
    private final Gamepad gamepad1;

    public Replayer(Gamepad gamepad, Driver driver, Operator operator, LinearOpMode linearOpMode) {
        this.gamepad1 = gamepad;
        this.replayedDriver = driver;
        this.replayedOperator = operator;
        this.OpMode = linearOpMode;
    }

    public void ReplayRecording(long id) {
        // Use the same internal app storage directory as Recorder
        File recordingsDir = new File(OpMode.hardwareMap.appContext.getFilesDir(), "recordings");
        File file = new File(recordingsDir, id + ".dat");

        List<byte[]> gamepadStates = new ArrayList<>();

        // --- Load recording using binary-safe reading ---
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {

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

            replayedDriver.Drive();
            replayedOperator.Operate();

            OpMode.sleep(100); // rough timing match
        }

        OpMode.telemetry.addData("Replay", "Done");
        OpMode.telemetry.update();
        OpMode.requestOpModeStop();
    }
}
