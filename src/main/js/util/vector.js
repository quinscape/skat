const Math_atan2 = Math.atan2;
const Math_sqrt = Math.sqrt;



/**
 * Vector geometry class. It uses a fluent mutable style for all methods returning Vector values. That is it modifies
 * the current vector in-place, mutable and return this for chaining.
 *
 * If you need immutability, work with well-place .copy() chaining
 *
 * ```js
 *  const v2 = v.copy().add(v2).add(v3).scale(l);
 *
 * ```
 * Optimizing the number of new Vector objects.
 *
 * Note that you can also use mutable module private variables for all your temporary values in your code, so with this
 * kind of instance control and the strictly single threaded nature of javascript,
 * you can set up zero-runtime-allocation mathematical systems.
 * 
 */
class Vector {
    constructor(x, y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the current value
     *
     * @param {number|Vector} x     x-coordinate
     * @param [y]                   y-coordinate
     * @return {Vector}
     */
    set(x, y)
    {
        if (y === undefined)
        {
            this.x = x.x;
            this.y = x.y;
        }
        else
        {
            this.x = x;
            this.y = y;
        }
        return this;
    }

    /**
     * Adds the current vector to the given vector
     *
     * @param {number|Vector} x     x-coordinate
     * @param [y]                   y-coordinate
     * @return {Vector}
     */
    add(x, y)
    {
        if (y === undefined)
        {
            this.x += x.x;
            this.y += x.y;
        }
        else
        {
            this.x += x;
            this.y += y;
        }
        return this;
    }


    /**
     * Substracts the given vector from the current vector
     *
     * @param {number|Vector} x     x-coordinate
     * @param [y]                   y-coordinate
     * @return {Vector}
     */
    subtract(x, y)
    {
        if (y === undefined)
        {
            this.x -= x.x;
            this.y -= x.y;
        }
        else
        {
            this.x -= x;
            this.y -= y;
        }
        return this;
    }

    /**
     * Scales the current vector by the given scalar value
     *
     * @param s     scalar value the coordinates are multiplied with
     * @return {Vector}
     */
    scale(s)
    {
        this.x *= s;
        this.y *= s;
        return this;
    }

    /**
     * Calculates the length of the current vector
     *
     * @return {number}
     */
    len()
    {
        return Math_sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Norms the vector to the given length
     *
     * @param len       length ( default is 1)
     *
     * @return {Vector}
     */
    norm(len = 1)
    {
        const invLen = len / this.len();
        return this.scale(invLen);
    }

    /**
     * Copies the current vector to a new instance.
     *
     * @return {Vector}
     */
    copy()
    {
        return new Vector(this.x, this.y);
    }

    /**
     * Returns a (x,y) representation
     * 
     * @return {string}
     */
    toString()
    {
        return "( " + this.x + ", " + this.y + ")";
    }

    /**
     * Dot product
     * @param v
     * @return {number}
     */
    dot(v)
    {
        return this.x * v.x + this.y * v.y;
    }

    /**
     * Projects the given vector onto the current vector and returns a new projected vector
     * @param b             other vector
     * @return {Vector}
     */
    projectOnto(b)
    {
        const dp = this.dot(b);
        return new Vector(( dp / (b.x * b.x + b.y * b.y) ) * b.x, ( dp / (b.x * b.x + b.y * b.y) ) * b.y);
    }

    /**
     * Angle to another vector
     *
     * @param v         other vector
     * @return {number}
     */
    angleTo(v)
    {
        const deltaX = this.x - v.x;
        const deltaY = this.y - v.y;
        return Math_atan2(deltaY, deltaX);
    }

    /**
     * Rotate the vector 90 degrees clockwise.
     *
     * @return {Vector}
     */
    rotateCW()
    {
        const h = -this.x;
        //noinspection JSSuspiciousNameCombination
        this.x = this.y;
        this.y = h;
        return this;
    }

    /**
     * Rotate the vector 90 degrees counter-clockwise.
     *
     * @return {Vector}
     */
    rotateCCW()
    {
        const h = this.x;
        //noinspection JSSuspiciousNameCombination
        this.x = -this.y;
        this.y = h;
        return this;
    }
}

export default Vector;
