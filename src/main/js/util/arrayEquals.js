/**
 * Shallow array equality function. Two arrays are equal if they have the same length and all elements are === equal.
 *
 * @param {Array<*>}a  array a
 * @param {Array<*>}b  array b
 * 
 * @return {boolean} true if equal
 */
export default function(a,b)
{
    if (!Array.isArray(a))
    {
        throw new Error("a is must be an Array")
    }

    if (!Array.isArray(b))
    {
        throw new Error("b is must be an Array")
    }

    if (a.length !== b.length)
    {
        return false;
    }

    for (let i = 0; i < a.length; i++)
    {
        if (a[i] !== b[i])
        {
            return false;
        }
    }
    return true;
}
