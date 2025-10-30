package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.systems.DriveTrain;
import org.firstinspires.ftc.teamcode.systems.OperationSubsystem;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="6128 DECODE Autonomous", group="OpMode")
@Config
public class Autonomous extends LinearOpMode{

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        final DriveTrain dS = new DriveTrain(hardwareMap);
        final OperationSubsystem oS = new OperationSubsystem(hardwareMap);

        double deltaTime = 0.0;
        double timeThreeArtsShot = -1;

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        telemetry.addData("Status", "Running");
        telemetry.update();

        oS.shooterTargetPower = 0.9;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            deltaTime = runtime.milliseconds();

            if (deltaTime >= 1000 && deltaTime < 1750) {
                dS.forward = -1.0;
            } else if (deltaTime >= 1750 && deltaTime < 2000) {
                dS.forward = 0;
            } else if (deltaTime >= 2000 && oS.artifactShotsDone < 3) {
                oS.runShooter = true;
            } else if (oS.artifactShotsDone >= 3 && timeThreeArtsShot < 0) {
                oS.runShooter = false;
                dS.forward = -1.0;
                timeThreeArtsShot = deltaTime;
            } else if (timeThreeArtsShot > 0 && deltaTime >= timeThreeArtsShot + 1000) {
                dS.forward = 0;
            }

            dS.Update();
            oS.Update();

            telemetry.addData("Shots Done", oS.artifactShotsDone);
            telemetry.update();
        }
    }
}