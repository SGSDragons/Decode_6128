package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="6128 DECODE OpMode", group="OpMode")
public class OpMode extends LinearOpMode{

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final DriveTrain drive = new DriveTrain(hardwareMap);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // Set shooter wheel and belt-collector wheel motors
        final DcMotorEx shooterMotor, bCMotor;
        shooterMotor  = hardwareMap.get(DcMotorEx.class, "shoot");
        bCMotor       = hardwareMap.get(DcMotorEx.class, "collect");

        // Set other variables
        double minShooterVelocity = 1300;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Get input from the gamepad
            double forward = -gamepad1.left_stick_y;  // Forward is negative Y
            double strafe = gamepad1.left_stick_x;   // Left/Right strafe
            double turn = gamepad1.right_stick_x;    // Turn left/right

            drive.drive(forward, strafe, turn);

            // Spin shooter wheel if the trigger is being held
            if (gamepad2.right_trigger > 0) {
                shooterMotor.setPower(1.0);
            } else {
                shooterMotor.setPower(0.0);
            }

            // Spin belt-collector wheel if the trigger is being held
            if (gamepad2.left_trigger > 0) {
                bCMotor.setPower(1.0);
            }
            // Backwards button for when the artifacts are in the way of the shooter
            else if (gamepad2.left_bumper) {
                bCMotor.setPower(-0.5);
            }
            // Auto run the belt if it is at max speed
            else if (shooterMotor.getVelocity() > minShooterVelocity && !gamepad2.circle && gamepad2.right_trigger > 0.0) {
                gamepad2.rumble(500);
                bCMotor.setPower(1.0);
            }
            else {
                bCMotor.setPower(0.0);
                gamepad2.stopRumble();
            }

            // Communication when needed
            if (gamepad1.triangle) {
                gamepad2.rumble(100);
                gamepad2.setLedColor(55, 255, 55, 200);
            } if (gamepad2.triangle) {
                gamepad1.rumble(100);
                gamepad1.setLedColor(55, 255, 55, 200);
            }
        }
    }
}
