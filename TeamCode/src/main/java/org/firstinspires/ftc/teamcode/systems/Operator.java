package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Objects;

@Config
public class Operator {

    public final DcMotorEx shooterMotor;
    public final DcMotorEx beltMotor;
    public final DcMotorEx collectorMotor;
    public final Servo stopper;
    private static boolean shootingDisabledToggle;
    private static double shooterTargetVelocity;
    private static double autoFeedRange;
    private static Gamepad operatorGamepad, driverGamepad;

    // Default
    public Operator(HardwareMap hardwareMap, Gamepad driveGamepad, Gamepad operateGamepad) {
        // Set motors and Servos
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        collectorMotor = hardwareMap.get(DcMotorEx.class, "collect");
        stopper = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;
        shootingDisabledToggle = true;

        operatorGamepad = operateGamepad;
        driverGamepad = driveGamepad;

        stopperPosition("closed");
    }
    public Operator(HardwareMap hardwareMap) {
        // Set motors and Servos
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        collectorMotor = hardwareMap.get(DcMotorEx.class, "collect");
        stopper = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;
        shootingDisabledToggle = true;

        stopperPosition("closed");
    }

    public void Operate() {

        // Always run shooter as long as the toggle is on
        if (!shootingDisabledToggle && !operatorGamepad.left_bumper) {
            runShooter();
        }
        // Otherwise spin shooter wheel when the trigger is being held
        else if (operatorGamepad.right_trigger > 0 && !operatorGamepad.left_bumper) {
            runShooter();
        } else if (!operatorGamepad.left_bumper) {
            stop(shooterMotor);
        }

        // Run the belt & collector if the trigger is being held
        if (operatorGamepad.left_trigger > 0 && !operatorGamepad.left_bumper) {
            runIntake();
        }
        // Auto run the belt if it is at max speed
        else if (canAutoFeed()) {
            autoFeed();
        }
        else if (!operatorGamepad.left_bumper) {
            stop(beltMotor, collectorMotor);
        }

        // Togglable constant shooting
        if (operatorGamepad.bWasPressed()) {
            shootingDisabledToggle = !shootingDisabledToggle;
        }

        // Empty Barrel Button
        if (operatorGamepad.left_bumper) {
            emptyBarrel();
        }
    }

    public void emptyBarrel() {
        beltMotor.setPower(-1.0);
        collectorMotor.setPower(-1.0);
        shooterMotor.setPower(-1.0);
    }
    public void activateShooter() {
        shootingDisabledToggle = true;
    }
    private void runShooter() {
        shooterMotor.setVelocity(shooterTargetVelocity);
        if (!(operatorGamepad == null)) {
            if (!(operatorGamepad.left_trigger > 0.0)) {
                stopperPosition("open");
            }
        }
    }
    public void runIntake() {
        beltMotor.setPower(1.0);
        collectorMotor.setPower(0.8);
        stopperPosition("closed");
    }
    public void autoFeed() {
        beltMotor.setPower(1.0);
        collectorMotor.setPower(0.6);
    }
    public boolean canAutoFeed() {
        return Math.abs(shooterTargetVelocity - shooterMotor.getVelocity()) < autoFeedRange
                && operatorGamepad.right_trigger > 0.0 && stopper.getPosition() == 1.0
                && !shootingDisabledToggle && !operatorGamepad.left_bumper;
    }
    public void stopperPosition(String stopperPosition) {
        if (Objects.equals(stopperPosition, "open")) {
            stopper.setPosition(1.0);
        }
        else if (Objects.equals(stopperPosition, "closed")) {
            stopper.setPosition(0.55);
        }
        else {
            return;
        }
    }
    public void stop(DcMotorEx... motors) {
        for (DcMotorEx motor : motors) {
            motor.setPower(0.0);
        }
    }
}