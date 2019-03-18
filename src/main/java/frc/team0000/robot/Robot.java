package frc.team0000.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team0000.robot.state.StateTracker;
import frc.team0000.robot.vision.VisionProcessor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

    private static final String kDefaultAuto = "Default";
    private static final String kCustomAuto = "My Auto";

    private XboxController controller;
    private DifferentialDrive drive;

    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();

    private StateTracker robotState = new StateTracker();

    private VisionProcessor vision;

    @Override
    public void teleopInit() {
        super.teleopInit();
    }

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
        m_chooser.addOption("My Auto", kCustomAuto);
        SmartDashboard.putData("Auto choices", m_chooser);

        Talon leftTalon = new Talon(0);
        Talon rightTalon = new Talon(1);
        drive = new DifferentialDrive(leftTalon, rightTalon);
        controller = new XboxController(0);

        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(640, 480);
        vision = VisionProcessor.getInstance(camera);
    }

    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        // TODO: Log things
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional comparisons to
     * the switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */
    @Override
    public void autonomousInit() {
        m_autoSelected = m_chooser.getSelected();
        // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
        System.out.println("Auto selected: " + m_autoSelected);
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        switch (m_autoSelected) {
            case kCustomAuto:
                // Put custom auto code here
                break;
            case kDefaultAuto:
            default:
                // Put default auto code here
                break;
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        handleDriving();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }

    private void handleDriving() {
        boolean turboToggled = controller.getBButton();
        if (turboToggled) {
            robotState.toggleTurboMode(!robotState.isInTurboMode());
        }
        double leftY = controller.getY(GenericHID.Hand.kLeft);
        double rightY = controller.getY(GenericHID.Hand.kRight);
        System.out.printf("Left input: %s; Right input: %s\n", leftY, rightY);
        double leftMovement;
        double rightMovement;
        if (robotState.isInTurboMode()) {
            leftMovement = leftY * 1.05;
            rightMovement = rightY * 1.05;
        } else {
            leftMovement = leftY * 1.33;
            rightMovement = rightY * 1.33;
        }
        drive.tankDrive(leftMovement, rightMovement);
    }

    private static double smoothInput(double input) {
        return Math.log(input);
    }
}