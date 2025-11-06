package org.firstinspires.ftc.teamcode.systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class OperationSubsystem {
    public final DcMotorEx shooterMotor;
    public final DcMotor beltMotor;
    public final DcMotor collectorMotor;
    public static double shooterTargetPower;
    public static double shooterVelocityMinRange;
    public boolean runShooter;
    public boolean runBC;
    public boolean runBelt;
    public boolean runCollector;
    public boolean emptyBarrel;
    public boolean forceStopAutoFeed;

    public OperationSubsystem(HardwareMap hardwareMap) {
        // Set shooter wheel and belt-collector wheel motors
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shoot");
        beltMotor = hardwareMap.get(DcMotorEx.class, "belt");
        collectorMotor = hardwareMap.get(DcMotorEx.class, "collect");

        // Set important variables
        shooterTargetPower = 0.75;
        shooterVelocityMinRange = 100;
        forceStopAutoFeed = false;
        emptyBarrel = false;
        runBC = false;
        runBelt = false;
        runCollector = false;
        runShooter = false;
    }

    public void Update() {
        // Backwards for when the artifacts are in the way of the shooter
        if (emptyBarrel) {
            shooterMotor.setVelocity(-1000); // -50% of the max velocity (2000)
            beltMotor.setPower(-0.5);
            collectorMotor.setPower(0.2); // Power is positive to stop the artifacts from rolling out the back
        }

        // Spin shooter wheel
        if (runShooter) {
            shooterMotor.setPower(shooterTargetPower);
        } else if (!emptyBarrel) {
            shooterMotor.setPower(0.0);
        }

        // Spin belt-collector wheel
        if (runBelt) {
            beltMotor.setPower(0.5);
        } else if (runCollector) {
            collectorMotor.setPower(0.5);
        } else if (runBC) {
            beltMotor.setPower(0.5);
            collectorMotor.setPower(0.5);
        }
        // Auto run the belt if it is at max speed
        else if ((shooterTargetPower * 2000) - shooterMotor.getVelocity() < shooterVelocityMinRange && !forceStopAutoFeed && runShooter) {
            beltMotor.setPower(1.0);
            collectorMotor.setPower(0.2);
        } else if (!forceStopAutoFeed) {
            beltMotor.setPower(0.0);
            collectorMotor.setPower(0.2);
        }
    }
}
