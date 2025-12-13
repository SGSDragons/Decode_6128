package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.systems.Driver;
import org.firstinspires.ftc.teamcode.systems.Operator;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="6128 DECODE Autonomous", group="OpMode")
@Config
public class Autonomous extends LinearOpMode{

    private ElapsedTime runtime = new ElapsedTime();

    public static int backupTime = 1000;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final Driver drive = new Driver(hardwareMap);
        final Operator operator = new Operator(drive.allDrives, hardwareMap, "open");

        operator.setMode(Operator.OpMode.AUTO);

        // Wait for the game to start (driver presses START)
        waitForStart();
        telemetry.addData("Status", "Running");
        telemetry.update();

        operator.runFlywheel();

        // Run backwards at full power for 1.5 seconds
        runtime.reset();
        for (DcMotor motor : drive.allDrives) {
            motor.setPower(-0.4);
        }

        while (opModeIsActive() && runtime.milliseconds() < backupTime) continue;

        for (DcMotor motor : drive.allDrives) {
            motor.setPower(0.0);
        }

        // Wait for half a second
        runtime.reset();
        while (opModeIsActive() && runtime.milliseconds() < 500) continue;

        // Shoot 3 artifacts
        runtime.reset();
        while (opModeIsActive() && runtime.milliseconds() < 15000) {
            operator.shoot(false);
        }
        operator.stopBC();
        operator.flywheelMotor.setPower(0.0);

        // End
        telemetry.addData("Status", "Stalling");
        telemetry.update();
        while (opModeIsActive()) continue;
    }
}