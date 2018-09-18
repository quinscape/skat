import intercept from "../util/intercept"


const isDev = intercept(process.env.NODE_ENV !== "production") &&
    typeof Proxy === "function" &&
    typeof Object.freeze === "function";

// default config without initConfig() call
let config, frozenProxy;

export function __initConfig(initial, keys = null)
{
    config = {
        names: []
    };


    if (keys)
    {
        for (let i = 0; i < keys.length; i++)
        {
            const name = keys[i];
            config[name] = initial[name];
            config.names.push(name);
        }
    }
    else
    {
        for (let name in initial)
        {
            if (initial.hasOwnProperty(name))
            {
                config[name] = initial[name];
                config.names.push(name);
            }
        }
    }

    if (isDev)
    {
        const frozen = require("deep-freeze")(config);

        frozenProxy = new Proxy(frozen, {
            get: function (config, property) {
                if (property in config)
                {
                    return config[property];
                }
                else
                {
                    throw new ReferenceError("Invalid config name '" + property + "'");
                }
            }
        });
    }
}

/**
 * Config holder service holding an unchangeable slice of the initial view data.
 *
 * This is an alternative to routing everything through redux.
 * For unchangeable, often needed basic things, this approach seems preferrable than having to connect() everything.
 *
 * @return {*}
 */
export default function () {
    return isDev ? frozenProxy : config;
}

