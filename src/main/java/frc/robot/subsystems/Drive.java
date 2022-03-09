package frc.robot.subsystems;

import static frc.robot.Constants.Drive.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.filter.SlewRateLimiter;
// import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.SPI;

public class Drive extends SubsystemBase {
  private DifferentialDrive driveBase;

  public WPI_TalonFX[] motors;
  // new WPI_TalonFX(0), // Left Front
  // new WPI_TalonFX(1), // Left Back
  // new WPI_TalonFX(2), // Right Front
  // new WPI_TalonFX(3) // Right Back

  private MotorControllerGroup leftDrive;
  private MotorControllerGroup rightDrive;

  private AHRS navx;

  private int mode = 1;

  private SlewRateLimiter leftRamp = new SlewRateLimiter(0.5);
  private SlewRateLimiter rightRamp = new SlewRateLimiter(0.5);

  // private double previousAngle = 0;

  // private final PIDController pid = new PIDController(0.01, 0, 0);

  public Drive() {
    motors = new WPI_TalonFX[4];

    for (int i = 0; i < 4; i++) {
      motors[i] = new WPI_TalonFX(i);
      motors[i].configOpenloopRamp(0);
      motors[i].setNeutralMode(NeutralMode.Coast);
    }

    leftDrive = new MotorControllerGroup(motors[0], motors[1]);
    rightDrive = new MotorControllerGroup(motors[2], motors[3]);

    leftDrive.setInverted(false);
    rightDrive.setInverted(true);

    driveBase = new DifferentialDrive(leftDrive, rightDrive);

    navx = new AHRS(SPI.Port.kMXP);
  }

  public void drive(double leftY, double rightY, double rightX) {
    double driftCompensation = 0;
    // double angle = navx.getAngle();
    // if (rightX < 0.1 && rightX > -0.1) { // driving forward
    // rightX = 0;
    // driftCompensation = pid.calculate(angle, previousAngle);
    // } else previousAngle = angle; // turning

    rightX = Math.max(-kTurnThreshold, Math.min(kTurnThreshold, Math.pow(rightX, 3) * kTurningSensitivity));

    // rightX = Math.pow(rightX, 5) / Math.abs(Math.pow(rightX, 3));

    double leftSpeed = mode == 0 ? leftY : leftY - rightX;
    double rightSpeed = mode == 0 ? rightY : leftY + rightX;

    leftSpeed = Math.max(-kForwardThreshold, Math.min(kForwardThreshold, leftSpeed));
    rightSpeed = Math.max(-kForwardThreshold, Math.min(kForwardThreshold, rightSpeed));

    // System.out.println(leftSpeed);
    // System.out.println(rightSpeed);

    driveBase.tankDrive(leftRamp.calculate(leftSpeed + driftCompensation), rightRamp.calculate(rightSpeed));
  }

  public void brake() {
    driveBase.tankDrive(0, 0);
    leftRamp.reset(0);
    rightRamp.reset(0);
  }

  public void switchMode() {
    mode = Math.abs(mode - 1);
    System.out.println("Switched to: " + mode);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("navx angle", navx.getAngle());
  }
}
