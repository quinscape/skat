import config from "../services/config"


/**
 * Logs graphql errors
 * @param errors
 */
export function defaultErrorHandler(errors)
{
    console.error("GraphQL Request failed");
    console.table(errors);
}

/**
 * GraphQL query service
 *
 * @param {Object} params               Parameters
 * @param {String} params.query         query string
 * @param {Object} [params.variables]   query variables
 *
 * @returns {Promise<Object>} Promise resolving to query data
 */
export default function (params) {

    //console.log("QUERY: ", params);

    const { csrfToken, contextPath, windowId } = config();


    if (params.query.indexOf("$windowId") > 0)
    {
        params.variables = {
            ... params.variables,
            windowId
        };
    }

    return fetch(
            window.location.origin + contextPath + "/graphql",
            {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "Content-Type": "application/json",

                    // spring security enforces every POST request to carry a csrf token as either parameter or header
                    [csrfToken.header] : csrfToken.value
                },
                body: JSON.stringify(params)
            }
        )
        .then(response => response.json())
        .then(
            ({ data, errors}) => {
                if (errors)
                {
                    return  Promise.reject(errors);
                }

                return data;
            }
        );
}
