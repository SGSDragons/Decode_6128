package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.Objects;

@Config
public class Operator {

    public final DcMotorEx flywheelMotor;
    public final DcMotorEx beltMotor;
    public final DcMotorEx intakeMotor;
    public final Servo gate;
    public static double shooterTargetVelocity;
    public static double savedShooterTargetVelocity = 1450;
    public static double autoFeedRange = 100;
    public static double openGatePos = 0.5;
    public static double closedGatePos = 0.0;

    // Default
    public Operator(HardwareMap hardwareMap, String startGatePos) {
        // Set motors and Servos
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "collect");
        gate = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        if (Objects.equals(startGatePos, "open") || Objects.equals(startGatePos, "closed")) {
            setGatePosition(startGatePos);
        }

        shooterTargetVelocity = savedShooterTargetVelocity;
    }
    public Operator(HardwareMap hardwareMap) {
        // Set motors and Servos
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "collect");
        gate = hardwareMap.get(Servo.class, "stopper");

        beltMotor.setDirection(Direction.REVERSE);

        setGatePosition("closed");

        shooterTargetVelocity = savedShooterTargetVelocity;
    }

    int overloadTicks = 0;
    boolean reversing = false;
    ElapsedTime deltaTime = new ElapsedTime();

    public void Operate(Gamepad gamepad) {

        runFlywheel();

        // If it's not already running in reverse and the motor is drawing too much
        // power, stop it, set it to run in reverse and backup at 50% power
        // Ignore all other operations from the controller until things are back to normal
        if (flywheelMotor.getCurrent(CurrentUnit.MILLIAMPS) > 7000) {
            overloadTicks++;
        } else {
            overloadTicks = 0;
        }
        if (!reversing && overloadTicks > 100) {
            flywheelMotor.setPower(0.0);
            flywheelMotor.setVelocity(0.0);
            beltMotor.setPower(-0.5);
            reversing = true;
            deltaTime.reset();

            return;
        }

        // If it's already reversing, check if the controller can go back to normal
        if (reversing) {
            // Keep waiting until it's close enough
            if (deltaTime.milliseconds() < 1000) {
                return;
            }
            reversing = false;
        }

        if (gamepad.right_trigger > 0.0) {
            shoot(false);
        } else if (gamepad.right_bumper) {
            // Right bumper runs shoot() without stop for emergencies
            shoot(true);
        } else if (gamepad.left_trigger > 0.0) {
            intake();
        } else if (gamepad.left_bumper) {
            // If the artifacts get stuck, the operator can force them out
            eject();
        } else {
            stopBC();
        }

        TelemetryPacket p = new TelemetryPacket();
        p.put("Flywheel Speed", flywheelMotor.getVelocity());
        p.put("Target Speed", shooterTargetVelocity);
        p.put("Belt Speed", beltMotor.getVelocity());
        p.put("Intake Speed", intakeMotor.getVelocity());
        p.put("Flywheel Current", flywheelMotor.getCurrent(CurrentUnit.MILLIAMPS));
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

    public String getGatePosition() {
        if (gate.getPosition() == openGatePos) {
            return "open";
        } else if (gate.getPosition() == closedGatePos) {
            return "closed";
        } else {
            return "neither";
        }
    }

    public void intake() {
        setGatePosition("closed");
        intakeMotor.setPower(1.0);
        beltMotor.setPower(1.0);
    }

    public void eject() {
        setGatePosition("closed");
        intakeMotor.setPower(-1.0);
        beltMotor.setPower(-1.0);
    }

    public void shoot(boolean override) {
        if (!override) {
            // Default behavior, only feed if flywheel is at correct speed
            setGatePosition("open");
            intakeMotor.setPower(0.3);
            beltMotor.setPower(canAutoFeed() ? 1.0 : 0.0);
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

    public void runFlywheel() {
        if (shooterTargetVelocity == 0.0) {
            shooterTargetVelocity = savedShooterTargetVelocity;
        }
        flywheelMotor.setVelocity(shooterTargetVelocity);
    }

    public void stopFlywheel() {
        shooterTargetVelocity = 0.0;
    }
}