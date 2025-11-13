package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.Objects;

@Config
public class DadsOperator {

    public final DcMotorEx shooterMotor;
    public final DcMotorEx beltMotor;
    public final DcMotorEx intakeMotor;
    public final Servo stopper;
    private static boolean shootingDisabledToggle;
    private static double shooterTargetVelocity;
    private static double autoFeedRange;
    private static Gamepad operatorGamepad, driverGamepad;

    // Default
    public DadsOperator(HardwareMap hardwareMap, Gamepad driveGamepad, Gamepad operateGamepad) {
        // Set motors and Servos
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "collect");
        stopper = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;

        operatorGamepad = operateGamepad;
        driverGamepad = driveGamepad;

        stopperPosition("closed");
    }
    public DadsOperator(HardwareMap hardwareMap) {
        // Set motors and Servos
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "collect");
        stopper = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        // Set important constants & variables
        shooterTargetVelocity = 1400;
        autoFeedRange = 100;

        stopperPosition("closed");
    }

    public void Operate() {
        runFlywheel();

        if (operatorGamepad.right_trigger > 0.0) {
            runShooter();
        } else if (operatorGamepad.left_trigger > 0.0) {
            runIntake();
        } else {
            stop(beltMotor, intakeMotor);
        }

        TelemetryPacket p = new TelemetryPacket();
        p.put("Flywheel Speed", shooterMotor.getVelocity());
        p.put("Target Speed", shooterTargetVelocity);
        p.put("Belt Speed", beltMotor.getVelocity());
        p.put("Intake Speed", intakeMotor.getVelocity());
        FtcDashboard.getInstance().sendTelemetryPacket(p);
    }
    private void runFlywheel() {
        shooterMotor.setVelocity(shooterTargetVelocity);
    }
    private void runShooter() {
        stopperPosition("open");
        intakeMotor.setPower(0.0);
        if (canAutoFeed()) {
            beltMotor.setPower(1.0);
        } else {
            stop(beltMotor);
        }
    }
    public void runIntake() {
        beltMotor.setPower(1.0);
        intakeMotor.setPower(1.0);
        stopperPosition("closed");
    }
    public void autoFeed() {
        beltMotor.setPower(1.0);
        intakeMotor.setPower(0.6);
    }
    public boolean canAutoFeed() {
        return Math.abs(shooterTargetVelocity - shooterMotor.getVelocity()) < autoFeedRange;
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