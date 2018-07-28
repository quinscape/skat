/**
 * Debug helper module that is a neutral function in normal operations.
 *
 * Acts as a target for proxyquire operations.
 *
 * @param {*} input         input that will be returned
 * @param {*} [qualifier]   qualifier for this call
 * @return {*}  returns normally the input, might be mocked for tests.
 */
export default function(input, qualifier)
{
    return input;
}
