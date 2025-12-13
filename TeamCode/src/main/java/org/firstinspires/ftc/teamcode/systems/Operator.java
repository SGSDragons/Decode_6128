package org.firstinspires.ftc.teamcode.systems;

import android.graphics.Path;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.List;
import java.util.Objects;

@Config
public class Operator {

    public final DcMotorEx flywheelMotor;
    public final DcMotorEx beltMotor;
    public final DcMotorEx intakeMotor;
    public final Servo gate;
    public static double shooterTargetVelocity = 1450;
    public static double autoFeedRange;
    public double openGatePos = 0.5;
    public double closedGatePos = 0.0;
    public double MILLIAMP_LIMIT = 25000;

    public List<DcMotorEx> allOperators;
    public List<DcMotorEx> driveMotors;

    // PID Values
    public static double FW_P = 7;
    public static double FW_I;
    public static double FW_D = 0.0;
    public static double FW_F = 15.0;

    // Deltatime
    ElapsedTime deltaTime = new ElapsedTime();

    public Operator(List<DcMotorEx> driveMotors, HardwareMap hardwareMap, String startGatePos) {
        // Set motors and Servos
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "collect");
        gate = hardwareMap.get(Servo.class, "stopper");

        allOperators = List.of(flywheelMotor, beltMotor, intakeMotor);
        this.driveMotors = driveMotors;

        beltMotor.setDirection(Direction.REVERSE);

        if (Objects.equals(startGatePos, "open") || Objects.equals(startGatePos, "closed")) {
            setGatePosition(startGatePos);
        }
    }

    public Operator(List<DcMotorEx> driveMotors, HardwareMap hardwareMap) {
        this(driveMotors, hardwareMap, "closed");
    }

    public enum OpMode {
        AUTO,
        TELEOP
    }
    public void setMode(OpMode mode) {
        if (mode == OpMode.AUTO) {
            FW_I = 0.1;
            autoFeedRange = 30;
        } else {
            FW_I = 0.15;
            autoFeedRange = 75;
        }

        PIDFCoefficients pid = new PIDFCoefficients(FW_P, FW_I, FW_D, FW_F);
        flywheelMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pid);
    }

    public void Operate(Gamepad gamepad) {

        runFlywheel();

        if (gamepad.right_trigger > 0.0) {
            shoot(false);
        } else if (gamepad.right_bumper) {
            // Right bumper runs shoot() without stop for emergencies
            shoot(true);
        } else if (gamepad.left_trigger > 0.0) {
            intake();
        } else {
            stopBC();
        }

        updateTelemetry();
    }

    public void updateTelemetry() {
        TelemetryPacket p = new TelemetryPacket();
        p.put("10 Target Speed", shooterTargetVelocity);
        p.put("10 Flywheel Speed", flywheelMotor.getVelocity());
        p.put("10 Min Flywheel Range", shooterTargetVelocity - autoFeedRange);
        p.put("10 Max Flywheel Range", shooterTargetVelocity + autoFeedRange);
        p.put("11 Belt Speed", beltMotor.getVelocity());
        p.put("11 Intake Speed", intakeMotor.getVelocity());

        double wheelCurrent = 0.0;
        p.put("20 Flywheel Current", flywheelMotor.getCurrent(CurrentUnit.MILLIAMPS));
        p.put("20 Intake Current", intakeMotor.getCurrent(CurrentUnit.MILLIAMPS));
        p.put("20 Belt Current", beltMotor.getCurrent(CurrentUnit.MILLIAMPS));
        for (DcMotorEx motor : driveMotors) {
            wheelCurrent += motor.getCurrent(CurrentUnit.MILLIAMPS);
        }
        p.put("20 Wheel Current", wheelCurrent);

        p.put("20 Total Current", totalMilliamps());

        if (driveMotors.size() == 4) {
            p.put("21 Front Left Drive Current", driveMotors.get(0).getCurrent(CurrentUnit.MILLIAMPS));
            p.put("21 Front Right Drive Current", driveMotors.get(1).getCurrent(CurrentUnit.MILLIAMPS));
            p.put("21 Back Left Drive Current", driveMotors.get(2).getCurrent(CurrentUnit.MILLIAMPS));
            p.put("21 Back Right Drive Current", driveMotors.get(3).getCurrent(CurrentUnit.MILLIAMPS));
        }

        FtcDashboard.getInstance().sendTelemetryPacket(p);
    }

    public boolean canAutoFeed() {
        return Math.abs(shooterTargetVelocity - flywheelMotor.getVelocity()) < autoFeedRange;
    }

    public void setGatePosition(String gatePosition) {
        if (Objects.equals(gatePosition, "open")) {
            gate.setPosition(openGatePos);
        }
        else if (Objects.equals(gatePosition, "closed")) {
            gate.setPosition(closedGatePos);
        }
        else {
            return;
        }
    }

    public void intake() {
        setGatePosition("closed");
        intakeMotor.setPower(1.0);
        if (!isJammed()) {
            beltMotor.setPower(1.0);
        }
    }

    public void shoot(boolean overrideAutoFeed) {
            if (!overrideAutoFeed) {
                // Default behavior, only feed if flywheel is at correct speed
                setGatePosition("open");
                if (canAutoFeed()) {
                    intakeMotor.setPower(0.3);
                    beltMotor.setPower(1.0);
                } else {
                    intakeMotor.setPower(0.0);
                    beltMotor.setPower(0.0);
                }
            } else {
                // Override behavior for when there is a problem with flywheel speed
                setGatePosition("open");
                intakeMotor.setPower(0.3);
                beltMotor.setPower(1.0);
            }
    }

    public void stopBC() {
        intakeMotor.setPower(0.0);
        beltMotor.setPower(0.0);
    }

    ElapsedTime jamBlocker = new ElapsedTime();
    boolean isJammed() {
        return jamBlocker.milliseconds() < 500;
    }

    double totalMilliamps() {
        double totalCurrent = 0.0;
        for (DcMotorEx motor : allOperators) {
            totalCurrent += motor.getCurrent(CurrentUnit.MILLIAMPS);
        }
        for (DcMotorEx motor : driveMotors) {
            totalCurrent += motor.getCurrent(CurrentUnit.MILLIAMPS);
        }
        return totalCurrent;
    }

    public void runFlywheel() {

        if (totalMilliamps() > MILLIAMP_LIMIT) {
            jamBlocker.reset();
        }

        if (isJammed()) {
            // If current is too high (stalling/jammed), stop the motor
            flywheelMotor.setPower(0.0);
            setGatePosition("closed");
            beltMotor.setPower(-1.0);
        } else {
            flywheelMotor.setVelocity(shooterTargetVelocity);
        }
    }
}