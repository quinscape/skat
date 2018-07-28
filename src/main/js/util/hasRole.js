/**
 * Helper function to evaluate the current authentication context to have a certain role.
 *
 * @param name      role name
 */
import config from "../services/config";


export default function(name)
{
    if (!name || typeof name !== "string")
    {
        throw new Error("Need a role name string");
    }

    const authentication = config().authentication;

    if (!authentication)
    {
        throw new Error("No authentication context");
    }

    const roles = authentication.roles;
    for (let i = 0, len = roles.length; i < len; i++)
    {
        const role = roles[i];
        if (role === name)
        {
            return true;
        }
    }
    return false;
}
