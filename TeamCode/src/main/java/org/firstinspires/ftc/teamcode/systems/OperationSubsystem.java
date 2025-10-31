package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class OperationSubsystem {
    public final DcMotorEx shooterMotor;
    public final DcMotorEx bCMotor;
    public static double shooterTargetPower;
    public static double shooterVelocityMinRange;
    public boolean runShooter;
    public boolean runBC;
    public boolean emptyBarrel;
    public boolean forceStopAutoFeed;

    public OperationSubsystem(HardwareMap hardwareMap) {
        // Set shooter wheel and belt-collector wheel motors
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        bCMotor = hardwareMap.get(DcMotorEx.class, "collect");

        // Set important variables
        shooterTargetPower = 0.75;
        shooterVelocityMinRange = 100;
        forceStopAutoFeed = false;
        emptyBarrel = false;
        runBC = false;
        runShooter = false;
    }

    public void Update() {
        // Backwards for when the artifacts are in the way of the shooter
        if (emptyBarrel) {
            shooterMotor.setVelocity(-500); // -25% of the max velocity (2000)
            bCMotor.setPower(-0.5); // -50% of the max power (1.0)
        }

        // Spin shooter wheel
        if (runShooter) {
            shooterMotor.setPower(shooterTargetPower);
        } else if (!emptyBarrel) {
            shooterMotor.setPower(0.0);
        }

        // Spin belt-collector wheel
        if (runBC) {
            bCMotor.setPower(1.0);
        }
        // Auto run the belt if it is at max speed
        else if ((shooterTargetPower * 2000) - shooterMotor.getVelocity() < shooterVelocityMinRange && !forceStopAutoFeed && runShooter) {
            bCMotor.setPower(1.0);
        } else if (!forceStopAutoFeed) {
            bCMotor.setPower(0.0);
        }
    }
}
