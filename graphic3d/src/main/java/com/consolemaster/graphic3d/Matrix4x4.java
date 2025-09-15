package com.consolemaster.graphic3d;

import lombok.Data;

/**
 * Represents a 4x4 transformation matrix for 3D operations.
 * Used for rotation, translation, scaling, and projection.
 */
@Data
public class Matrix4x4 {
    private final double[][] matrix = new double[4][4];

    public Matrix4x4() {
        identity();
    }

    /**
     * Sets this matrix to the identity matrix.
     */
    public Matrix4x4 identity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }
        return this;
    }

    /**
     * Creates a translation matrix.
     */
    public static Matrix4x4 translation(double x, double y, double z) {
        Matrix4x4 result = new Matrix4x4();
        result.matrix[0][3] = x;
        result.matrix[1][3] = y;
        result.matrix[2][3] = z;
        return result;
    }

    /**
     * Creates a rotation matrix around the X axis.
     */
    public static Matrix4x4 rotationX(double angle) {
        Matrix4x4 result = new Matrix4x4();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        result.matrix[1][1] = cos;
        result.matrix[1][2] = -sin;
        result.matrix[2][1] = sin;
        result.matrix[2][2] = cos;
        return result;
    }

    /**
     * Creates a rotation matrix around the Y axis.
     */
    public static Matrix4x4 rotationY(double angle) {
        Matrix4x4 result = new Matrix4x4();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        result.matrix[0][0] = cos;
        result.matrix[0][2] = sin;
        result.matrix[2][0] = -sin;
        result.matrix[2][2] = cos;
        return result;
    }

    /**
     * Creates a rotation matrix around the Z axis.
     */
    public static Matrix4x4 rotationZ(double angle) {
        Matrix4x4 result = new Matrix4x4();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        result.matrix[0][0] = cos;
        result.matrix[0][1] = -sin;
        result.matrix[1][0] = sin;
        result.matrix[1][1] = cos;
        return result;
    }

    /**
     * Creates a scaling matrix.
     */
    public static Matrix4x4 scaling(double x, double y, double z) {
        Matrix4x4 result = new Matrix4x4();
        result.matrix[0][0] = x;
        result.matrix[1][1] = y;
        result.matrix[2][2] = z;
        return result;
    }

    /**
     * Creates a perspective projection matrix.
     */
    public static Matrix4x4 perspective(double fov, double aspect, double near, double far) {
        Matrix4x4 result = new Matrix4x4();
        double f = 1.0 / Math.tan(fov / 2.0);
        result.matrix[0][0] = f / aspect;
        result.matrix[1][1] = f;
        result.matrix[2][2] = (far + near) / (near - far);
        result.matrix[2][3] = (2 * far * near) / (near - far);
        result.matrix[3][2] = -1;
        result.matrix[3][3] = 0;
        return result;
    }

    /**
     * Multiplies this matrix with another matrix.
     */
    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.matrix[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result.matrix[i][j] += matrix[i][k] * other.matrix[k][j];
                }
            }
        }
        return result;
    }

    /**
     * Transforms a 3D point using this matrix.
     */
    public Point3D transform(Point3D point) {
        double x = matrix[0][0] * point.getX() + matrix[0][1] * point.getY() + matrix[0][2] * point.getZ() + matrix[0][3];
        double y = matrix[1][0] * point.getX() + matrix[1][1] * point.getY() + matrix[1][2] * point.getZ() + matrix[1][3];
        double z = matrix[2][0] * point.getX() + matrix[2][1] * point.getY() + matrix[2][2] * point.getZ() + matrix[2][3];
        double w = matrix[3][0] * point.getX() + matrix[3][1] * point.getY() + matrix[3][2] * point.getZ() + matrix[3][3];

        // Perspective divide
        if (w != 0) {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Point3D(x, y, z);
    }
}
