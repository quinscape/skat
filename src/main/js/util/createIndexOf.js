/**
 * @callback IndexOfPredicate
 * @param {array}   array to find in
 * @param {*}       second arg to predicate
 */

/**
 * Creates a indexOf() like function based on a binary predicate function
 *
 * @param {IndexOfPredicate} predicate     arra
 * @return {IndexOfPredicate}
 */
export default function (predicate) {
    return (array, arg) => {
        for (let i = 0; i < array.length; i++)
        {
            const elem = array[i];
            if (predicate(elem, arg))
            {
                return i;
            }
        }
        return -1;
    }
}
