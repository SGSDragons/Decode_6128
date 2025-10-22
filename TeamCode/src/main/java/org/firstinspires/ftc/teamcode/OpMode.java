package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.concurrent.TimeUnit;
import org.firstinspires.ftc.teamcode.BasicOpMode.*;

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

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // POV Mode uses left stick for forward, backward, left, right, and right stick to turn.

            // Get input from the gamepad
            double forward = -gamepad1.left_stick_y;  // Forward is negative Y
            double strafe = -gamepad1.left_stick_x;    // Left/Right strafe
            double turn = -gamepad1.right_stick_x;     // Turn left/right

            drive.drive(forward, strafe, turn);

            // Set shooter Wheel and belt-collector Wheel Motors
            final DcMotor shooterMotor, bCMotor;
            shooterMotor  = hardwareMap.get(DcMotor.class, "shoot");
            shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            bCMotor     = hardwareMap.get(DcMotor.class, "collect");

            // Spin shooter wheel if the trigger is being held
            if (gamepad1.right_trigger > 0) {
                shooterMotor.setPower(1.0);
            } else {
                shooterMotor.setPower(0.0);
            }

            // Spin belt-collector wheel if the trigger is being held
            if (gamepad1.left_trigger > 0) {
                bCMotor.setPower(1.0);
            } else if (gamepad1.left_bumper) {
                bCMotor.setPower(-0.5);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                bCMotor.setPower(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            } else {
                bCMotor.setPower(0.0);
            }
        }
    }
}
