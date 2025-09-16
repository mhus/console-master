package com.consolemaster.graphic3d;

import lombok.Data;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Represents a 4x4 transformation matrix for 3D operations using BigDecimal for precision.
 * Used for rotation, translation, scaling, and projection.
 */
@Data
public class Matrix4x4 {
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1);

    private final BigDecimal[][] matrix = new BigDecimal[4][4];

    public Matrix4x4() {
        identity();
    }

    /**
     * Sets this matrix to the identity matrix.
     */
    public Matrix4x4 identity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = (i == j) ? ONE : ZERO;
            }
        }
        return this;
    }

    /**
     * Creates a translation matrix.
     */
    public static Matrix4x4 translation(BigDecimal x, BigDecimal y, BigDecimal z) {
        Matrix4x4 result = new Matrix4x4();
        result.matrix[0][3] = x;
        result.matrix[1][3] = y;
        result.matrix[2][3] = z;
        return result;
    }

    /**
     * Creates a translation matrix (double for compatibility).
     */
    public static Matrix4x4 translation(double x, double y, double z) {
        return translation(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(z));
    }

    /**
     * Creates a rotation matrix around the X axis.
     */
    public static Matrix4x4 rotationX(BigDecimal angle) {
        Matrix4x4 result = new Matrix4x4();
        BigDecimal cos = cos(angle);
        BigDecimal sin = sin(angle);
        result.matrix[1][1] = cos;
        result.matrix[1][2] = sin.negate();
        result.matrix[2][1] = sin;
        result.matrix[2][2] = cos;
        return result;
    }

    /**
     * Creates a rotation matrix around the X axis (double for compatibility).
     */
    public static Matrix4x4 rotationX(double angle) {
        return rotationX(BigDecimal.valueOf(angle));
    }

    /**
     * Creates a rotation matrix around the Y axis.
     */
    public static Matrix4x4 rotationY(BigDecimal angle) {
        Matrix4x4 result = new Matrix4x4();
        BigDecimal cos = cos(angle);
        BigDecimal sin = sin(angle);
        result.matrix[0][0] = cos;
        result.matrix[0][2] = sin;
        result.matrix[2][0] = sin.negate();
        result.matrix[2][2] = cos;
        return result;
    }

    /**
     * Creates a rotation matrix around the Y axis (double for compatibility).
     */
    public static Matrix4x4 rotationY(double angle) {
        return rotationY(BigDecimal.valueOf(angle));
    }

    /**
     * Creates a rotation matrix around the Z axis.
     */
    public static Matrix4x4 rotationZ(BigDecimal angle) {
        Matrix4x4 result = new Matrix4x4();
        BigDecimal cos = cos(angle);
        BigDecimal sin = sin(angle);
        result.matrix[0][0] = cos;
        result.matrix[0][1] = sin.negate();
        result.matrix[1][0] = sin;
        result.matrix[1][1] = cos;
        return result;
    }

    /**
     * Creates a rotation matrix around the Z axis (double for compatibility).
     */
    public static Matrix4x4 rotationZ(double angle) {
        return rotationZ(BigDecimal.valueOf(angle));
    }

    /**
     * Creates a scaling matrix.
     */
    public static Matrix4x4 scaling(BigDecimal x, BigDecimal y, BigDecimal z) {
        Matrix4x4 result = new Matrix4x4();
        result.matrix[0][0] = x;
        result.matrix[1][1] = y;
        result.matrix[2][2] = z;
        return result;
    }

    /**
     * Creates a scaling matrix (double for compatibility).
     */
    public static Matrix4x4 scaling(double x, double y, double z) {
        return scaling(BigDecimal.valueOf(x), BigDecimal.valueOf(y), BigDecimal.valueOf(z));
    }

    /**
     * Creates a perspective projection matrix.
     */
    public static Matrix4x4 perspective(BigDecimal fov, BigDecimal aspect, BigDecimal near, BigDecimal far) {
        Matrix4x4 result = new Matrix4x4();
        BigDecimal f = ONE.divide(tan(fov.divide(TWO, MATH_CONTEXT)), MATH_CONTEXT);
        result.matrix[0][0] = f.divide(aspect, MATH_CONTEXT);
        result.matrix[1][1] = f;
        result.matrix[2][2] = far.add(near, MATH_CONTEXT).divide(near.subtract(far, MATH_CONTEXT), MATH_CONTEXT);
        result.matrix[2][3] = TWO.multiply(far, MATH_CONTEXT).multiply(near, MATH_CONTEXT).divide(near.subtract(far, MATH_CONTEXT), MATH_CONTEXT);
        result.matrix[3][2] = MINUS_ONE;
        result.matrix[3][3] = ZERO;
        return result;
    }

    /**
     * Creates a perspective projection matrix (double for compatibility).
     */
    public static Matrix4x4 perspective(double fov, double aspect, double near, double far) {
        return perspective(BigDecimal.valueOf(fov), BigDecimal.valueOf(aspect), BigDecimal.valueOf(near), BigDecimal.valueOf(far));
    }

    /**
     * Multiplies this matrix with another matrix.
     */
    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.matrix[i][j] = ZERO;
                for (int k = 0; k < 4; k++) {
                    result.matrix[i][j] = result.matrix[i][j].add(
                        matrix[i][k].multiply(other.matrix[k][j], MATH_CONTEXT), MATH_CONTEXT);
                }
            }
        }
        return result;
    }

    /**
     * Transforms a 3D point using this matrix.
     */
    public Point3D transform(Point3D point) {
        BigDecimal x = matrix[0][0].multiply(point.getX(), MATH_CONTEXT)
            .add(matrix[0][1].multiply(point.getY(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[0][2].multiply(point.getZ(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[0][3], MATH_CONTEXT);

        BigDecimal y = matrix[1][0].multiply(point.getX(), MATH_CONTEXT)
            .add(matrix[1][1].multiply(point.getY(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[1][2].multiply(point.getZ(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[1][3], MATH_CONTEXT);

        BigDecimal z = matrix[2][0].multiply(point.getX(), MATH_CONTEXT)
            .add(matrix[2][1].multiply(point.getY(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[2][2].multiply(point.getZ(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[2][3], MATH_CONTEXT);

        BigDecimal w = matrix[3][0].multiply(point.getX(), MATH_CONTEXT)
            .add(matrix[3][1].multiply(point.getY(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[3][2].multiply(point.getZ(), MATH_CONTEXT), MATH_CONTEXT)
            .add(matrix[3][3], MATH_CONTEXT);

        // Perspective divide
        if (w.compareTo(ZERO) != 0) {
            x = x.divide(w, MATH_CONTEXT);
            y = y.divide(w, MATH_CONTEXT);
            z = z.divide(w, MATH_CONTEXT);
        }

        return new Point3D(x, y, z);
    }

    /**
     * Helper method to calculate cosine using Taylor series.
     */
    private static BigDecimal cos(BigDecimal x) {
        return BigDecimal.valueOf(Math.cos(x.doubleValue()));
    }

    /**
     * Helper method to calculate sine using Taylor series.
     */
    private static BigDecimal sin(BigDecimal x) {
        return BigDecimal.valueOf(Math.sin(x.doubleValue()));
    }

    /**
     * Helper method to calculate tangent.
     */
    private static BigDecimal tan(BigDecimal x) {
        return BigDecimal.valueOf(Math.tan(x.doubleValue()));
    }

    // Getter for matrix element
    public BigDecimal get(int row, int col) {
        return matrix[row][col];
    }

    // Setter for matrix element
    public void set(int row, int col, BigDecimal value) {
        matrix[row][col] = value;
    }

    // Convenience setter for double values
    public void set(int row, int col, double value) {
        matrix[row][col] = BigDecimal.valueOf(value);
    }
}
