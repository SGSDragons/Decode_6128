package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.systems.DriveTrain;
import org.firstinspires.ftc.teamcode.systems.OperationSubsystem;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="6128 DECODE Autonomous", group="OpMode")
@Config
public class Autonomous extends LinearOpMode{

    private ElapsedTime runtime = new ElapsedTime();
    public static boolean doBackup = true;
    public static boolean doShoot = true;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final DriveTrain drive = new DriveTrain(hardwareMap);
        final OperationSubsystem operate = new OperationSubsystem(hardwareMap);

        // Wait for the game to start (driver presses START)
        waitForStart();
        telemetry.addData("Status", "Running");
        telemetry.update();

        // Run backwards at full power for 1.5 seconds
        if (doBackup) {
            telemetry.addData("Status", "Backing up");
            telemetry.update();

            runtime.reset();

            for (DcMotor wheelMotor : drive.allMotors) {
                wheelMotor.setTargetPosition(wheelMotor.getCurrentPosition() - 140);
                drive.frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                wheelMotor.setPower(-0.4);
            }
            while (opModeIsActive() && drive.frontLeftDrive.isBusy() && runtime.milliseconds() < 1500) {}

            for (DcMotor motor : drive.allMotors) {
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor.setPower(0.0);
            }

        }

        // Shoot 3 artifacts
        if (doShoot) {
            telemetry.addData("Status", "Shooting");
            telemetry.update();

            runtime.reset();
            while (opModeIsActive() && runtime.milliseconds() < 15000) {
                operate.runShooter = true;
                operate.Update();
            }
            operate.runShooter = false;
            operate.Update();
        }

        // End
        telemetry.addData("Status", "Stalling");
        telemetry.update();
        while (opModeIsActive()) {}
    }
}