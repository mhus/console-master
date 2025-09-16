package com.consolemaster.graphic3d;

import lombok.Data;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Represents a 3D camera with position, rotation, and projection settings using BigDecimal for precision.
 */
@Data
public class Camera3D {
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal PI_OVER_4 = PI.divide(BigDecimal.valueOf(4), MATH_CONTEXT);

    private Point3D position;
    private Point3D rotation; // Euler angles in radians
    private BigDecimal fov; // Field of view in radians
    private BigDecimal near;
    private BigDecimal far;

    public Camera3D() {
        this.position = new Point3D(0, 0, 5);
        this.rotation = new Point3D(0, 0, 0);
        this.fov = PI_OVER_4; // 45 degrees
        this.near = BigDecimal.valueOf(0.1);
        this.far = BigDecimal.valueOf(100.0);
    }

    public Camera3D(Point3D position, Point3D rotation) {
        this();
        this.position = position;
        this.rotation = rotation;
    }

    // Double constructor for compatibility
    public Camera3D(Point3D position, Point3D rotation, double fov, double near, double far) {
        this(position, rotation);
        this.fov = BigDecimal.valueOf(fov);
        this.near = BigDecimal.valueOf(near);
        this.far = BigDecimal.valueOf(far);
    }

    // BigDecimal constructor
    public Camera3D(Point3D position, Point3D rotation, BigDecimal fov, BigDecimal near, BigDecimal far) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    /**
     * Creates the view matrix for this camera.
     */
    public Matrix4x4 getViewMatrix() {
        // Create rotation matrices for each axis
        Matrix4x4 rotX = Matrix4x4.rotationX(rotation.getX().negate());
        Matrix4x4 rotY = Matrix4x4.rotationY(rotation.getY().negate());
        Matrix4x4 rotZ = Matrix4x4.rotationZ(rotation.getZ().negate());

        // Combine rotations
        Matrix4x4 rotationMatrix = rotZ.multiply(rotY).multiply(rotX);

        // Create translation matrix
        Matrix4x4 translation = Matrix4x4.translation(
            position.getX().negate(),
            position.getY().negate(),
            position.getZ().negate()
        );

        // Combine: first translate, then rotate
        return rotationMatrix.multiply(translation);
    }

    /**
     * Creates the projection matrix for this camera.
     */
    public Matrix4x4 getProjectionMatrix(BigDecimal aspectRatio) {
        return Matrix4x4.perspective(fov, aspectRatio, near, far);
    }

    /**
     * Creates the projection matrix for this camera (double for compatibility).
     */
    public Matrix4x4 getProjectionMatrix(double aspectRatio) {
        return getProjectionMatrix(BigDecimal.valueOf(aspectRatio));
    }

    /**
     * Creates the combined view-projection matrix.
     */
    public Matrix4x4 getViewProjectionMatrix(BigDecimal aspectRatio) {
        return getProjectionMatrix(aspectRatio).multiply(getViewMatrix());
    }

    /**
     * Creates the combined view-projection matrix (double for compatibility).
     */
    public Matrix4x4 getViewProjectionMatrix(double aspectRatio) {
        return getViewProjectionMatrix(BigDecimal.valueOf(aspectRatio));
    }

    /**
     * Moves the camera forward/backward along its local Z axis.
     */
    public void moveForward(BigDecimal distance) {
        // Calculate forward vector based on rotation
        BigDecimal cosY = cos(rotation.getY());
        BigDecimal sinY = sin(rotation.getY());
        BigDecimal cosX = cos(rotation.getX());
        BigDecimal sinX = sin(rotation.getX());

        Point3D forward = new Point3D(
            sinY.multiply(cosX, MATH_CONTEXT),
            sinX.negate(),
            cosY.multiply(cosX, MATH_CONTEXT)
        );

        position = position.add(forward.multiply(distance));
    }

    /**
     * Moves the camera forward/backward along its local Z axis (double for compatibility).
     */
    public void moveForward(double distance) {
        moveForward(BigDecimal.valueOf(distance));
    }

    /**
     * Moves the camera left/right along its local X axis.
     */
    public void moveRight(BigDecimal distance) {
        BigDecimal cosY = cos(rotation.getY());
        BigDecimal sinY = sin(rotation.getY());

        Point3D right = new Point3D(cosY, BigDecimal.ZERO, sinY.negate());
        position = position.add(right.multiply(distance));
    }

    /**
     * Moves the camera left/right along its local X axis (double for compatibility).
     */
    public void moveRight(double distance) {
        moveRight(BigDecimal.valueOf(distance));
    }

    /**
     * Moves the camera up/down along its local Y axis.
     */
    public void moveUp(BigDecimal distance) {
        BigDecimal cosY = cos(rotation.getY());
        BigDecimal sinY = sin(rotation.getY());
        BigDecimal cosX = cos(rotation.getX());
        BigDecimal sinX = sin(rotation.getX());

        Point3D up = new Point3D(
            sinY.negate().multiply(sinX, MATH_CONTEXT),
            cosX,
            cosY.negate().multiply(sinX, MATH_CONTEXT)
        );

        position = position.add(up.multiply(distance));
    }

    /**
     * Moves the camera up/down along its local Y axis (double for compatibility).
     */
    public void moveUp(double distance) {
        moveUp(BigDecimal.valueOf(distance));
    }

    /**
     * Rotates the camera around its local axes.
     */
    public void rotate(BigDecimal deltaX, BigDecimal deltaY, BigDecimal deltaZ) {
        rotation = new Point3D(
            rotation.getX().add(deltaX, MATH_CONTEXT),
            rotation.getY().add(deltaY, MATH_CONTEXT),
            rotation.getZ().add(deltaZ, MATH_CONTEXT)
        );
    }

    /**
     * Rotates the camera around its local axes (double for compatibility).
     */
    public void rotate(double deltaX, double deltaY, double deltaZ) {
        rotate(BigDecimal.valueOf(deltaX), BigDecimal.valueOf(deltaY), BigDecimal.valueOf(deltaZ));
    }

    /**
     * Looks at a specific point in 3D space.
     */
    public void lookAt(Point3D target) {
        Point3D direction = target.subtract(position).normalize();

        // Calculate yaw (Y rotation)
        BigDecimal yaw = atan2(direction.getX(), direction.getZ());

        // Calculate pitch (X rotation)
        BigDecimal pitch = asin(direction.getY().negate());

        rotation = new Point3D(pitch, yaw, BigDecimal.ZERO);
    }

    // Helper methods for trigonometric functions
    private static BigDecimal cos(BigDecimal x) {
        return BigDecimal.valueOf(Math.cos(x.doubleValue()));
    }

    private static BigDecimal sin(BigDecimal x) {
        return BigDecimal.valueOf(Math.sin(x.doubleValue()));
    }

    private static BigDecimal atan2(BigDecimal y, BigDecimal x) {
        return BigDecimal.valueOf(Math.atan2(y.doubleValue(), x.doubleValue()));
    }

    private static BigDecimal asin(BigDecimal x) {
        return BigDecimal.valueOf(Math.asin(x.doubleValue()));
    }

    // Convenience methods for double compatibility
    public double getFovAsDouble() {
        return fov.doubleValue();
    }

    public double getNearAsDouble() {
        return near.doubleValue();
    }

    public double getFarAsDouble() {
        return far.doubleValue();
    }
}
