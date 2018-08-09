import Vector from "./vector"

const bzv0 = new Vector();
const bzv1 = new Vector();

function bezierPoint(vector, x0, y0, x1, y1, t)
{
    const x = x0 + (x1 - x0) * t;
    const y = y0 + (y1 - y0) * t;

    vector.x = x;
    vector.y = y;

    return vector;
}

function bezierPointV(vector, v0, v1, t)
{
    const x0 = v0.x;
    const y0 = v0.y;
    const x = x0 + (v1.x - x0) * t;
    const y = y0 + (v1.y - y0) * t;

    vector.x = x;
    vector.y = y;

    return vector;
}

/**
 * Calculate a point on a quadratic bezier curve.
 *
 * @param {number} x0           curve start x-coordinate
 * @param {number} y0           curve start y-coordinate
 * @param {number} x1           control point x-coordinate
 * @param {number} y1           control point y-coordinate
 * @param {number} x2           curve end x-coordinate
 * @param {number} y2           curve end y-coordinate
 * @param {number} current      position on the curve (0...1)
 * @param {Vector} [out]        Vector instance the ouput is written to
 * @return {*}
 */
function quadraticBezier(x0, y0, x1, y1, x2, y2, current, out = new Vector())
{
    const a = bezierPoint(bzv0, x0, y0, x1, y1, current);
    const b = bezierPoint(bzv1, x1, y1, x2, y2, current);

    return bezierPointV(out, a, b, current);
}

export default quadraticBezier;
