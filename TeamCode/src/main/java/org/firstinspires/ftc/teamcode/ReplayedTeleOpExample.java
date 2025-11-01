package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.systems.Driver;
import org.firstinspires.ftc.teamcode.systems.Operator;
import org.firstinspires.ftc.teamcode.systems.Replayer;

public class ReplayedTeleOpExample {
    @TeleOp(name="Singleplayer TeleOp", group="OpMode")
    public class SingleplayerTeleOp extends LinearOpMode {
        private ElapsedTime runtime = new ElapsedTime();

        @Override
        public void runOpMode() {


            telemetry.addData("Status", "Initialized");
            telemetry.update();

            final Driver driver = new Driver(hardwareMap);
            final Operator operator = new Operator(hardwareMap);
            final Replayer replayer = new Replayer(gamepad1, driver, operator, this);

            // Wait for the game to start (driver presses START)
            waitForStart();
            runtime.reset();

            telemetry.addData("Status", "Running");
            telemetry.update();

            // Replay Recorded TeleOp as an Autonomous
            replayer.ReplayRecording(0);
        }
    }
}
