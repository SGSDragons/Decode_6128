package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class Operator {

    private final DcMotorEx shooterMotor;
    private final DcMotorEx bCMotor;
    public static double shooterTargetVelocity = 1400;
    public static double autoFeedRange = 100;

    public Operator(HardwareMap hardwareMap) {
        // Set shooter wheel and belt-collector wheel motors
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        bCMotor = hardwareMap.get(DcMotorEx.class, "collect");

        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;
    }

    public void Operate(Gamepad gamepad1, Gamepad gamepad2) {
        // Backwards button for when the artifacts are in the way of the shooter
        if (gamepad2.square || gamepad2.x) {
            shooterMotor.setVelocity(-500); // -25% of the max velocity (2000)
            bCMotor.setPower(-0.5); // -50% of the max power (1.0)
        }

        // Spin shooter wheel if the trigger is being held
        if (gamepad2.right_trigger > 0) {
            shooterMotor.setVelocity(shooterTargetVelocity);
        } else if (gamepad2.square || !gamepad2.x) {
            shooterMotor.setVelocity(0.0);
        }

        // Spin belt-collector wheel if the trigger is being held
        if (gamepad2.left_trigger > 0) {
            bCMotor.setPower(1.0);
        }
        // Auto run the belt if it is at max speed
        else if (Math.abs(shooterTargetVelocity - shooterMotor.getVelocity()) < autoFeedRange && !gamepad2.circle && !gamepad2.b && gamepad2.right_trigger > 0.0) {
            bCMotor.setPower(1.0);
        } else if (!gamepad2.square || !gamepad2.x) {
            bCMotor.setPower(0.0);
        }

        // Communication when needed
        if (gamepad1.triangle || gamepad1.y) {
            gamepad2.rumble(100);
            gamepad2.setLedColor(55, 255, 55, 200);
        }
        if (gamepad2.triangle || gamepad2.y) {
            gamepad1.rumble(100);
            gamepad1.setLedColor(55, 255, 55, 200);
        }
    }
}