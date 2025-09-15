package com.consolemaster.graphic3d;

import lombok.Data;

/**
 * Represents a 3D camera with position, rotation, and projection settings.
 */
@Data
public class Camera3D {
    private Point3D position;
    private Point3D rotation; // Euler angles in radians
    private double fov; // Field of view in radians
    private double near;
    private double far;

    public Camera3D() {
        this.position = new Point3D(0, 0, 5);
        this.rotation = new Point3D(0, 0, 0);
        this.fov = Math.PI / 4; // 45 degrees
        this.near = 0.1;
        this.far = 100.0;
    }

    public Camera3D(Point3D position, Point3D rotation) {
        this();
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Creates the view matrix for this camera.
     */
    public Matrix4x4 getViewMatrix() {
        // Create rotation matrices for each axis
        Matrix4x4 rotX = Matrix4x4.rotationX(-rotation.getX());
        Matrix4x4 rotY = Matrix4x4.rotationY(-rotation.getY());
        Matrix4x4 rotZ = Matrix4x4.rotationZ(-rotation.getZ());

        // Combine rotations
        Matrix4x4 rotation = rotZ.multiply(rotY).multiply(rotX);

        // Create translation matrix
        Matrix4x4 translation = Matrix4x4.translation(-position.getX(), -position.getY(), -position.getZ());

        // Combine: first translate, then rotate
        return rotation.multiply(translation);
    }

    /**
     * Creates the projection matrix for this camera.
     */
    public Matrix4x4 getProjectionMatrix(double aspectRatio) {
        return Matrix4x4.perspective(fov, aspectRatio, near, far);
    }

    /**
     * Creates the combined view-projection matrix.
     */
    public Matrix4x4 getViewProjectionMatrix(double aspectRatio) {
        return getProjectionMatrix(aspectRatio).multiply(getViewMatrix());
    }

    /**
     * Moves the camera forward/backward along its local Z axis.
     */
    public void moveForward(double distance) {
        // Calculate forward vector based on rotation
        double cosY = Math.cos(rotation.getY());
        double sinY = Math.sin(rotation.getY());
        double cosX = Math.cos(rotation.getX());
        double sinX = Math.sin(rotation.getX());

        Point3D forward = new Point3D(
            sinY * cosX,
            -sinX,
            cosY * cosX
        );

        position = position.add(forward.multiply(distance));
    }

    /**
     * Moves the camera left/right along its local X axis.
     */
    public void moveRight(double distance) {
        double cosY = Math.cos(rotation.getY());
        double sinY = Math.sin(rotation.getY());

        Point3D right = new Point3D(cosY, 0, -sinY);
        position = position.add(right.multiply(distance));
    }

    /**
     * Moves the camera up/down along its local Y axis.
     */
    public void moveUp(double distance) {
        double cosY = Math.cos(rotation.getY());
        double sinY = Math.sin(rotation.getY());
        double cosX = Math.cos(rotation.getX());
        double sinX = Math.sin(rotation.getX());

        Point3D up = new Point3D(
            -sinY * sinX,
            cosX,
            -cosY * sinX
        );

        position = position.add(up.multiply(distance));
    }

    /**
     * Rotates the camera around its local axes.
     */
    public void rotate(double deltaX, double deltaY, double deltaZ) {
        rotation = new Point3D(
            rotation.getX() + deltaX,
            rotation.getY() + deltaY,
            rotation.getZ() + deltaZ
        );
    }

    /**
     * Looks at a specific point in 3D space.
     */
    public void lookAt(Point3D target) {
        Point3D direction = target.subtract(position).normalize();

        // Calculate yaw (Y rotation)
        double yaw = Math.atan2(direction.getX(), direction.getZ());

        // Calculate pitch (X rotation)
        double pitch = Math.asin(-direction.getY());

        rotation = new Point3D(pitch, yaw, 0);
    }
}
